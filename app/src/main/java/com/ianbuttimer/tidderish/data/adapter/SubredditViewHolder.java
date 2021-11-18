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

package com.ianbuttimer.tidderish.data.adapter;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.IAdapterHandler;
import com.ianbuttimer.tidderish.databinding.SubredditListItemBinding;
import com.ianbuttimer.tidderish.event.FollowEvent;
import com.ianbuttimer.tidderish.reddit.Subreddit;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;
import com.ianbuttimer.tidderish.utils.Utils;

import java.text.MessageFormat;
import java.util.Date;

import static com.ianbuttimer.tidderish.utils.ColourUtils.getContrastColor;


/**
 * A RecyclerView.ViewHolder for Subreddit objects
 */

public class SubredditViewHolder<B extends ViewBinding> extends AbstractViewHolder<Subreddit, B> {

    private final TextView tvName;
    private final TextView tvTitle;
    private final TextView tvDescription;
    private final TextView tvFollowers;
    private final TextView tvAge;
    private final FloatingActionButton fabLike;
    private final FrameLayout flBackground;

    private Subreddit mSubreddit;

    /**
     * Constructor
     * @param view              View to hold
     * @param adapterHandler    Handler for view
     */
    public SubredditViewHolder(View view, IAdapterHandler adapterHandler, SubredditListItemBinding binding) {
        // don't set clock listener for view, individual view items have own listeners
        super(view, adapterHandler, Listeners.CLICK_AND_LONG);

        tvName = binding.tvNameSubredItem;
        tvTitle = binding.tvTitleSubredItem;
        tvDescription = binding.tvDescSubredItem;
        tvFollowers = binding.tvFollowersSubredItem;
        tvAge = binding.tvAgeSubredItem;
        fabLike = binding.fabLikeSubredItem;
        flBackground = binding.flBackgroundSubredItem;

        fabLike.setOnClickListener(onLikeClick);
    }

    @Override
    public void setViewInfo(Subreddit info, int position) {
        mSubreddit = info;

        tvName.setText(mSubreddit.getDisplayName());
        tvTitle.setText(mSubreddit.getTitle());

        setText(tvDescription, mSubreddit.getDescriptionHtml());

        Utils.setCountIndication(tvFollowers, mSubreddit.getSuscribers(), R.string.item_follower, R.string.item_followers);

        Date created = mSubreddit.getCreated();
        if (created != null) {
            long current = System.currentTimeMillis();
            String age = Utils.getRelativeTimeSpanString(created.getTime(), current);
            tvAge.setText(age);
            tvAge.setVisibility(View.VISIBLE);
        } else {
            tvAge.setVisibility(View.INVISIBLE);
        }

        setFollowing(mSubreddit.isFollowing());

        @ColorInt int keyColour = mSubreddit.getKeyColour();
        @ColorInt int textColour = getContrastColor(keyColour);
        flBackground.setBackgroundColor(keyColour);
        for (TextView tv : new TextView[] { tvName, tvTitle, tvDescription, tvFollowers, tvAge }) {
            tv.setTextColor(textColour);
        }

        setLinkColour(tvDescription, keyColour);
        setLinkMovement(tvDescription);
    }

    private void setFollowing(boolean following) {
        mSubreddit.setFollowing(following);
        @DrawableRes int icon;
        @StringRes int contentDesc;
        @ColorRes int colour;
        if (following) {
            icon = R.drawable.ic_unlike;
            contentDesc = R.string.unfollow_subreddit_content_desc;
            colour = R.color.colorAccentDark;
        } else {
            icon = R.drawable.ic_like;
            contentDesc = R.string.follow_subreddit_content_desc;
            colour = R.color.colorAccent;
        }
        fabLike.setImageResource(icon);
        fabLike.setContentDescription(
                MessageFormat.format(
                        getContext().getString(contentDesc), mSubreddit.getTitle()));
        fabLike.setBackgroundTintList(
                ColorStateList.valueOf(
                        getContext().getResources().getColor(colour)));
    }


    @Override
    public void onClick(View view) {
        // no op
    }

    private final View.OnClickListener onLikeClick = view -> {
        String icon;
        if (mSubreddit.getIcon() != null) {
            icon = mSubreddit.getIcon().toString();
        } else {
            icon = "";
        }

        boolean following = !mSubreddit.isFollowing();
        setFollowing(following);  // toggle following flag

        PostOffice.postEvent(
                FollowEvent.newFollowStateChangeRequest(
                        following,
                        mSubreddit.getDisplayName(),
                        mSubreddit.getKeyColour(),
                        icon
                )
        );
    };
}
