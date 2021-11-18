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

package com.ianbuttimer.tidderish.ui;


import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import android.os.Looper;
import android.util.Pair;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.InfoCache;
import com.ianbuttimer.tidderish.data.Pinned;
import com.ianbuttimer.tidderish.data.PinnedQueryResponse;
import com.ianbuttimer.tidderish.data.provider.ProviderUri;
import com.ianbuttimer.tidderish.event.PostsEvent;
import com.ianbuttimer.tidderish.event.StandardEvent;
import com.ianbuttimer.tidderish.reddit.Link;
import com.ianbuttimer.tidderish.reddit.RedditObject;
import com.ianbuttimer.tidderish.reddit.Subreddit;
import com.ianbuttimer.tidderish.reddit.get.ThingAboutResponse;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;

import java.util.ArrayList;

import static com.ianbuttimer.tidderish.ui.AbstractPostListActivity.Tabs.PINNED_POSTS;

/**
 * Base class for Posts activity tab fragments
 */

public abstract class AbstractPostsPinnedTabFragment extends AbstractBasePostsTabFragment {

    private static final String TAG = PINNED_POSTS.name();

    public AbstractPostsPinnedTabFragment(@LayoutRes int layoutId) {
        super(layoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setObserverAndUri(mDbObserver, ProviderUri.PINNED_CONTENT_URI);
    }

    protected ContentObserver mDbObserver = new ContentObserver(new Handler(Looper.myLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            postEventForActivity(StandardEvent.newUpdatePinnedListRequest());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getAdapter().getItemCount() == 0) {
            showMessage(R.string.no_pinned_posts);
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
    protected boolean onStandardEvent(StandardEvent event) {
        boolean handled = super.onStandardEvent(event);
        if (!handled) {
            handled = true;

            if (event.isPinnedListResult()) {
                PinnedQueryResponse response = event.getPinnedQueryResponse();
                if (response != null) {
                    ArrayList<Pinned> list = response.getList();
                    if (!list.isEmpty()) {
                        int size = list.size();
                        String[] ids = new String[size];
                        for (int i = 0; i < size; i++) {
                            ids[i] = list.get(i).getFullname();
                        }
                        postEventForActivity(StandardEvent.newThingAboutRequest(ids));

                        hideInProgressMessage();
                    } else {
                        if (mAdapter.clear()) {
                            mAdapter.notifyDataSetChanged();
                        }
                        showMessage(R.string.no_pinned_posts);
                    }
                }
            } else if (event.isThingAboutResult()) {
                ThingAboutResponse response = event.getThingAboutResponse();
                if (response != null) {
                    boolean isNew = event.isNewMode();
                    boolean isUpdate = event.isUpdateMode();

                    ArrayList<Link> linkList = new ArrayList<>();
                    for (RedditObject<?, ?> obj : response.getList()) {
                        if (obj instanceof Link) {
                            boolean add;

                            if (isUpdate) {
                                Link link = findByLink(obj.getName());
                                // update mode not found => add
                                add = (link == null);
                            } else {
                                // new mode => add
                                add = true;
                            }
                            if (add) {
                                Link link = (Link) obj;
                                linkList.add(link);

                                Pair<InfoCache, Subreddit> refs = getCachedRefs(link);
                            }
                        }
                    }
                    mAdapter.setList(linkList);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                handled = false;
            }

            PostOffice.logHandled(event, TAG, handled);
        }
        return handled;
    }

    @Override
    protected boolean onPostsEvent(PostsEvent event) {
        boolean handled = super.onPostsEvent(event);

//        if (!handled) {
//            handled = true;
//
//            if (event.is???()) {
//            } else {
//                handled = false;
//            }
//        }

        PostOffice.logHandled(event, TAG, handled);

        return handled;
    }

    protected void requestPinned() {
        if (mList.size() == 0) {
            postEventForActivity(StandardEvent.newPinnedListRequest());
        } else {
            postEventForActivity(StandardEvent.newUpdatePinnedListRequest());
        }
    }

}
