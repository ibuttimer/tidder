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


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.IAdapterHandler;
import com.ianbuttimer.tidderish.databinding.SubredditListItemBinding;
import com.ianbuttimer.tidderish.reddit.Subreddit;

import java.util.List;


/**
 * Adapter class for a RecyclerView of Subreddit
 */

public class SubredditAdapter<B extends ViewBinding> extends AbstractRecycleViewAdapter<Subreddit, B, SubredditViewHolder<B>> {

    /**
     * Constructor
     * @param objects           The objects to represent in the list.
     * @param adapterHandler    Handler for the views in this adapter
     */
    public SubredditAdapter(@NonNull List<Subreddit> objects, @Nullable IAdapterHandler adapterHandler) {
        super(objects, adapterHandler, R.layout.subreddit_list_item);
    }

    @Override
    protected ViewBinding createView(LayoutInflater inflater, int viewType, ViewGroup parent) {
        return SubredditListItemBinding.inflate(inflater, parent, false);
    }

    @Override
    public SubredditViewHolder<B> getNewViewHolder(ViewBinding viewBinding, IAdapterHandler clickHandler, int viewType) {
        return new SubredditViewHolder<>(viewBinding.getRoot(), clickHandler, (SubredditListItemBinding) viewBinding);
    }
}
