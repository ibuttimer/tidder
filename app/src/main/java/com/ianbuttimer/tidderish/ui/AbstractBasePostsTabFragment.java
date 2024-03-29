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


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.primitives.Ints;
import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.Follow;
import com.ianbuttimer.tidderish.data.IAdapterHandler;
import com.ianbuttimer.tidderish.data.InfoCache;
import com.ianbuttimer.tidderish.data.adapter.AbstractRecycleViewAdapter;
import com.ianbuttimer.tidderish.data.adapter.AbstractViewHolder;
import com.ianbuttimer.tidderish.data.adapter.LinkAdapter;
import com.ianbuttimer.tidderish.data.adapter.LinkViewHolder;
import com.ianbuttimer.tidderish.databinding.LinkListItemBinding;
import com.ianbuttimer.tidderish.event.AbstractEvent;
import com.ianbuttimer.tidderish.event.PostsEvent;
import com.ianbuttimer.tidderish.event.RedditClientEvent;
import com.ianbuttimer.tidderish.event.StandardEvent;
import com.ianbuttimer.tidderish.reddit.Link;
import com.ianbuttimer.tidderish.reddit.Subreddit;
import com.ianbuttimer.tidderish.reddit.get.SubredditAboutResponse;
import com.ianbuttimer.tidderish.reddit.util.LinkFindByName;
import com.ianbuttimer.tidderish.reddit.util.LinkFindBySubredditName;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;

import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Base class for Posts activity tab fragments
 */

public abstract class AbstractBasePostsTabFragment
            extends AbstractListingTabFragment<Link, LinkListItemBinding, LinkViewHolder>
            implements IAdapterHandler {

    private static final String TAG = AbstractBasePostsTabFragment.class.getSimpleName();
    private static final String TAG_STANDARD_EVENT = TAG + ":onStandardEvent";

    protected static final String FOLLOWING = "following";
    protected static final String CACHE = "cache";

    protected ArrayList<Follow> mFollowing;      // list of subreddits being followed
    protected HashMap<String, InfoCache> mCache;    // subreddit/posts info cache using subreddit display name as key

    public AbstractBasePostsTabFragment(@LayoutRes int layoutId) {
        super(layoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        readSavedInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    protected AbstractRecycleViewAdapter<Link, LinkListItemBinding, LinkViewHolder> getAdapter() {
        return new LinkAdapter(mList, this);
    }

    @Override
    public void onItemClick(View view) {
        Link link = getClickedObject(view);
        int position = mAdapter.setSelectedPos(mLayoutManager, view);

        postEvent(PostsEvent.newViewPostRequest(
                link.getName(), link.getTitle(), link.getPermalink(), position));

        if (mTwoPane) {
            // only change background when in 2 pane mode
            if ((getActivity() != null) && (getRecyclerView() != null)) {
                Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.post_selected_background, null);
                view.setBackground(background);

                setViewsBackground(null, position);
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view) {
        return mSelectCtrl.onItemLongClick(view);
    }

    @Override
    public void onItemDoubleClick(View view) {
        mSelectCtrl.onItemDoubleClick(view);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        return mSelectCtrl.onKey(view, keyCode, keyEvent);
    }

    protected Link getClickedObject(View view) {
        return (Link) view.getTag(R.id.base_obj_tag);
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        mSelectCtrl.onItemDismiss(position, direction);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return mSelectCtrl.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if ((mCache != null) && (mCache.size() > 0)) {
            outState.putParcelable(makeBundleKey(CACHE), Parcels.wrap(mCache));
        }
        if ((mFollowing != null) && (mFollowing.size() > 0)) {
            outState.putParcelable(makeBundleKey(FOLLOWING), Parcels.wrap(mFollowing));
        }
    }

    @Override
    protected void readSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String key = makeBundleKey(CACHE);
            if (savedInstanceState.containsKey(key)) {
                mCache = Parcels.unwrap(savedInstanceState.getParcelable(key));
            }
            key = makeBundleKey(FOLLOWING);
            if (savedInstanceState.containsKey(key)) {
                mFollowing = Parcels.unwrap(savedInstanceState.getParcelable(key));
            }
        }
        if (mCache == null) {
            mCache = new HashMap<>();
        }
        if (mFollowing == null) {
            mFollowing = new ArrayList<>();
        }
    }

    @UiThread
    @Override
    protected void processMessageEvent(AbstractEvent<?> event) {
        if (PostOffice.deliverEvent(event, getAddress())) {
            if (event instanceof PostsEvent) {
                onPostsEvent((PostsEvent)event);
            } else if (event instanceof StandardEvent) {
                onStandardEvent((StandardEvent)event);
            }
        }
    }

    protected boolean onStandardEvent(StandardEvent event) {
        boolean handled = true;

        if (event.isSubredditInfoResult()) {
            // NEW POST FLOW 10. handle subreddit info result
            SubredditAboutResponse response = event.getSubredditAboutResponse();
            if (response != null) {
                Subreddit subreddit = response.getSubreddit();
                if (subreddit != null) {
                    String name = subreddit.getDisplayName();
                    if (!TextUtils.isEmpty(name)) {
                        InfoCache infoCache = mCache.get(name);
                        if (infoCache != null) {
                            infoCache.setSubreddit(subreddit);

                            // set thumbnails in queue
                            for (Link link : infoCache.getQueue()) {
                                if (link.isSelfThumbnail() && link.getSubreddit().equals(name)) {
                                    link.setThumbnail(subreddit.getIcon());
                                }
                            }
                        }

                        // set thumbnails in list
                        for (int i = 0; i < mList.size(); ++i) {
                            Link link = mList.get(i);
                            if (link.isSelfThumbnail() && link.getSubreddit().equals(name)) {
                                link.setThumbnail(subreddit.getIcon());
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    }
                }
            }
        } else {
            handled = false;
        }

        PostOffice.logHandled(event, TAG_STANDARD_EVENT, handled);

        return handled;
    }

    protected boolean onPostsEvent(PostsEvent event) {
        boolean handled = true;

        if (event.isClearPostsCommand()) {
            setViewsBackground(null);
        } else {
            handled = false;
        }

        return handled;
    }

    @Subscribe
    public void onMessageEvent(RedditClientEvent event) {
        String tag = getAddress();
        if (PostOffice.deliverEventOrBroadcast(event, tag)) {
            boolean handled = onClientEvent(event);

            PostOffice.logHandled(event, tag, handled);
        }
    }

    protected abstract boolean onClientEvent(RedditClientEvent event);


    protected LinkFindBySubredditName mSubredditFinder = new LinkFindBySubredditName(null);

    protected Link findBySubreddit(String subredditName) {
        mSubredditFinder.setName(subredditName);
        return mAdapter.findItem(mSubredditFinder);
    }

    protected LinkFindByName mLinkFinder = new LinkFindByName(null);

    protected Link findByLink(String name) {
        mLinkFinder.setName(name);
        return mAdapter.findItem(mLinkFinder);
    }

    protected Pair<InfoCache, Subreddit> getCachedRefs(Link link) {
        InfoCache infoCache;
        Subreddit subreddit;
        String subredditName = link.getSubreddit();

        if (!mCache.containsKey(subredditName)) {
            subreddit = new Subreddit();
            subreddit.setDisplayName(subredditName);
            infoCache = new InfoCache(subreddit);
            mCache.put(subredditName, infoCache);

            // post subreddit info request
            postEvent(StandardEvent.newSubredditInfoRequest(subredditName));
        } else {
            infoCache = mCache.get(subredditName);
            subreddit = infoCache.getSubreddit();
        }

        if (link.isSelfThumbnail() && (subreddit != null)) {
            // replace self thumbnail with subreddit icon
            Uri icon = subreddit.getIcon();
            if (icon != null) {
                link.setThumbnail(icon);
            }
        }
        return new Pair<>(infoCache, subreddit);
    }


    protected void setViewsBackground(Drawable background, int... exclude) {
        RecyclerView rvList = getRecyclerView();
        if (rvList != null) {
            int[] excludes;
            if (exclude != null) {
                excludes = exclude;
            } else {
                excludes = new int[0];
            }
            for (int i = 0; i < mList.size(); i++) {
                if (!Ints.contains(excludes, i)) {
                    RecyclerView.ViewHolder vh = rvList.findViewHolderForAdapterPosition(i);
                    if (vh instanceof AbstractViewHolder) {
                        View holderView = ((AbstractViewHolder<?, ?>)vh).getView();
                        holderView.setBackground(background);
                        holderView.invalidate();
                    }
                }
            }
        }
    }
}
