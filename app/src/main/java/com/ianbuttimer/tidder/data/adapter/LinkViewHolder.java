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
import com.ianbuttimer.tidder.net.GlideApp;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.ui.UiUtils;
import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A RecyclerView.ViewHolder for Subreddit objects
 */

public class LinkViewHolder extends AbstractViewHolder<Link> {

    @BindView(R.id.img_thumbnail_link_item) ImageView imgThumbnail;
    @BindView(R.id.tv_title_link_item) TextView tvTitle;
    @BindView(R.id.tv_subreddit_link_item) TextView tvSubreddit;
    @BindView(R.id.bsv_link_item) BasicStatsView bsvView;

    private Link mLink;

    /**
     * Constructor
     * @param view              View to hold
     * @param adapterHandler    Handler for view
     */
    public LinkViewHolder(View view, IAdapterHandler adapterHandler) {
        super(view, adapterHandler);

        ButterKnife.bind(this, view);

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
            Uri thumbnail = mLink.getThumbnail();

            GlideApp.with(getContext())
                    .load(thumbnail)
                    .placeholder(R.drawable.ic_picture)
                    .centerInside()
//                    .fitCenter()
                    .into(imgThumbnail);
        }
    }

    @Override
    public void onViewRecycled() {
        // cancel any pending loads
        GlideApp.with(getContext()).clear(imgThumbnail);
    }
}
