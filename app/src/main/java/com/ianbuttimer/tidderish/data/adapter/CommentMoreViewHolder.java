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

import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.IAdapterHandler;
import com.ianbuttimer.tidderish.databinding.CommentMoreListItemBinding;
import com.ianbuttimer.tidderish.reddit.Comment;
import com.ianbuttimer.tidderish.reddit.CommentMore;

import java.text.MessageFormat;


/**
 * A RecyclerView.ViewHolder for Subreddit objects
 */

public class CommentMoreViewHolder extends CommentViewHolder {

    /**
     * Constructor
     * @param view              View to hold
     * @param adapterHandler    Handler for view
     */
    public CommentMoreViewHolder(View view, IAdapterHandler adapterHandler, CommentMoreListItemBinding binding) {
        super(view, adapterHandler, null);
        // do bind here ignoring super
        bind(binding);
    }

    @Override
    protected void bind(ViewBinding viewBinding) {
        if (viewBinding instanceof CommentMoreListItemBinding) {
            CommentMoreListItemBinding binding = (CommentMoreListItemBinding) viewBinding;
            this.tvText = binding.tvTextCommentItem;
            this.imgReplies = binding.imgRepliesCommentItem;
            this.bsvView = null;
            this.pbProgress = binding.pbCommentItem;
        }
    }

    @Override
    public void setViewInfo(Comment info, int position) {
        if (info instanceof CommentMore) {
            CommentMore moreComment = (CommentMore) info;

            tvText.setText(
                    MessageFormat.format(
                            getContext().getString(R.string.comment_more_text),
                            moreComment.getCount()));

            addIndents(info);
        }

        setBackground(position);

        hideProgress();
    }

    @Override
    protected void showProgress(int visibility) {
        if (pbProgress != null) {
            int imgVisibility = (visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            pbProgress.setVisibility(visibility);
            imgReplies.setVisibility(imgVisibility);
        }
    }
}
