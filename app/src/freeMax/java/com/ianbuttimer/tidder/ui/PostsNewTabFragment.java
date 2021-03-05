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

package com.ianbuttimer.tidder.ui;


import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.databinding.FragmentPostsBinding;

/**
 * Base class for Posts activity tab fragments
 */

public class PostsNewTabFragment extends AbstractPostsNewTabFragment {

    private FragmentPostsBinding binding;

    public PostsNewTabFragment() {
        super(R.layout.fragment_posts);
    }

    @Override
    public void onStart() {
        super.onStart();

        // local db variants don't need valid user info to access db
        requestFollowing();
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
