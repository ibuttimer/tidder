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

package com.ianbuttimer.tidder.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.ianbuttimer.tidder.data.provider.ProviderUri;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.ui.ICommonEvents;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * Asynchronous request and response handler for Follow queries
 */

public class QueryCallback<T extends AbstractEvent> extends
        AbstractContentProviderCallback<QueryResponse> {

    private WeakReference<Activity> mActivity;
    private ICommonEvents<T, Response> mEventFactory;

    public QueryCallback(Activity activity, ICommonEvents<T, Response> eventFactory) {
        super();
        mActivity = new WeakReference<>(activity);
        mEventFactory = eventFactory;
    }

    @Override
    public void onResponse(QueryResponse result) {
        int msgId = 0;
        onQueryResponse(result, msgId);
    }

    @Override
    public void onFailure(int code, String message) {
        onQueryResponse(null, getErrorId(code));
    }

    @Override
    public void processQueryResponse(@Nullable QueryResultWrapper response) {
        if ((response != null) && response.isCursor()) {
            Cursor cursor = response.getCursorResult();
            Uri request = response.getUriRequest();
            T event = null;

            // TODO content provider callback should use similar method to ApiResponseCallback, thus eliminating need for Uris here

            if (ProviderUri.FOLLOW_CONTENT_URI.equals(request)) {
                // SEARCH FLOW 7. post following status of subreddit list result
                // ALL FLOW 7. post following status of subreddit list result
                event = mEventFactory.newCpResponseResult(new FollowQueryResponse(cursor));
            } else if (ProviderUri.PINNED_CONTENT_URI.equals(request)) {
                event = mEventFactory.newCpResponseResult(new PinnedQueryResponse(cursor));
            } else if (ProviderUri.CONFIG_CONTENT_URI.equals(request)) {
                event = mEventFactory.newCpResponseResult(new ConfigQueryResponse(cursor));
            }

            if (event != null) {
                EventBus.getDefault().post(
                        mEventFactory.infoExtractor(
                                event,
                                response.getAdditionalInfo())
                                .all()    // add all info
                                .done()
                );
            }
        }
    }

    /**
     * Process the response from a {@link ICallback#call(FragmentActivity, int, Uri, String, String, Bundle)} call
     * @param response  Response from the content provider
     * @return Response object or <code>null</code>
     */
    @Override
    public QueryResponse processUriResponse(@Nullable AbstractResultWrapper response) {
        // no op
        return null;
    }

    @Override
    public Context getContext() {
        return mActivity.get();
    }


    /**
     * Class to update the ui with response list details
     */
    private class QueryResponseHandler extends
            com.ianbuttimer.tidder.data.ResponseHandler<QueryResponse> implements Runnable {

        QueryResponseHandler(Activity activity, QueryResponse response, int errorId) {
            super(activity, response, errorId, null);
        }

        @Override
        public void run() {
            super.run();
        }
    }

    /**
     * Handle a list response
     * @param response  Response object
     */
    protected void onQueryResponse(QueryResponse response, int msgId) {
        // ui updates need to be on ui thread
        Activity activity = mActivity.get();
        activity.runOnUiThread(new QueryResponseHandler(activity, response, msgId));
    }

//    /**
//     * Query the list of Subreddits being followed
//     * @param activity  Current activity
//     * @param loaderId  Loader id to use
//     * @param event     Request event
//     */
//    public void requestSubredditFollowingList(@NonNull FragmentActivity activity,
//                                              int loaderId, T event) {
//        queryList(activity, loaderId, event, ProviderUri.FOLLOW_CONTENT_URI);
//    }
//
//    /**
//     * Request the list of pinned posts
//     * @param activity  Current activity
//     * @param loaderId  Loader id to use
//     * @param event     Request event
//     */
//    public void requestPinnedList(@NonNull FragmentActivity activity, int loaderId, T event) {
//        queryList(activity, loaderId, event, ProviderUri.PINNED_CONTENT_URI);
//    }
//
//    /**
//     * Request the list of pinned posts
//     * @param activity  Current activity
//     * @param loaderId  Loader id to use
//     * @param event     Request event
//     * @param selection Selection criteria
//     * @param selectionArgs Selection arguments
//     */
//    public void requestPinnedList(@NonNull FragmentActivity activity, int loaderId, T event,
//                                  String selection, String[] selectionArgs) {
//        queryList(activity, loaderId, event, ProviderUri.PINNED_CONTENT_URI,
//                    selection, selectionArgs);
//    }

    /**
     * Query the content provider
     * @param activity  Current activity
     * @param loaderId  Loader id to use
     * @param event     Request event
     * @param uri       Uri of query
     */
    public void queryList(@NonNull FragmentActivity activity, int loaderId, T event, Uri uri) {
        queryList(activity, loaderId, event, uri, null, null);
    }

    /**
     * Query the content provider
     * @param activity  Current activity
     * @param loaderId  Loader id to use
     * @param event     Request event
     * @param uri       Uri of query
     * @param selection Selection criteria
     * @param selectionArgs Selection arguments
     */
    public void queryList(@NonNull FragmentActivity activity, int loaderId, T event, Uri uri,
                           @Nullable String selection, @Nullable String[] selectionArgs) {
        ContentProviderLoader.Builder builder = QueryCallback.getBuilder()
                .putUri(uri)
                .putAdditionalInfo(
                        mEventFactory.getFactoryInstance().additionalInfoAll(event));

        if (!TextUtils.isEmpty(selection)) {
            builder.putSelection(selection)
                    .putSelectionArgs(selectionArgs);
        }
        query(activity, loaderId, builder.build());
    }
}
