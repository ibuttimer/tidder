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

package com.ianbuttimer.tidderish.ui;


import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.Config;
import com.ianbuttimer.tidderish.data.ConfigQueryResponse;
import com.ianbuttimer.tidderish.databinding.FragmentPostsBinding;
import com.ianbuttimer.tidderish.event.RedditClientEvent;
import com.ianbuttimer.tidderish.event.StandardEvent;
import com.ianbuttimer.tidderish.ui.util.DatabaseSettings;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;
import com.ianbuttimer.tidderish.utils.Utils;

/**
 * Base class for Posts activity tab fragments
 */

public class PostsNewTabFragment extends AbstractPostsNewTabFragment {

    private FragmentPostsBinding binding;

    public PostsNewTabFragment() {
        super(R.layout.fragment_posts);
    }

    @Override
    protected boolean onClientEvent(RedditClientEvent event) {
        boolean handled = true;
        if (event.isUserValidEvent()) {
            // full version requires valid user info to access db & latest config from db
            PostOffice.postEvent(StandardEvent.newSettingsRequest(),
                                    PostListActivity.getActivityAddress(),
                                    getAddress());
        } else {
            handled = false;
        }
        return handled;
    }

    @Override
    protected boolean onStandardEvent(StandardEvent event) {
        boolean handled = super.onStandardEvent(event);
        if (!handled) {
            handled = true;

            if (event.isSettingsResult()) {
                ConfigQueryResponse response = event.getConfigQueryResponse();
                if (response != null) {
                    //
                    Config[] array = response.getArray();
                    if (Utils.arrayHasSize(array)) {
                        DatabaseSettings.getInstance().applyConfig(array[0]);
                    }
                }

                // have latest config, so request following
                requestFollowing();
            } else {
                handled = false;
            }

            PostOffice.logHandled(event, getAddress(), handled);
        }
        return handled;
    }

    @Override
    protected ViewBinding getViewBinding() {
        binding = FragmentPostsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return binding.incListingLayout.rvListListingL;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return binding.incListingLayout.pbProgressListingL;
    }

    @Override
    protected TextView getTextView() {
        return binding.incListingLayout.tvMessageListingL;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
