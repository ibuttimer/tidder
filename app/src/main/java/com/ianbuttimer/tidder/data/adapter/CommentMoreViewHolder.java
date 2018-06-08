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

import android.view.View;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.CommentMore;

import java.text.MessageFormat;


/**
 * A RecyclerView.ViewHolder for Subreddit objects
 */

public class CommentMoreViewHolder extends CommentViewHolder {

    protected CommentMore mMoreComment;

    /**
     * Constructor
     * @param view              View to hold
     * @param adapterHandler    Handler for view
     */
    public CommentMoreViewHolder(View view, IAdapterHandler adapterHandler) {
        super(view, adapterHandler);
    }

    @Override
    public void setViewInfo(Comment info, int position) {
        mComment = info;
        mMoreComment = (CommentMore) info;

        tvText.setText(
            MessageFormat.format(
                    getContext().getString(R.string.comment_more_text),
                    mMoreComment.getCount()));

        addIndents(mComment);

        setBackground(position);

        hideProgress();
    }

    public void showProgress() {
        showProgress(View.VISIBLE);
    }

    public void hideProgress() {
        showProgress(View.INVISIBLE);
    }

    private void showProgress(int visibility) {
        if (pbProgress != null) {
            int imgVisibility = (visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            pbProgress.setVisibility(visibility);
            imgReplies.setVisibility(imgVisibility);
        }
    }


}
