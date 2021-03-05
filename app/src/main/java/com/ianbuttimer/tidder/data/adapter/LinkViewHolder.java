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

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.databinding.LinkListItemBinding;
import com.ianbuttimer.tidder.net.GlideApp;
import com.ianbuttimer.tidder.net.NetworkUtils;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.util.RedditMisc;
import com.ianbuttimer.tidder.ui.UiUtils;
import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;



/**
 * A RecyclerView.ViewHolder for Subreddit objects
 */

public class LinkViewHolder extends AbstractViewHolder<Link, LinkListItemBinding> {

    private final ImageView imgThumbnail;
    private final TextView tvTitle;
    private final TextView tvSubreddit;
    private final BasicStatsView bsvView;

    private Link mLink;

    /**
     * Constructor
     * @param view              View to hold
     * @param adapterHandler    Handler for view
     */
    public LinkViewHolder(View view, IAdapterHandler adapterHandler, LinkListItemBinding binding) {
        super(view, adapterHandler);

        imgThumbnail = binding.imgThumbnailLinkItem;
        tvTitle = binding.tvTitleLinkItem;
        tvSubreddit = binding.tvSubredditLinkItem;
        bsvView = binding.bsvLinkItem;

        UiUtils.setTypeface(tvTitle, UiUtils.AppTypeface.NOTO_SANS_REGULAR);
        UiUtils.setTypeface(tvSubreddit, UiUtils.AppTypeface.NOTO_SANS_ITALIC);
    }

    @Override
    public void setViewInfo(Link info, int position) {
        mLink = info;

        tvTitle.setText(mLink.getTitle());

        tvSubreddit.setText(mLink.getSubredditNamePrefixed());

        bsvView.setViewInfo(mLink);

        if (mLink.isLoadableThumbnail()) {
            Uri thumbnail = RedditMisc.convertDefaultThumbnailUri(getContext().getResources(), mLink.getThumbnail());

            if (thumbnail != null) {
                thumbnail = NetworkUtils.unescapeUri(thumbnail);

                GlideApp.with(getContext())
                        .load(thumbnail)
                        .placeholder(R.drawable.ic_picture)
                        .centerInside()
                        //                    .fitCenter()
                        .into(imgThumbnail);
            }
        }
    }

    @Override
    public void onViewRecycled() {
        // cancel any pending loads
        GlideApp.with(getContext()).clear(imgThumbnail);
    }
}
