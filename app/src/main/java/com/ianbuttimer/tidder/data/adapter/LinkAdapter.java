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


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.reddit.Link;

import java.util.List;


/**
 * Adapter class for a RecyclerView of Link
 */

public class LinkAdapter extends AbstractRecycleViewAdapter<Link, LinkViewHolder> {

    /**
     * Constructor
     * @param objects           The objects to represent in the list.
     * @param adapterHandler    Handler for the views in this adapter
     */
    public LinkAdapter(@NonNull List<Link> objects, @Nullable IAdapterHandler adapterHandler) {
        super(objects, adapterHandler, R.layout.link_list_item);
    }


    @Override
    public LinkViewHolder getNewViewHolder(View view, IAdapterHandler adapterHandler, int viewType) {
        return new LinkViewHolder(view, adapterHandler);
    }
}
