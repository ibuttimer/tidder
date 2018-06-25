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

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * A RecyclerView.ViewHolder for Subreddit objects
 */

public class CommentViewHolder extends AbstractViewHolder<Comment> {

    private static final String INDENT_TAG = "indent";

    @BindView(R.id.tv_text_comment_item) TextView tvText;
    @BindView(R.id.img_replies_comment_item) ImageView imgReplies;

    @Nullable @BindView(R.id.bsv_comment_item) BasicStatsView bsvView;
    @Nullable @BindView(R.id.pb_comment_item) ProgressBar pbProgress;

    /**
     * Constructor
     * @param view              View to hold
     * @param adapterHandler    Handler for view
     */
    public CommentViewHolder(View view, IAdapterHandler adapterHandler) {
        super(view, adapterHandler);

        ButterKnife.bind(this, view);
    }

    @Override
    public void setViewInfo(Comment info, int position) {

        boolean isRip = info.isRequestInProgress();
        @ColorInt int background = setBackground(position);

        int visibility;
        if (isRip) {
            visibility = View.INVISIBLE;

            showProgress();
        } else {
            visibility = View.VISIBLE;

            hideProgress();

            setText(tvText, info.getBodyHtml());
            setLinkColour(tvText, background);
            setLinkMovement(tvText);
        }
        tvText.setVisibility(visibility);

        if (bsvView != null) {
            if (!isRip) {
                bsvView.setViewInfo(info);
            }
            bsvView.setVisibility(visibility);
        }

        visibility = View.INVISIBLE;
        if ((info.getReplies().length > 0) && !isRip) {
            @StringRes int strRes;
            @DrawableRes int imageRes;
            if (info.isRepliesExpanded()) {
                imageRes = R.drawable.ic_collapse_arrow;
                strRes = R.string.content_desc_collapse_replies;
            } else {
                imageRes = R.drawable.ic_expand_arrow;
                strRes = R.string.content_desc_expand_replies;
            }
            imgReplies.setImageResource(imageRes);
            imgReplies.setContentDescription(getContext().getString(strRes));
            visibility = View.VISIBLE;
        }
        imgReplies.setVisibility(visibility);

        addIndents(info);
    }


    @Override
    public void onViewRecycled() {
        super.onViewRecycled();
        removeIndents();
    }

    /**
     * Add indents to indicate reply level
     * @param info  Comment to display in this view holder
     */
    public void addIndents(Comment info) {
        int depth = info.getDepth();
        if ((depth > 0) && !info.isRequestInProgress()) {
            ConstraintLayout rootView = (ConstraintLayout)getView();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {

                // add required num of indents
                int[] ids = new int[depth];
                for (int i = 0; i < depth; ++i) {
                    View view = inflater.inflate(R.layout.comment_indent, rootView, false);
                    int viewId = View.generateViewId();
                    view.setId(viewId);
                    view.setTag(INDENT_TAG);

                    rootView.addView(view);

                    ids[i] = viewId;
                }

                ConstraintSet constraint = new ConstraintSet();
                constraint.clone(rootView);

                // adjust constraints for indents
                int adjacentID = ConstraintSet.PARENT_ID;
                int adjacentSide = ConstraintSet.START;
                for (int i = 0; i < depth; ++i) {
                    // adjust constraints to constrain view to right of adjacent view
                    constraint.connect(ids[i], ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                    constraint.connect(ids[i], ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                    constraint.connect(ids[i], ConstraintSet.START, adjacentID, adjacentSide);

                    adjacentID = ids[i];
                    adjacentSide = ConstraintSet.END;
                }

                // adjust constraints to constrain text to right of adjacent view
                constraint.connect(R.id.tv_text_comment_item, ConstraintSet.START, adjacentID, adjacentSide);

                constraint.applyTo(rootView);

                rootView.invalidate();
            }
        }

    }

    /**
     * Remove reply level indents
     */
    protected void removeIndents() {
        ConstraintLayout rootView = (ConstraintLayout)getView();

        // remove any added indents
        View view;
        do {
            view = rootView.findViewWithTag(INDENT_TAG);
            if (view != null) {
                rootView.removeView(view);
            }
        } while (view != null);

        ConstraintSet constraint = new ConstraintSet();
        constraint.clone(rootView);

        // adjust constraints to constrain text to right of parent
        constraint.connect(R.id.tv_text_comment_item, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

        constraint.applyTo(rootView);
    }

    @ColorInt
    protected int setBackground(int position) {
        @ColorInt int color;
//        View view = getView();
//        if ((position % 2) == 0) {
//            color = view.getResources().getColor(R.color.list_item_background);
//        } else {
            color = Color.TRANSPARENT;
//        }
//        getView().setBackgroundColor(color);
        return color;
    }


    public void showProgress() {
        showProgress(View.VISIBLE);
    }

    public void hideProgress() {
        showProgress(View.INVISIBLE);
    }

    protected void showProgress(int visibility) {
        if (pbProgress != null) {
            pbProgress.setVisibility(visibility);
        }
    }


}
