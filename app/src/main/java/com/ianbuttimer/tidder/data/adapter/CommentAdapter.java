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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.CommentMore;

import java.util.List;


/**
 * Adapter class for a RecyclerView of Comment
 */

public class CommentAdapter extends AbstractRecycleViewAdapter<Comment, CommentViewHolder> {

    protected static final int STD_COMMENT = DEFAULT_KEY;
    protected static final int MORE_COMMENT = STD_COMMENT + 1;


    /**
     * Constructor
     * @param objects           The objects to represent in the list.
     * @param adapterHandler    Handler for the views in this adapter
     */
    public CommentAdapter(@NonNull List<Comment> objects, @Nullable IAdapterHandler adapterHandler) {
        super(objects, adapterHandler);
        addLayoutId(STD_COMMENT, R.layout.comment_list_item);
        addLayoutId(MORE_COMMENT, R.layout.comment_more_list_item);
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        @LayoutRes int layoutId = mLayoutIds.get(viewType);
        if (layoutId == 0) {
            throw new IllegalStateException("No layout mapping for view type " + viewType);
        }

        // inflate but don't attach
        ConstraintLayout rootView = (ConstraintLayout)inflater.inflate(layoutId, viewGroup, false);

        return getNewViewHolder(rootView, mAdapterHandler, viewType);
    }

    @Override
    public CommentViewHolder getNewViewHolder(View view, IAdapterHandler adapterHandler, int viewType) {
        CommentViewHolder viewHolder;
        if (viewType == STD_COMMENT) {
            viewHolder = new CommentViewHolder(view, adapterHandler);
        } else {
            viewHolder = new CommentMoreViewHolder(view, adapterHandler);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        Comment item = getItem(position);
        return getItemViewType(item);
    }

    public static int getItemViewType(Comment item) {
        int viewType;
        if (item instanceof CommentMore) {
            viewType = MORE_COMMENT;
        } else {
            viewType = STD_COMMENT;
        }
        return viewType;
    }
}
