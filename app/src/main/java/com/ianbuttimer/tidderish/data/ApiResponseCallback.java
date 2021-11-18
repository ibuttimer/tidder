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

package com.ianbuttimer.tidderish.data;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ianbuttimer.tidderish.net.NetworkUtils;
import com.ianbuttimer.tidderish.reddit.BaseObject;
import com.ianbuttimer.tidderish.reddit.Response;
import com.ianbuttimer.tidderish.event.AbstractEvent;
import com.ianbuttimer.tidderish.ui.ICommonEvents;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;

import java.lang.ref.WeakReference;
import java.net.URL;

import timber.log.Timber;

/**
 * Asynchronous request and response handler for Follow queries
 * @param <E> class of event
 */

public class ApiResponseCallback<E extends AbstractEvent<E>> extends AsyncCallback<Response<? extends BaseObject<?>>> {

    private final WeakReference<Activity> mActivity;
    private final ICommonEvents<E, Response<? extends BaseObject<?>>> mEventFactory;

    public ApiResponseCallback(Activity activity, ICommonEvents<E, Response<? extends BaseObject<?>>> eventFactory) {
        super();
        mActivity = new WeakReference<>(activity);
        mEventFactory = eventFactory;
    }

    @Override
    public void onResponse(Response<? extends BaseObject<?>> result) {
        int msgId = 0;
        onApiResponse(result, msgId);
    }

    @Override
    public void onFailure(int code, String message) {
        onApiResponse(null, getErrorId(code));
    }

    /**
     * Convert the http response into a Response object
     * @param response  Response from the server
     * @return Response object or <code>null</code>
     */
    @Override
    public Response<? extends BaseObject<?>> processUrlResponse(@NonNull URL request, @NonNull okhttp3.Response response) {
        String jsonResponse = NetworkUtils.getResponseBodyString(response);
        return processUriResponse(new ICallback.UrlProviderResultWrapper(request, jsonResponse));
    }

    /**
     * Process the response from a {@link ICallback#call(FragmentActivity, int, Uri, String, String, Bundle)} call
     * @param response  Response from the content provider
     * @return Response object or <code>null</code>
     */
    @Override
    @Nullable public Response<? extends BaseObject<?>> processUriResponse(@Nullable AbstractResultWrapper response) {
        Response<? extends BaseObject<?>> newResponse = null;
        if ((response != null) && response.isString()) {
            Class<?> responseClass = response.getResponseClass();
            if (responseClass != null) {
                String stringResult = response.getStringResult();
                E event = null;

                try {
                    newResponse = (Response<? extends BaseObject<?>>)responseClass.newInstance();
                    newResponse.parseJson(stringResult);

                    // SEARCH FLOW 3a. post subreddit interests search result
                    // SEARCH FLOW 3b. post subreddit name search result
                    // LIST FLOW 6. post subreddit info result
                    // ALL FLOW 3. post subreddits result
                    // NEW POST FLOW 6. post subreddit post result
                    event = mEventFactory.getFactoryInstance().newResponseResult(newResponse);

                } catch (InstantiationException | IllegalAccessException e) {
                    Timber.e(e);
                }

                if (event != null) {
                    PostOffice.postEvent(
                            mEventFactory.infoExtractor(
                                    event, response.getAdditionalInfo())
                                    .all()  // add all additional info
                                    .done());
                }
            }

        }
        return newResponse;
    }

    @Override
    public Context getContext() {
        return mActivity.get();
    }


    /**
     * Class to update the ui with response list details
     */
    private static class ApiResponseHandler extends
            com.ianbuttimer.tidderish.data.ResponseHandler<Response<? extends BaseObject<?>>> implements Runnable {

        ApiResponseHandler(Activity activity, Response<? extends BaseObject<?>> response, int errorId) {
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
    protected void onApiResponse(Response<? extends BaseObject<?>> response, int msgId) {
        // ui updates need to be on ui thread
        Activity activity = mActivity.get();
        activity.runOnUiThread(new ApiResponseHandler(activity, response, msgId));
    }

}
