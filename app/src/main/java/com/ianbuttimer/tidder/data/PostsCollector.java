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

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;

import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.RedditClient;
import com.ianbuttimer.tidder.reddit.Request;
import com.ianbuttimer.tidder.reddit.ResponseReceiver;
import com.ianbuttimer.tidder.reddit.get.SubredditLinkRequest;
import com.ianbuttimer.tidder.reddit.get.SubredditLinkResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.ianbuttimer.tidder.data.PostsWidgetViewsService.WIDGET_CURSOR_COLUMNS;
import static com.ianbuttimer.tidder.data.PostsWidgetViewsService.makeCursorRow;

public class PostsCollector {

    protected ArrayList<String> mSubreddits;
    protected String mSource;
    protected WeakReference<Context> mContext;

    protected CountDownLatch mLatch;
    protected MatrixCursor mCursor;

    public PostsCollector(Context context, ArrayList<String> subreddits, String source) {
        this.mContext = new WeakReference<>(context);
        this.mSubreddits = subreddits;
        this.mSource = source;
    }


    public Cursor getPosts() {
        int count = mSubreddits.size();
        mCursor = new MatrixCursor(WIDGET_CURSOR_COLUMNS, count);

        if (count > 0) {
            mLatch = new CountDownLatch(count);

            for (String name : mSubreddits) {
                Request request = SubredditLinkRequest.builder()
                        .subreddit(name, mSource)
                        .limit(1)
                        .build();    // build request

                RedditClient.getClient().startServiceForGet(mContext.get(),
                        request,
                        mResultReceiverToLinkResponse);
            }

            try {
                // wait for all responses
                mLatch.await();
            } catch (InterruptedException e) {
                Timber.e(e);
            }
        }

        return mCursor;
    }


    protected void addPost(SubredditLinkResponse response) {

        if ((response != null) && !response.isEmpty()) {
            Link link = response.getItem(0);  // only want one
            if (link != null) {
                mCursor.addRow(makeCursorRow(link));
            }
        }
        mLatch.countDown();
    }

    /**
     * ResponseReceiver to convert response from IntentService to a UrlResultWrapper
     */
    private final ResponseReceiver mResultReceiverToLinkResponse =
            new ResponseReceiver() {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);

                    SubredditLinkResponse result = null;
                    if (resultCode == RESULT_OK) {

                        result = new SubredditLinkResponse();
                        result.parseJson(mResultText);
                    } else if (resultCode == RESULT_CANCELED) {
                        // no op
                    }
                    addPost(result);
                }
            };

}
