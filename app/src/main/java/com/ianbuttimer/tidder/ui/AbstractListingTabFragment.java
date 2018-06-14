/*
 * Copyright (C) 2018  Ian Buttimer
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ianbuttimer.tidder.ui;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.adapter.AbstractRecycleViewAdapter;
import com.ianbuttimer.tidder.data.adapter.AbstractViewHolder;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.net.UriUtils;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.ListingList;
import com.ianbuttimer.tidder.reddit.ListingTracker;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.ui.widgets.EndlessRecyclerViewScrollListener;
import com.ianbuttimer.tidder.ui.widgets.ListItemClickListener;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import github.nisrulz.recyclerviewhelper.RVHItemDividerDecoration;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;
import timber.log.Timber;

import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.TWO_PANE;

/**
 * Base class for listing tab fragments
 */
@SuppressWarnings("unused")
public abstract class AbstractListingTabFragment<T extends BaseObject, K extends AbstractViewHolder>
                        extends Fragment implements PostOffice.IAddressable {

    protected static final String LIST = "list";
    protected static final String TRACKERS = "trackers";

    @Nullable @BindView(R.id.rv_list_listingL) RecyclerView rvList;
    @Nullable @BindView(R.id.pb_progress_listingL) ProgressBar pbProgress;
    @Nullable @BindView(R.id.tv_message_listingL) TextView tvMessage;

    @LayoutRes protected int mLayoutId;

    protected boolean mTwoPane;

    protected AbstractRecycleViewAdapter<T, K> mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<T> mList;
    protected EndlessRecyclerViewScrollListener mScrollListener;

    protected HashMap<String, ListingTracker<T>> mTrackers;

    protected ContentObserver mObserver;
    protected int mRegisterCount;
    protected Uri mObserverUri;

    public AbstractListingTabFragment(@LayoutRes int layoutId) {
        this(layoutId, null, null);
    }

    public AbstractListingTabFragment(@LayoutRes int layoutId, Uri observerUri, ContentObserver observer) {
        this.mLayoutId = layoutId;
        this.mObserverUri = observerUri;
        this.mObserver = observer;

        this.mRegisterCount = 0;
        this.mTwoPane = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(mLayoutId, container, false);

        bind(rootView);

        hideInProgressMessage();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        processSavedInstanceState(savedInstanceState);

        if (rvList != null) {
            Context context = getContext();
            mLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL,
                    false);
            rvList.setLayoutManager(mLayoutManager);

            mAdapter = getAdapter();

            rvList.setAdapter(mAdapter);


            // Setup onItemTouchHandler to enable drag and drop , swipe left or right
            ItemTouchHelper.Callback callback = new RVHItemTouchHelperCallback(
                    mAdapter, isLongPressDragEnabled(), isItemViewSwipeEnabledLeft(),
                    isItemViewSwipeEnabledRight());
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(rvList);

            // Set the divider in the recyclerview
            if (context != null) {
                rvList.addItemDecoration(new RVHItemDividerDecoration(context, LinearLayoutManager.VERTICAL));
            }

            // Set On Click Listener
            rvList.addOnItemTouchListener(getListItemClickListener());
        }
    }

    protected abstract void bind(View rootView);

    protected abstract AbstractRecycleViewAdapter<T, K> getAdapter();

    protected ListItemClickListener getListItemClickListener() {
        return new ListItemClickListener(getContext(), getIListItemClickTester());
    }

    protected ListItemClickListener.IListItemClickTester getIListItemClickTester() {
        return new ListItemClickListener.SimpleListItemClickTester(
                        mAdapter.getAdapterHandler(), true);    // always intercept
    }

    /**
     * Is long press drag enabled on list items. Default <code>false</code>.<br>
     * Override, returning <code>true</code> to enable long press drag on list items
     * @return long press drag enabled flag
     */
    protected boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * Is swipe left enabled on list items. Default <code>false</code>.<br>
     * Override, returning <code>true</code> to enable swipe left on list items
     * @return swipe left enabled flag
     */
    boolean isItemViewSwipeEnabledLeft() {
        return false;
    }


    /**
     * Is swipe right enabled on list items. Default <code>false</code>.<br>
     * Override, returning <code>true</code> to enable swipe right on list items
     * @return swipe right enabled flag
     */
    boolean isItemViewSwipeEnabledRight() {
        return false;
    }

    protected void processSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        if (savedInstanceState != null) {
            String key = makeBundleKey(LIST);
            if (savedInstanceState.containsKey(key)) {
                mList = Parcels.unwrap(savedInstanceState.getParcelable(key));
            }
            key = makeBundleKey(TRACKERS);
            if (savedInstanceState.containsKey(key)) {
                mTrackers = Parcels.unwrap(savedInstanceState.getParcelable(key));
            }
            key = TWO_PANE;
            if (savedInstanceState.containsKey(key)) {
                mTwoPane = savedInstanceState.getBoolean(key);
            }
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (mTrackers == null) {
            mTrackers = new HashMap<>();
        }
        readSavedInstanceState(savedInstanceState);
    }

    protected abstract void readSavedInstanceState(@Nullable Bundle savedInstanceState);

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if ((mList != null) && (mList.size() > 0)) {
            outState.putParcelable(makeBundleKey(LIST), Parcels.wrap(mList));
        }
        if ((mTrackers != null) && (mTrackers.size() > 0)) {
            outState.putParcelable(makeBundleKey(TRACKERS), Parcels.wrap(mTrackers));
        }
        outState.putBoolean(TWO_PANE, mTwoPane);
    }

    protected String makeBundleKey(String key) {
        return getClass().getSimpleName() + "_" + key;
    }

    @Override
    public void onStart() {
        super.onStart();

        Context context = getContext();
        if (shouldEventRegister()) {
            PostOffice.register(this);

            if ((context != null) && (mObserver != null) && UriUtils.actionable(mObserverUri)) {
                if (mRegisterCount == 0) {
                    context.getContentResolver().registerContentObserver(
                            mObserverUri,
                            false,
                            mObserver
                    );
                }
                ++mRegisterCount;
            }

            StandardEvent event = PostOffice.removeStickyEvent(StandardEvent.class);
            if (event != null) {
                // repost so subscriber that missed it can receive it
                postEvent(event);
            }
        }
    }

    @Override
    public void onStop() {
        Context context = getContext();
        if (shouldEventRegister()) {
            if ((context != null) && (mObserver != null)) {
                --mRegisterCount;
                if (mRegisterCount == 0) {
                    context.getContentResolver().unregisterContentObserver(mObserver);
                }
            }

            PostOffice.unregister(this);
        }

        super.onStop();
    }

    protected <E extends AbstractEvent> void postEvent(E event) {
        postEvent(event, getAddress());
    }

    protected <E extends AbstractEvent> void postEvent(E event, String... tags) {
        PostOffice.postEvent(event, tags);
    }

    protected <E extends AbstractEvent> void postSticky(E event) {
        postSticky(event, getAddress());
    }

    protected <E extends AbstractEvent> void postSticky(E event, String... tags) {
        PostOffice.postSticky(event, tags);
    }

    protected <E extends AbstractEvent> void postEventForActivity(E event) {
        postEventForActivity(event, getAddress());
    }

    protected <E extends AbstractEvent> void postEventForActivity(E event, String... tags) {
        PostOffice.postEvent(event.setAddress(getActivityTag()), tags);
    }

    protected <E extends AbstractEvent> void postStickyForActivity(E event) {
        postStickyForActivity(event, getAddress());
    }

    protected <E extends AbstractEvent> void postStickyForActivity(E event, String... tags) {
        PostOffice.postSticky(event.setAddress(getActivityTag()), tags);
    }

    protected String getActivityTag() {
        String tag = "";
        Activity activity = getActivity();
        if (activity != null) {
            tag = activity.getClass().getSimpleName();
        }
        return tag;
    }

    @Override
    public abstract String getAddress();

    protected boolean shouldEventRegister() {
        return true;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AbstractEvent event) {
         processMessageEvent(event);
    }

    protected abstract void processMessageEvent(AbstractEvent event);

    protected void showInProgress() {
        setInProgressMessageVisibility(View.INVISIBLE, View.VISIBLE);
    }

    protected void showMessage(String message) {
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
        setInProgressMessageVisibility(View.VISIBLE, View.INVISIBLE);
    }

    protected void showMessage(@StringRes int message) {
        showMessage(getString(message));
    }

    protected void hideInProgressMessage() {
        setInProgressMessageVisibility(View.INVISIBLE, View.INVISIBLE);
    }

    private void setInProgressMessageVisibility(int msgVisibility, int progressVisibility) {
        if (tvMessage != null) {
            tvMessage.setVisibility(msgVisibility);
        }
        if (pbProgress != null) {
            pbProgress.setVisibility(progressVisibility);
        }
    }

    protected ListingTracker<T> addTracker(String key, ListingTracker<T> tracker) {
        mTrackers.put(key, tracker);
        return tracker;
    }

    @Nullable protected ListingTracker<T> getTracker(String key) {
        return mTrackers.get(key);
    }

    protected void updateTrackerForward(String key, ListingList<T> list) {
        ListingTracker<T> tracker = getTracker(key);
        if (tracker != null) {
            tracker.updateForward(list);
            trackerDbg(key, tracker);
        }
    }

    protected void updateTrackerBackward(String key, ListingList<T> list) {
        ListingTracker<T> tracker = getTracker(key);
        if (tracker != null) {
            tracker.updateBackward(list);
            trackerDbg(key, tracker);
        }
    }

    protected void trackerDbg(String key, ListingTracker<T> tracker) {
        if (tracker != null) {
            Timber.d("Tracker[%s] before:%s after:%s count:%d",
                    key, tracker.getBefore(), tracker.getAfter(), tracker.getCount());
        }
    }

    public void setObserver(ContentObserver observer) {
        this.mObserver = observer;
    }

    public void setObserverUri(Uri observerUri) {
        this.mObserverUri = observerUri;
    }

    public void setObserverAndUri(ContentObserver observer, Uri observerUri) {
        setObserver(observer);
        setObserverUri(observerUri);
    }

    public void setLayoutId(@LayoutRes int layoutId) {
        this.mLayoutId = layoutId;
    }

}
