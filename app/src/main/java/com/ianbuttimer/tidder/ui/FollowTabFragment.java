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

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.Follow;
import com.ianbuttimer.tidder.data.FollowQueryResponse;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.data.ITester;
import com.ianbuttimer.tidder.data.adapter.AbstractRecycleViewAdapter;
import com.ianbuttimer.tidder.data.adapter.SubredditAdapter;
import com.ianbuttimer.tidder.data.adapter.SubredditViewHolder;
import com.ianbuttimer.tidder.data.provider.ProviderUri;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.event.FollowEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.reddit.ListingList;
import com.ianbuttimer.tidder.reddit.ListingTracker;
import com.ianbuttimer.tidder.reddit.Subreddit;
import com.ianbuttimer.tidder.reddit.get.SubredditAboutResponse;
import com.ianbuttimer.tidder.reddit.post.ApiSearchSubredditsResponse;
import com.ianbuttimer.tidder.reddit.util.SubredditFindByName;
import com.ianbuttimer.tidder.ui.util.ISectionsPagerAdapter;
import com.ianbuttimer.tidder.ui.widgets.EndlessRecyclerViewScrollListener;
import com.ianbuttimer.tidder.ui.widgets.ListItemClickListener;
import com.ianbuttimer.tidder.ui.widgets.NoUrlTextViewListItemClickListener;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.ArrayTester;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;


/**
 * Base class for Follow activity tab fragments
 */

public abstract class FollowTabFragment
            extends AbstractListingTabFragment<Subreddit, SubredditViewHolder>
            implements IAdapterHandler {

    @BindView(R.id.cl_search_fragF) ConstraintLayout layoutSearch;

    public FollowTabFragment() {
        super(R.layout.fragment_follow);
        mObserver = null;
    }

    /**
     * Returns a new instance of this fragment for the given section number.
     * @param sectionNumber Section number
     */
    public static FollowTabFragment newInstance(int sectionNumber) {
        FollowTabFragment fragment = null;
        FollowActivity.Tabs tab = FollowActivity.Tabs.valueOf(sectionNumber);

        if (FollowActivity.Tabs.SEARCH.equals(tab)) {
            fragment = new SearchTabFragment();
        } else if (FollowActivity.Tabs.LIST.equals(tab)) {
            fragment = new ListTabFragment();
        } else if (FollowActivity.Tabs.ALL.equals(tab)) {
            fragment = new AllTabFragment();
        }

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setObserverAndUri(mDbObserver, ProviderUri.FOLLOW_CONTENT_URI);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set On Click Listener for description
        if (rvList != null) {
            rvList.addOnItemTouchListener(new NoUrlTextViewListItemClickListener(
                    getActivity(),
                    new int[]{R.id.tv_desc_subred_item},
                    mAdapter.getAdapterHandler()));
        }
    }

    @Override
    protected void readSavedInstanceState(@Nullable Bundle savedInstanceState) {
        // no op
    }

    @Override
    protected AbstractRecycleViewAdapter<Subreddit, SubredditViewHolder> getAdapter() {
        return new SubredditAdapter(mList, this);
    }

    protected ContentObserver mDbObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // LIST FLOW 1b. request update following subreddit list
            FragmentActivity activity = getActivity();
            if (activity instanceof ISectionsPagerAdapter) {
                ISectionsPagerAdapter pagerAdapter = (ISectionsPagerAdapter)activity;
                ArrayList<String> atHome = new ArrayList<>();
                ArrayList<String> out = new ArrayList<>();

                int count = pagerAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    Fragment fragment = pagerAdapter.getFragment(i);
                    if ((fragment != null) && (fragment instanceof FollowTabFragment)) {
                        String tag = ((FollowTabFragment)fragment).getAddress();
                        if (PostOffice.isRegistered(fragment)) {
                            atHome.add(tag);    // add address to active list
                        } else {
                            out.add(tag);       // add address to inactive list
                        }
                    }
                }
                if (!atHome.isEmpty() && !out.isEmpty()) {
                    // posting a sticky event means its also posted immediately, so both atHome & out can get it
                    atHome.addAll(out);
                    postStickyForActivity(StandardEvent.newUpdateFollowingListRequest(),
                            atHome.toArray(new String[atHome.size()]));
                }
                if (!atHome.isEmpty() && out.isEmpty()) {
                    // just post normal event as there are only active suscribers
                    postEventForActivity(StandardEvent.newUpdateFollowingListRequest(),
                            atHome.toArray(new String[atHome.size()]));
                }
            }
        }
    };

    protected SubredditFindByName mSubredditFinder = new SubredditFindByName(null);

    protected Subreddit findItem(String subredditName) {
        mSubredditFinder.setName(subredditName);
        return mAdapter.findItem(mSubredditFinder);
    }

    @UiThread
    @Override
    protected void processMessageEvent(AbstractEvent event) {

        if (PostOffice.deliverEvent(event, getAddress())) {
            if (event instanceof FollowEvent) {
                onFollowEvent((FollowEvent) event);
            } else if (event instanceof StandardEvent) {
                onStandardEvent((StandardEvent) event);
            }
        }
    }

    protected abstract void onFollowEvent(FollowEvent event);

    protected abstract void onStandardEvent(StandardEvent event);


    @Override
    public void onItemDismiss(int position, int direction) {
        // no op
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemClick(View view) {
        Subreddit subreddit = getClickedObject(view);
    }

    @Override
    public boolean onItemLongClick(View view) {
        Subreddit subreddit = getClickedObject(view);
        return false;
    }

    @Override
    public void onItemDoubleClick(View view) {
        Subreddit subreddit = getClickedObject(view);
    }

    protected Subreddit getClickedObject(View view) {
        return (Subreddit) view.getTag(R.id.base_obj_tag);
    }

    @Override
    protected ListItemClickListener getListItemClickListener() {
        // click listener for FloatingActionButton
        return new ListItemClickListener(getContext(), getIListItemClickTester(),
                new int[] { R.id.fab_like_subred_item });
    }

    @Override
    protected ListItemClickListener.IListItemClickTester getIListItemClickTester() {
        // tester for FloatingActionButton,
        return new ListItemClickListener.SimpleListItemClickTester(
                mAdapter.getAdapterHandler(), false);   // never intercept so the FloatingActionButton can handle it
    }


    /**
     * Tab fragment implementation for subreddit follow search tab
     */
    public static class SearchTabFragment extends FollowTabFragment {

        private static final String TAG = "FollowSearchTab";

        protected static final String QUERY = "query";

        @BindView(R.id.et_interest_fragF) EditText etInterests;
        @BindView(R.id.et_name_fragF) EditText etName;
        @BindView(R.id.btn_search_fragF) FloatingActionButton btnSearch;
        @BindView(R.id.btn_clear_fragF) FloatingActionButton btnClear;

        protected String mQuery;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View rootView = super.onCreateView(inflater, container, savedInstanceState);

            btnSearch.setEnabled(false);
            etInterests.addTextChangedListener(mSearchWatcher);
            etName.addTextChangedListener(mSearchWatcher);

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (rvList != null) {
                mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list

                        // SEARCH SCROLL FLOW 1. request additional subreddits (follows SEARCH FLOW after this)
                        ListingTracker<Subreddit> tracker = getTracker(getAddress());
                        if (tracker != null) {
                            postEventForActivity(FollowEvent.newSearchInterestRequest(
                                    mQuery, null, tracker.getAfter(), tracker.getCount()));
                        }
                    }
                };
                // Adds the scroll listener to RecyclerView
                rvList.addOnScrollListener(mScrollListener);
            }
        }

        @Override
        protected void bind(View rootView) {
            ButterKnife.bind(this, rootView);
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);

            if (!TextUtils.isEmpty(mQuery)) {
                outState.putString(makeBundleKey(QUERY), mQuery);
            }
        }

        @Override
        protected void readSavedInstanceState(@Nullable Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(QUERY)) {
                    mQuery = savedInstanceState.getString(QUERY);
                }
            }
        }

        @OnClick(R.id.btn_search_fragF)
        public void onSearchClick() {
            String interests = etInterests.getText().toString();
            String name = etName.getText().toString();
            FollowEvent event;

            if (!TextUtils.isEmpty(interests)) {
                mQuery = interests;

                // SEARCH FLOW 1a. request subreddit interests search
                event = FollowEvent.newSearchInterestRequest(mQuery);
                addTracker(getAddress(), new ListingTracker<Subreddit>());
            } else {
                mQuery = name;

                // SEARCH FLOW 1b. request subreddit names search
                event = FollowEvent.newSearchNameRequest(mQuery);
            }
            postEventForActivity(event);

            if (mAdapter.clear()) {
                mAdapter.notifyDataSetChanged();
            }

            showInProgress();
        }

        @OnLongClick(R.id.btn_search_fragF)
        public boolean onSearchLongClick(View view) {
            UiUtils.processContentDescription(view);
            return true;
        }

        @OnClick(R.id.btn_clear_fragF)
        public void onClearClick() {
            etInterests.setText("");
            etName.setText("");
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }

        @OnLongClick(R.id.btn_clear_fragF)
        public boolean onClearLongClick(View view) {
            UiUtils.processContentDescription(view);
            return true;
        }

        private TextWatcher mSearchWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // no op
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // no op
            }
            @Override
            public void afterTextChanged(Editable editable) {
                boolean interests = (etInterests.getText().length() > 0);
                boolean name = (etName.getText().length() > 0);
                btnSearch.setEnabled((interests || name) && !(interests && name));
                btnClear.setEnabled(interests || name);
            }
        };

        @Override
        public String getAddress() {
            return getTabAddress();
        }

        public static String getTabAddress() {
            return TAG;
        }

        @Override
        protected void onStandardEvent(StandardEvent event) {
            // process standard listing & following responses
            boolean handled = onListingTabMessageEvent(event);
            if (!handled) {
                handled = true;

                if (event.isSubredditInfoResult()) {
                    SubredditAboutResponse response = event.getAboutResponse();
                    if (response != null) {
                        Subreddit subreddit = response.getSubreddit();
                        if (subreddit != null) {
                            final String name = subreddit.getDisplayName();
                            if (!TextUtils.isEmpty(name)) {
                                Pair<Subreddit, Integer> listItem = mAdapter.findItemAndIndex(new ITester<Subreddit>() {
                                    @Override
                                    public boolean test(Subreddit obj) {
                                        return name.equals(obj.getDisplayName());
                                    }
                                });
                                if ((listItem != null) && (listItem.first != null)) {
                                    listItem.first.copy(subreddit);
                                    mAdapter.notifyItemChanged(listItem.second);
                                }
                            }
                        }
                    }
                } else {
                    handled = false;
                }

                PostOffice.logHandled(event, TAG + ":onStandardEvent", handled);
            }
        }

        @Override
        protected void onFollowEvent(FollowEvent event) {
            // process standard listing & following responses
            boolean handled = onListingTabMessageEvent(event,
                    event.isSearchInterestResult(),
                    event.getSearchInterestResponse(),
                    R.string.no_search_result);
            if (!handled) {
                handled = true;

                if (event.isSearchNameResult()) {
                    // SEARCH FLOW 4b. process subreddit name search result
                    boolean noResult = true;
                    ApiSearchSubredditsResponse response = event.getSearchNameResponse();
                    if (response != null) {
                        ArrayList<Subreddit> newEntries = response.getList();
                        int newCount = newEntries.size();
                        if (newCount > 0) {
                            int newPos = mList.size();
                            mList.addAll(newEntries);
                            mAdapter.notifyDataSetChanged();

                            for (Subreddit subreddit : newEntries) {
                                postEventForActivity(StandardEvent.newSubredditInfoRequest(subreddit.getDisplayName()));
                            }

                            noResult = false;

                            // SEARCH FLOW 5b. request if following any in subreddit search result
                            postEventForActivity(
                                    StandardEvent.newFollowingListRequest(
                                                    newEntries, newPos, (newPos + newCount - 1)));
                        }
                    } else {
                        Timber.i("No response in subreddit name search response");
                    }
                    if (noResult) {
                        showMessage(R.string.no_search_result);
                    } else {
                        hideInProgressMessage();
                    }
                } else {
                    handled = false;
                }

                PostOffice.logHandled(event, TAG + ":onFollowEvent", handled);
            }
        }
    }

    /**
     * Base tab fragment implementation for subreddit follow list & all tabs
     */
    public static abstract class NonSearchTabFragment extends FollowTabFragment {

        @Override
        protected void bind(View rootView) {
            ButterKnife.bind(this, rootView);

            layoutSearch.setVisibility(View.GONE);

            // adjust constraints to constrain recycler view to parent
            ConstraintSet constraint = new ConstraintSet();
            constraint.clone((ConstraintLayout)rootView);
            constraint.connect(R.id.rv_list_listingL, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraint.applyTo((ConstraintLayout)rootView);
        }
    }

    /**
     * Tab fragment implementation for subreddit list tab
     */
    public static class ListTabFragment extends NonSearchTabFragment {

        private static final String TAG = "FollowListTab";

        private static ITester<Subreddit> sFollowingTester = new ITester<Subreddit>() {
            @Override
            public boolean test(Subreddit obj) {
                return obj.isFollowing();
            }
        };

        @Override
        public void onStart() {
            super.onStart();

            if ((mList != null) && (mList.isEmpty())) {
                // LIST FLOW 1a. request following subreddit list
                postEventForActivity(StandardEvent.newFollowingListRequest());

                showInProgress();
            }
        }

        @Override
        public String getAddress() {
            return getTabAddress();
        }

        public static String getTabAddress() {
            return TAG;
        }

        @Override
        protected void onFollowEvent(FollowEvent event) {
            // no op
        }

        @Override
        protected void onStandardEvent(StandardEvent event) {
            boolean handled = true;

            if (event.isFollowingListResult()) {
                // LIST FLOW 3. handle following subreddit list result
                boolean noResult = true;
                FollowQueryResponse response = event.getFollowQueryResponse();
                if (response != null) {
                    Follow[] following = response.getArray();
                    if (following != null) {
                        int length = following.length;
                        boolean isNew = event.isNewMode();
                        boolean isUpdate = event.isUpdateMode();

                        if (isNew) {
                            mList = new ArrayList<>(length);
                        } else if (isUpdate) {
                            // mark all not following
                            for (Subreddit subreddit : mList) {
                                subreddit.setFollowing(false);
                            }
                        }
                        for (int i = 0; i < length; i++) {
                            Follow follow = following[i];
                            String name = follow.getSubreddit();
                            Subreddit subreddit = null;

                            if (isUpdate) {
                                subreddit = findItem(name);
                                if (subreddit != null) {
                                    subreddit.setFollowing(true);
                                }
                            }

                            if (subreddit == null) {
                                // update mode not found => add, or new => add
                                subreddit = new Subreddit(follow);
                                subreddit.setFollowing(true);
                                mList.add(subreddit);

                                // LIST FLOW 4. post subreddit info request
                                postEventForActivity(StandardEvent.newSubredditInfoRequest(name));
                            }
                        }
                        if (isUpdate) {
                            // remove any not following
                            mList = (ArrayList<Subreddit>) new ArrayTester<>(mList)
                                            .subList(sFollowingTester, new ArrayList<Subreddit>());
                        }
                        noResult = mList.isEmpty();
                        if (mAdapter.setList(mList)) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (noResult) {
                    showMessage(R.string.not_following_anything);
                } else {
                    hideInProgressMessage();
                }
            } else if (event.isSubredditInfoResult()) {
                // LIST FLOW 7. handle subreddit info result
                SubredditAboutResponse response = event.getAboutResponse();
                if (response != null) {
                    Subreddit subreddit = response.getSubreddit();
                    if (subreddit != null) {
                        Subreddit listItem = findItem(subreddit.getDisplayName());
                        if (listItem != null) {
                            subreddit.setFollowing(listItem.isFollowing());
                            listItem.copy(subreddit);
                            mAdapter.notifyItemChanged(mList.indexOf(listItem));
                        }
                    }
                }
            } else {
                handled = false;
            }

            PostOffice.logHandled(event, TAG + ":onStandardEvent", handled);
        }
    }

    /**
     * Tab fragment implementation for subreddit all tab
     */
    public static class AllTabFragment extends NonSearchTabFragment {

        private static final String TAG = "FollowAllTab";

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (rvList != null) {
                mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list

                        // ALL SCROLL FLOW 1. request additional subreddits (follows ALL FLOW after this)
                        ListingTracker<Subreddit> tracker = getTracker(getAddress());
                        if (tracker != null) {
                            postEventForActivity(FollowEvent.newAllSubredditRequest(
                                    null, null, tracker.getAfter(), tracker.getCount()));
                        }
                    }
                };
                // Adds the scroll listener to RecyclerView
                rvList.addOnScrollListener(mScrollListener);
            }
        }

        @Override
        public void onStart() {
            super.onStart();

            if (mList.isEmpty()) {
                // ALL FLOW 1. request all subreddit list
                postEventForActivity(FollowEvent.newAllSubredditRequest(null, null, null, 0));
                addTracker(getAddress(), new ListingTracker<Subreddit>());

                showInProgress();
            }
        }

        @Override
        public String getAddress() {
            return getTabAddress();
        }

        public static String getTabAddress() {
            return TAG;
        }

        @Override
        protected void onFollowEvent(FollowEvent event) {
            // process standard listing & following responses
            onListingTabMessageEvent(event,
                    event.isAllSubredditResult(),
                    event.getAllResponse(),
                    R.string.no_subreddit_result);
        }

        @Override
        protected void onStandardEvent(StandardEvent event) {
            // process standard listing & following responses
            boolean handled = onListingTabMessageEvent(event);

            PostOffice.logHandled(event, TAG + ":onStandardEvent", handled);
        }
    }

    protected boolean onListingTabMessageEvent(FollowEvent event,
                                            boolean isListing,
                                            ListingList<Subreddit> listing,
                                            @StringRes int okMessage) {
        boolean handled = true;

        if (isListing) {
            // ALL FLOW 4. process subreddits result
            // SEARCH FLOW 4a. process subreddit interests search result
            boolean noResult = true;
            if (listing != null) {
                ArrayList<Subreddit> newEntries = listing.getList();
                int newCount = newEntries.size();
                if (newCount > 0) {
                    int newPos = mList.size();
                    mList.addAll(newEntries);
                    mAdapter.notifyDataSetChanged();

                    noResult = false;

                    updateTrackerForward(getAddress(), listing);

                    // ALL FLOW 5. request if following any in subreddits result
                    // SEARCH FLOW 5a. request if following any in subreddit search result
                    postEventForActivity(StandardEvent.newFollowingListRequest(
                                    newEntries, newPos, (newPos + newCount - 1)));
                }
            } else {
                Timber.i("No listing response");
            }
            if (noResult) {
                showMessage(okMessage);
            } else {
                hideInProgressMessage();
            }
        } else {
            handled = false;
        }

        PostOffice.logHandled(event, getAddress() + ":onListingTabMessageEvent", handled);

        return handled;
    }

    protected boolean onListingTabMessageEvent(StandardEvent event) {
        boolean handled = true;

        if (event.isFollowingListResult()) {
            // ALL FLOW 8. handle following status of subreddit list result
            // SEARCH FLOW 8. handle following status of subreddit list result
            FollowQueryResponse response = event.getFollowQueryResponse();
            if (response != null) {
                Follow[] following = response.getArray();
                if (following != null) {
                    int length = following.length;
                    Pair<Integer, Integer> range = event.getRange();
                    if ((range.first == 0) && (range.second == 0)) {
                        range = new Pair<>(0, mList.size() - 1);
                    }

                    // update list with following status
                    if (!mList.isEmpty()) {
                        for (int rngIdx = range.first; rngIdx <= range.second; ++rngIdx) {
                            Subreddit subreddit = mList.get(rngIdx);
                            String displayName = subreddit.getDisplayName();
                            boolean isFollowing = subreddit.isFollowing();
                            boolean inList = false;

                            for (int followIdx = 0; followIdx < length; followIdx++) {
                                if (displayName.equals(following[followIdx].getSubreddit())) {
                                    inList = true;
                                    if (!isFollowing) {
                                        subreddit.setFollowing(true);
                                        mAdapter.notifyItemChanged(rngIdx);
                                    }
                                    break;
                                }
                            }
                            if (!inList && isFollowing) {
                                subreddit.setFollowing(false);
                                mAdapter.notifyItemChanged(rngIdx);
                            }
                        }
                    }
                }
            }
        } else {
            handled = false;
        }

        PostOffice.logHandled(event, getAddress() + ":onListingTabMessageEvent", handled);

        return handled;
    }

}
