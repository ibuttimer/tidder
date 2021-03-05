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
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.Follow;
import com.ianbuttimer.tidder.data.FollowQueryResponse;
import com.ianbuttimer.tidder.data.InfoCache;
import com.ianbuttimer.tidder.data.provider.ProviderUri;
import com.ianbuttimer.tidder.event.PostsEvent;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.ListingTracker;
import com.ianbuttimer.tidder.reddit.Subreddit;
import com.ianbuttimer.tidder.reddit.get.SubredditLinkResponse;
import com.ianbuttimer.tidder.reddit.util.FollowFindBySubredditName;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.ArrayTester;
import com.ianbuttimer.tidder.utils.PreferenceControl;

import java.util.ArrayList;
import java.util.Set;

import timber.log.Timber;

import static com.ianbuttimer.tidder.ui.AbstractPostListActivity.Tabs.NEW_POSTS;


/**
 * Base class for New Posts tab fragment
 */

public abstract class AbstractPostsNewTabFragment extends AbstractBasePostsTabFragment {

    private static final String TAG = NEW_POSTS.name();

    private static final int POST_REQ_LIMIT = 10;   // num of posts to request so can cache some to save network traffic
    private static final int POST_REQ_THRESHOLD = 3;// re-request posts threshold

    public AbstractPostsNewTabFragment(@LayoutRes int layoutId) {
        super(layoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setObserverAndUri(mDbObserver, ProviderUri.FOLLOW_CONTENT_URI);
    }

    protected ContentObserver mDbObserver = new ContentObserver(new Handler(Looper.myLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            postEventForActivity(StandardEvent.newUpdateFollowingListRequest());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    };

    @Override
    boolean isItemViewSwipeEnabledLeft() {
        return true;
    }

    @Override
    boolean isItemViewSwipeEnabledRight() {
        return true;
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

            if (event.isFollowingListResult()) {
                // NEW POST FLOW 3. handle following subreddit list result
                boolean noResult = true;
                FollowQueryResponse response = event.getFollowQueryResponse();
                if (response != null) {
                    boolean ignore = false;
                    ArrayList<Follow> list = response.getList();

                    if ((mFollowing != null) && (mFollowing.size() > 0)) {
                        // ignore if matches existing list
                        ignore = mFollowing.equals(list);
                    }
                    if (!ignore) {
                        // remove any surplus subreddits from following list
                        ArrayList<String> removeList;
                        if (mFollowing != null) {
                            removeList = subredditRemoveList(mFollowing, list, true);
                        } else {
                            removeList = new ArrayList<>();
                        }

                        Timber.i("Removed from following %d", removeList.size());

                        if (!removeList.isEmpty()) {
                            // remove caches for removed subreddits
                            boolean modified = false;
                            for (String name : removeList) {
                                InfoCache infoCache = mCache.remove(name);

                                Timber.i("Removed from cache %s - %s", name, (infoCache != null));

                                if (infoCache != null) {
                                    if (removePostsFromList(infoCache, false)) {
                                        modified = true;
                                    }
                                }
                            }
                            if (modified) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        mFollowing = list;
                        if (mFollowing != null) {
                            noResult = (mFollowing.size() == 0);

                            // NEW POST FLOW 4. request post from subreddit following list
                            requestPostsForAll();
                        }
                    } else {
                        noResult = false;
                    }
                }
                if (noResult) {
                    showMessage(R.string.not_following_anything);
                } else {
                    hideInProgressMessage();
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
            if (event.isGetPostResult()) {
                // NEW POST FLOW 7. handle subreddit post result
                SubredditLinkResponse response = event.getPostResponse();
                if (response != null) {
                    // all posts will be from the same subreddit
                    ArrayList<Link> list = response.getList();
                    if (!list.isEmpty()) {
                        InfoCache infoCache = null;
                        String subredditName = null;

                        for (Link link : list) {
                            subredditName = link.getSubreddit();

                            Pair<InfoCache, Subreddit> refs = getCachedRefs(link);
                            infoCache = refs.first;

                            infoCache.add(link);
                        }

                        updateTrackerForward(subredditName, response);

                        // add a post from subreddit if not already one in list
                        if (findBySubreddit(subredditName) == null) {
                            updatePostsList(infoCache, true);
                        }
                    }
                }
            } else if (event.isRefreshPostsCommand()) {
                Set<String> keySet = mCache.keySet();
                int size = keySet.size();
                for (String key : keySet) {
                    updatePostsList(mCache.get(key), (--size == 0));
                }
                hideInProgressMessage();
            } else {
                handled = false;
            }
        }

        PostOffice.logHandled(event, TAG, handled);

        return handled;
    }

    protected boolean updatePostsList(InfoCache infoCache, boolean notify) {
        boolean modified = false;
        if ((infoCache != null) && (infoCache.size() > 0)) {
            Link link = infoCache.pop();
            if (link != null) {
                // remove any entry from the same subreddit
                Link currentLink = findBySubreddit(link.getSubreddit());
                if (currentLink != null) {
                    mList.remove(currentLink);
                }
                // add new entry
                modified = mList.add(link);
                if (notify) {
                    mAdapter.notifyDataSetChanged();
                }
            }
            if (infoCache.size() <= POST_REQ_THRESHOLD) {
                // request new posts before required
                Subreddit subreddit = infoCache.getSubreddit();
                if (subreddit != null) {
                    requestPosts(subreddit.getDisplayName());
                }
            }
        }
        return modified;
    }

    protected boolean removePostsFromList(InfoCache infoCache, boolean notify) {
        boolean modified = false;
        if (infoCache != null) {
            Subreddit subreddit = infoCache.getSubreddit();
            if (subreddit != null) {
                // remove any entry from the same subreddit
                Link currentLink = findBySubreddit(subreddit.getDisplayName());
                if (currentLink != null) {
                    modified = mList.remove(currentLink);

                    Timber.i("Removed from list %s - %s", subreddit.getDisplayName(), modified);

                    if (notify) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
        return modified;
    }


    @Override
    protected boolean onClientEvent(RedditClientEvent event) {
        // no op
        return false;
    }


    protected void requestFollowing() {
        if (mFollowing.size() == 0) {
            // NEW POST FLOW 1. request following list
            postEventForActivity(StandardEvent.newFollowingListRequest());

            showInProgress();
        }
    }

    /**
     * Request posts for all subreddits
     */
    protected void requestPostsForAll() {
        if (mFollowing != null) {
            for (Follow follow : mFollowing) {
                requestPosts(follow.getSubreddit());
            }
        }
    }

    protected void tidyCache() {
        ArrayList<String> toPurge = new ArrayList<>();
        ArrayTester<Follow> arrayTester = new ArrayTester<>(mFollowing);
        FollowFindBySubredditName tester = new FollowFindBySubredditName(null);

        for (String name : mCache.keySet()) {
            tester.setName(name);
            if (arrayTester.findItem(tester) == null) {
                toPurge.add(name);
            }
        }
        for (String name : toPurge) {
            mCache.remove(name);
        }
    }

    protected ArrayList<String> subredditRemoveList(
            ArrayList<Follow> master, ArrayList<Follow> update, boolean remove) {
        ArrayList<Follow> toRemove = new ArrayList<>();
        ArrayList<String> removed = new ArrayList<>();
        ArrayTester<Follow> arrayTester = new ArrayTester<>(update);
        FollowFindBySubredditName tester = new FollowFindBySubredditName(null);

        for (Follow follow : master) {
            String name = follow.getSubreddit();
            tester.setName(name);
            if (arrayTester.findItem(tester) == null) {
                toRemove.add(follow);
            }
        }
        if (remove) {
            for (Follow follow : toRemove) {
                if (master.remove(follow)) {
                    removed.add(follow.getSubreddit());
                }
            }
        }
        return removed;
    }



    /**
     * Request post for a subreddit
     * @param name  Name of subreddit
     */
    protected void requestPosts(String name) {
        if (!TextUtils.isEmpty(name)) {
            String source = PreferenceControl.getPostSourcePreference(getContext());
            // tracker key is the subreddit name
            ListingTracker<Link> tracker = getTracker(name);
            if (tracker == null) {
                tracker = addTracker(name, new ListingTracker<>());
            }
            if (tracker != null) {
                postEventForActivity(PostsEvent
                        .newGetPostAfterRequest(name, source, tracker, POST_REQ_LIMIT));
            }
        }
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        Link link = mAdapter.remove(position);
        if (link != null) {
            mAdapter.notifyItemRemoved(position);

            if (PreferenceControl.getRefreshOnDiscardPreference(getContext())) {
                String name = link.getSubreddit();
                InfoCache infoCache = mCache.get(name);

                // replace from queue
                updatePostsList(infoCache, true);
            } else if (mAdapter.getItemCount() == 0) {
                showMessage(R.string.refresh_posts_list);
            }
        }
    }

}
