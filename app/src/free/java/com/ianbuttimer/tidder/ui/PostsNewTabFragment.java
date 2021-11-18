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


import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.ads.AdView;
import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.databinding.FragmentPostsBinding;
import com.ianbuttimer.tidderish.ui.util.AdConfig;

/**
 * Base class for Posts activity tab fragments
 */

public class PostsNewTabFragment extends AbstractPostsNewTabFragment {

    private FragmentPostsBinding binding;

    private AdConfig mAdConfig;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AdView mAdView = binding.adViewListingLayout;

        Activity activity = getActivity();
        if (activity != null) {
            mAdConfig = new AdConfig(activity);
            mAdConfig.initialise();

            mAdConfig.loadAd(mAdView);

//            String testDeviceId = activity.getString(R.string.admob_test_device_id);
//
//            RequestConfiguration.Builder confBuilder = new RequestConfiguration.Builder()
//                    .setMaxAdContentRating(MAX_AD_CONTENT_RATING_G);
//
//            if (!TextUtils.isEmpty(testDeviceId) &&
//                    !testDeviceId.equals(activity.getString(R.string.admob_test_device_id_todo))) {
//                confBuilder.setTestDeviceIds(List.of(testDeviceId));
//            }
//
//            MobileAds.setRequestConfiguration(confBuilder.build());
//            MobileAds.initialize(activity);

//            AdRequest.Builder adBuilder = new AdRequest.Builder();
//            mAdView.loadAd(adBuilder.build());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdConfig.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdConfig.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdConfig.onDestroy();
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
