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

package com.ianbuttimer.tidderish.event;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;

import com.ianbuttimer.tidderish.data.ICallback;
import com.ianbuttimer.tidderish.data.QueryCallback;
import com.ianbuttimer.tidderish.data.provider.BaseProvider;
import com.ianbuttimer.tidderish.data.provider.ProviderUri;
import com.ianbuttimer.tidderish.reddit.BaseObject;
import com.ianbuttimer.tidderish.reddit.Response;
import com.ianbuttimer.tidderish.reddit.Subreddit;
import com.ianbuttimer.tidderish.reddit.get.SubredditAboutRequest;
import com.ianbuttimer.tidderish.reddit.get.ThingAboutRequest;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ianbuttimer.tidderish.data.db.FollowColumns.SUBREDDIT;
import static com.ianbuttimer.tidderish.data.provider.BaseProvider.FollowBase.SUBREDDIT_EQ_SELECTION;
import static com.ianbuttimer.tidderish.data.provider.BaseProvider.FollowBase.SUBREDDIT_PROJECTION;

/**
 * Class to process StandardEvents
 */

public class StandardEventProcessor implements IStandardEventProcessor {

    private static final String TAG = StandardEventProcessor.class.getSimpleName();

    private final WeakReference<FragmentActivity> mActivity;
    @Nullable private final ICallback<Response<? extends BaseObject<?>>> mApiResponseHandler;
    @Nullable private final QueryCallback<StandardEvent> mCpStdEventHandler;

    private final HashMap<String, Integer> mLoaderIds;

    private final ArrayList<IStandardEventProcessorExt> mExtensions;

    private final String mAddress;    // post office address
    private final String mTag;        // tag for log messages

    /**
     * Constructor
     * @param activity              Current activity
     * @param apiResponseHandler    Api response handler
     * @param cpResponseHandler     Content provider response handler
     */
    public StandardEventProcessor(FragmentActivity activity,
                                  @Nullable ICallback<Response<? extends BaseObject<?>>> apiResponseHandler,
                                  @Nullable QueryCallback<StandardEvent> cpResponseHandler) {
        this(activity, activity.getClass().getSimpleName(), apiResponseHandler, cpResponseHandler);
    }

    /**
     * Constructor
     * @param activity              Current activity
     * @param address               Processor address
     * @param apiResponseHandler    Api response handler
     * @param cpResponseHandler     Content provider response handler
     */
    public StandardEventProcessor(FragmentActivity activity,
                                  String address,
                                  @Nullable ICallback<Response<? extends BaseObject<?>>> apiResponseHandler,
                                  @Nullable QueryCallback<StandardEvent> cpResponseHandler) {
        this.mActivity = new WeakReference<>(activity);
        this.mAddress = address;
        this.mApiResponseHandler = apiResponseHandler;
        this.mCpStdEventHandler = cpResponseHandler;
        this.mLoaderIds = new HashMap<>();

        this.mTag = TAG + ":" + mAddress;
        this.mExtensions = new ArrayList<>();
    }

    /**
     * Handle an event
     * @param event Event to handle
     * @return  <code>true</code> if event handled
     */
    public boolean onStandardEvent(StandardEvent event) {
        boolean handled = false;

        if (PostOffice.deliverEventOrBroadcast(event, mAddress, mTag)) {
            handled = true;

            if (event.isQueryFollowStatusListRequest()) {
                // SEARCH FLOW 6. query following status of subreddit list
                // ALL FLOW 6. query following status of subreddit list
                ArrayList<Subreddit> list = event.getList();
                if (!list.isEmpty() && (mCpStdEventHandler != null)) {
                    int length = list.size();
                    String[] selectionArgs = new String[length];
                    for (int i = 0; i < length; i++) {
                        selectionArgs[i] = list.get(i).getDisplayName();
                    }
                    String selection;
                    if (length == 1) {
                        selection = SUBREDDIT_EQ_SELECTION;
                    } else {
                        selection = BaseProvider.columnInSelection(SUBREDDIT, length);
                    }
                    mCpStdEventHandler.query(mActivity.get(),
                            getLoaderId(event),
                            QueryCallback.getBuilder()
                                    .putUri(ProviderUri.FOLLOW_CONTENT_URI)
                                    .putProjection(SUBREDDIT_PROJECTION)
                                    .putSelection(selection)
                                    .putSelectionArgs(selectionArgs)
                                    .putAdditionalInfo(
                                            StandardEvent.getFactory().additionalInfoAll(event))
                                    .build()
                    );
                }
            } else if (event.isFollowingListRequest()) {
                // LIST FLOW 2. request following subreddit list
                // NEW POST FLOW 2. request following subreddit list
                if (mCpStdEventHandler != null) {
                    mCpStdEventHandler.queryList(mActivity.get(),
                            getLoaderId(event),
                            event,
                            ProviderUri.FOLLOW_CONTENT_URI);
                }
            } else if (event.isPinnedListRequest()) {
                if (mCpStdEventHandler != null) {
                    String fullname = event.getName();
                    String selection = null;
                    String[] selectionArgs = null;
                    if (!TextUtils.isEmpty(fullname)) {
                        selection = BaseProvider.PinnedBase.NAME_EQ_SELECTION;
                        selectionArgs = new String[]{fullname};
                    }
                    mCpStdEventHandler.queryList(mActivity.get(),
                            getLoaderId(event),
                            event,
                            ProviderUri.PINNED_CONTENT_URI,
                            selection,
                            selectionArgs);
                }
            } else if (event.isSubredditInfoRequest()) {
                // LIST FLOW 5. request subreddit info
                // NEW POST FLOW 9. request subreddit info
                requestSubredditInfo(event);
            } else if (event.isThingAboutRequest()) {
                if (mApiResponseHandler != null) {
                    Bundle additionalInfo = StandardEvent.getFactory().additionalInfoAll(event);
                    mApiResponseHandler.requestGetService(
                            ThingAboutRequest.builder()
                                    .id(event.getNames())
                                    .build()    // build request
                                    .setAdditionalInfo(additionalInfo)  // add additional info
                    );
                }
            } else {
                handled = false;

                for (int i = 0; (i < mExtensions.size()) && !handled; i++) {
                    handled = mExtensions.get(i).onStandardEvent(event);
                }
            }

            PostOffice.logHandled(event, mTag, handled);
        }
        return handled;
    }

    /**
     * Get a loader id based on the event tag
     * @param event Event
     * @return  loader id
     */
    @Override
    public int getLoaderId(StandardEvent event) {
        int loaderId;
        String tag = event.getAddress();
        if (mLoaderIds.containsKey(tag)) {
            loaderId = mLoaderIds.get(tag);
        } else {
            loaderId = mLoaderIds.size();
            mLoaderIds.put(tag, loaderId);
        }
        return loaderId;
    }


    @Override
    public FragmentActivity getActivity() {
        return mActivity.get();
    }

    /**
     * Request info about a subreddit
     */
    private void requestSubredditInfo(StandardEvent event) {
        if (mApiResponseHandler != null) {
            mApiResponseHandler.requestGetService(
                    SubredditAboutRequest.builder()
                            .subreddit(event.getName())
                            .build()    // build request
                            .setAdditionalInfo(
                                    StandardEvent.getFactory().additionalInfoTag(event))  // add additional info
            );
        }
    }



    public interface IStandardEventProcessorExt {

        void setHost(IStandardEventProcessor host);

        /**
         * Handle an event
         * @param event Event to handle
         * @return  <code>true</code> if event handled
         */
        boolean onStandardEvent(StandardEvent event);
    }

    public boolean addExtension(IStandardEventProcessorExt extension) {
        boolean modified = false;
        if (extension != null) {
            modified = mExtensions.add(extension);
            extension.setHost(this);
        }
        return modified;
    }

    public boolean removeExtension(IStandardEventProcessorExt extension) {
        boolean modified = false;
        if (extension != null) {
            modified = mExtensions.remove(extension);
            extension.setHost(null);
        }
        return modified;
    }

    @Nullable
    @Override
    public ICallback<Response<? extends BaseObject<?>>> getApiResponseHandler() {
        return mApiResponseHandler;
    }

    @Nullable
    @Override
    public QueryCallback<StandardEvent> getCpStdEventHandler() {
        return mCpStdEventHandler;
    }

    public String getAddress() {
        return mAddress;
    }
}
