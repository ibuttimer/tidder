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
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.databinding.CommentListItemBinding;
import com.ianbuttimer.tidder.databinding.CommentMoreListItemBinding;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.CommentMore;

import java.util.List;


/**
 * Adapter class for a RecyclerView of Comment
 */

public class CommentAdapter extends AbstractRecycleViewAdapter<Comment, CommentListItemBinding, CommentViewHolder> {

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

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        @LayoutRes int layoutId = mLayoutIds.get(viewType);
        if (layoutId == 0) {
            throw new IllegalStateException("No layout mapping for view type " + viewType);
        }

        // inflate but don't attach
        ViewBinding viewBinding = createView(inflater, viewType, viewGroup);

        return getNewViewHolder(viewBinding, mAdapterHandler, viewType);
    }

    @Override
    protected ViewBinding createView(LayoutInflater inflater, int viewType, ViewGroup parent) {
        ViewBinding binding;
        switch (viewType) {
            case STD_COMMENT:
                binding = CommentListItemBinding.inflate(inflater, parent, false);
                break;
            case MORE_COMMENT:
                binding = CommentMoreListItemBinding.inflate(inflater, parent, false);
                break;
            default:
                throw new IllegalStateException("Unknown viewType: " + viewType);
        }
        return binding;
    }

    @Override
    public CommentViewHolder getNewViewHolder(ViewBinding viewBinding, IAdapterHandler adapterHandler, int viewType) {
        CommentViewHolder viewHolder;
        View rootView = viewBinding.getRoot();
        switch (viewType) {
            case STD_COMMENT:
                viewHolder = new CommentViewHolder(rootView, adapterHandler, (CommentListItemBinding) viewBinding);
                break;
            case MORE_COMMENT:
                viewHolder = new CommentMoreViewHolder(rootView, adapterHandler, (CommentMoreListItemBinding) viewBinding);
                break;
            default:
                throw new IllegalStateException("Unknown viewType: " + viewType);
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
