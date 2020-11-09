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
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.event.PostsEvent;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.event.StandardEvent;

import butterknife.BindView;

import static com.ianbuttimer.tidder.ui.AbstractPostListActivity.Tabs.PINNED_POSTS;


/**
 * Base class for Posts activity tab fragments
 */

public class PostsPinnedTabFragment extends AbstractBasePostsTabFragment {

    private static final String TAG = PINNED_POSTS.name();

    @BindView(R.id.adView_listing_layout) AdView mAdView;

    public PostsPinnedTabFragment() {
        super(R.layout.fragment_follow_pinned);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (activity != null) {
            String testDeviceId = activity.getString(R.string.admob_test_device_id);

            AdRequest.Builder adBuilder = new AdRequest.Builder();
            if (!TextUtils.isEmpty(testDeviceId) &&
                    !testDeviceId.equals(activity.getString(R.string.admob_test_device_id_todo))) {
                adBuilder.addTestDevice(testDeviceId);
            }
            mAdView.loadAd(adBuilder.build());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAdView != null) {
            mAdView.destroy();
        }
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
