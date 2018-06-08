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

package com.ianbuttimer.tidder.data.adapter;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.reddit.Subreddit;
import com.ianbuttimer.tidder.event.FollowEvent;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ianbuttimer.tidder.utils.ColourUtils.getContrastColor;
import static com.ianbuttimer.tidder.utils.Utils.getCountIndication;


/**
 * A RecyclerView.ViewHolder for Subreddit objects
 */

public class SubredditViewHolder extends AbstractViewHolder<Subreddit> {

    @BindView(R.id.tv_name_subred_item) TextView tvName;
    @BindView(R.id.tv_title_subred_item) TextView tvTitle;
    @BindView(R.id.tv_desc_subred_item) TextView tvDescription;
    @BindView(R.id.tv_followers_subred_item) TextView tvFollowers;
    @BindView(R.id.tv_age_subred_item) TextView tvAge;
    @BindView(R.id.fab_like_subred_item) FloatingActionButton fabLike;
    @BindView(R.id.fl_background_subred_item) FrameLayout flBackground;

    private Subreddit mSubreddit;

    /**
     * Constructor
     * @param view              View to hold
     * @param adapterHandler    Handler for view
     */
    public SubredditViewHolder(View view, IAdapterHandler adapterHandler) {
        // don't set clock listener for view, individual view items have own listeners
        super(view, adapterHandler, Listeners.CLICK_AND_LONG);

        ButterKnife.bind(this, view);
    }

    @Override
    public void setViewInfo(Subreddit info, int position) {
        mSubreddit = info;

        tvName.setText(mSubreddit.getDisplayName());
        tvTitle.setText(mSubreddit.getTitle());

        setText(tvDescription, mSubreddit.getDescriptionHtml());

        tvFollowers.setText(
                getCountIndication(mSubreddit.getSuscribers(), R.string.item_follower, R.string.item_followers));

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
        if (following) {
            icon = R.drawable.ic_unlike;
        } else {
            icon = R.drawable.ic_like;
        }
        fabLike.setImageResource(icon);
    }


    @Override
    public void onClick(View view) {
        // no op
    }

    @OnClick(R.id.fab_like_subred_item)
    public void onLikeClick(View view) {
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

    }
}
