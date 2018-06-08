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
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.ianbuttimer.tidder.data.InfoCache;
import com.ianbuttimer.tidder.data.Pinned;
import com.ianbuttimer.tidder.data.PinnedQueryResponse;
import com.ianbuttimer.tidder.data.provider.ProviderUri;
import com.ianbuttimer.tidder.event.PostsEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.RedditObject;
import com.ianbuttimer.tidder.reddit.Subreddit;
import com.ianbuttimer.tidder.reddit.get.ThingAboutResponse;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;

import java.util.ArrayList;

import static com.ianbuttimer.tidder.ui.AbstractPostListActivity.Tabs.PINNED_POSTS;

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

    protected ContentObserver mDbObserver = new ContentObserver(new Handler()) {
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
                        postEventForActivity(PostsEvent.newGetPinnedPostRequest(ids));
                    } else {
                        if (mAdapter.clear()) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
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

        if (!handled) {
            handled = true;

            if (event.isGetPinnedPostResult()) {
                ThingAboutResponse response = event.getThingResponse();
                if (response != null) {
                    boolean isNew = event.isNewMode();
                    boolean isUpdate = event.isUpdateMode();

                    ArrayList<Link> linkList = new ArrayList<>();
                    for (RedditObject obj : response.getList()) {
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
        }

        PostOffice.logHandled(event, TAG, handled);

        return handled;
    }

}
