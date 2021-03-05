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


import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.ads.AdView;
import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.databinding.FragmentFollowPinnedBinding;
import com.ianbuttimer.tidder.event.PostsEvent;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.ui.util.AdConfig;

import static com.ianbuttimer.tidder.ui.AbstractPostListActivity.Tabs.PINNED_POSTS;


/**
 * Base class for Posts activity tab fragments
 */

public class PostsPinnedTabFragment extends AbstractBasePostsTabFragment {

    private static final String TAG = PINNED_POSTS.name();

    private FragmentFollowPinnedBinding binding;

    private AdConfig mAdConfig;

    public PostsPinnedTabFragment() {
        super(R.layout.fragment_follow_pinned);
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
//            AdRequest.Builder adBuilder = new AdRequest.Builder();
//            if (!TextUtils.isEmpty(testDeviceId) &&
//                    !testDeviceId.equals(activity.getString(R.string.admob_test_device_id_todo))) {
//                adBuilder.addTestDevice(testDeviceId);
//            }
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
        binding = FragmentFollowPinnedBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return null;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return null;
    }

    @Override
    protected TextView getTextView() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    protected boolean shouldEventRegister() {
        // no pinned functionality in free variant
        return false;
    }

    @Override
    public String getAddress() {
        return getTabAddress();
    }

    public static String getTabAddress() {
        return TAG;
    }

    @Override
    protected boolean onStandardEvent(StandardEvent event) {
        // no op
        return false;
    }

    @Override
    protected boolean onPostsEvent(PostsEvent event) {
        // no op
        return false;
    }

    @Override
    protected boolean onClientEvent(RedditClientEvent event) {
        // no op
        return false;
    }

}
