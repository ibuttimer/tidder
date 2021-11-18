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

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.ApiResponseCallback;
import com.ianbuttimer.tidderish.data.DatabaseIntentService;
import com.ianbuttimer.tidderish.data.ICallback;
import com.ianbuttimer.tidderish.data.QueryCallback;
import com.ianbuttimer.tidderish.data.provider.BaseProvider;
import com.ianbuttimer.tidderish.data.provider.FollowBuilder;
import com.ianbuttimer.tidderish.databinding.ActivityFollowBinding;
import com.ianbuttimer.tidderish.event.FollowEvent;
import com.ianbuttimer.tidderish.event.StandardEvent;
import com.ianbuttimer.tidderish.event.StandardEventProcessor;
import com.ianbuttimer.tidderish.reddit.BaseObject;
import com.ianbuttimer.tidderish.reddit.RedditClient;
import com.ianbuttimer.tidderish.reddit.Response;
import com.ianbuttimer.tidderish.reddit.get.AllSubredditsRequest;
import com.ianbuttimer.tidderish.reddit.get.SubredditsSearchRequest;
import com.ianbuttimer.tidderish.reddit.post.ApiSearchSubredditsRequest;
import com.ianbuttimer.tidderish.ui.util.AbstractSectionPagerAdapter;
import com.ianbuttimer.tidderish.ui.util.ISectionsPagerAdapter;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;
import com.ianbuttimer.tidderish.ui.widgets.ToastReceiver;
import com.ianbuttimer.tidderish.utils.PreferenceControl;

import org.greenrobot.eventbus.Subscribe;

import java.text.MessageFormat;


public class FollowActivity extends AppCompatActivity implements ISectionsPagerAdapter {

    private static final String TAG = FollowActivity.class.getSimpleName();
    private static final String TAG_FOLLOW_EVENT = TAG + ":onFollowEvent";

    public enum Tabs { LIST, SEARCH, ALL;

        @Nullable public static Tabs valueOf(int ordinal) {
            Tabs value = null;
            for (Tabs tab : values()) {
                if (tab.ordinal() == ordinal) {
                    value = tab;
                    break;
                }
            }
            return value;
        }
    }

    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link androidx.fragment.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityFollowBinding binding = ActivityFollowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FloatingActionButton fabDelete = binding.fabDeleteFollowA;

        Toolbar toolbar = binding.toolbarFollowA;
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create the adapter that will return a fragment for each of the primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, that will host the section contents, with the sections adapter.
        ViewPager mViewPager = binding.containerFollowA;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs_followA);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int visibility;
                if (Tabs.LIST.equals(Tabs.valueOf(position))) {
                    visibility = View.VISIBLE;
                } else {
                    visibility = View.INVISIBLE;
                }
                ((ImageButton)fabDelete).setVisibility(visibility);
            }
        });

        fabDelete.setOnClickListener(view -> {
            Context context = view.getContext();
            DatabaseIntentService.Builder builder =
                    DatabaseIntentService.Builder.builder(
                            context,
                            DatabaseIntentService.Actions.DELETE_ALL_FOLLOW);
            context.startService(builder
                    .resultReceiver(new ToastReceiver(context,
                            context.getResources().getString(R.string.unfollowing_all_toast)))
                    .build());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PostOffice.register(this);
    }

    @Override
    protected void onStop() {
        PostOffice.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onStandardEvent(StandardEvent event) {
        mStdEventProcessor.onStandardEvent(event);
    }

    @Subscribe
    public void onFollowEvent(FollowEvent event) {
        boolean handled = true;

        PostOffice.logDeliver(event, TAG_FOLLOW_EVENT, true);

        if (event.isSearchInterestRequest()
                || event.isSearchNameRequest()) {
            // SEARCH FLOW 2. send subreddit search request
            requestSubreddits(event);
        } else if (event.isAllSubredditRequest()) {
            // ALL FLOW 2. request subreddits
            requestAllSubreddits(event);
        } else if (event.isFollowStateChangeRequest()) {
            requestFollowStateChange(event);
        } else {
            handled = false;
        }


        PostOffice.logHandled(event, TAG_FOLLOW_EVENT, handled);
    }

    /**
     * Request a subreddit search
     */
    private void requestSubreddits(FollowEvent event) {
        // additional info bundle for request
        Bundle additionalInfo = FollowEvent.getFactory().additionalInfoTag(event);

        if (event.isSearchInterestRequest()) {
            mApiResponseHandler.requestGetService(
                    SubredditsSearchRequest.builder()
                            .query(event.getQuery())
                            .listing(event)
                            .showAll()
                            .build()    // build request
                    .setAdditionalInfo(additionalInfo)  // add additional info
            );
        } else if (event.isSearchNameRequest()) {
            boolean sfw = PreferenceControl.getSafeForWorkPreference(this);
            mApiResponseHandler.requestPostService(
                    ApiSearchSubredditsRequest.builder()
                            .query(event.getQuery())
                            .over18data(!sfw)
                            .build()    // build request
                    .setAdditionalInfo(additionalInfo)  // add additional info
            );
        }
    }

    /**
     * Request all subreddit
     */
    private void requestAllSubreddits(FollowEvent event) {
        // additional info bundle for request
        Bundle additionalInfo = FollowEvent.getFactory().additionalInfoTag(event);

        mApiResponseHandler.requestGetService(
                AllSubredditsRequest.builder()
                        .listing(event)
                        .build()    // build request
                        .setAdditionalInfo(additionalInfo)  // add additional info
        );
    }

    private void requestFollowStateChange(FollowEvent event) {
        DatabaseIntentService.Builder builder;
        @StringRes int toastResult;
        String displayName = event.getName();

        if (event.getFollow()) {
            ContentValues cv = FollowBuilder.builder()
                    .uuid(RedditClient.getClient().getUserId())
                    .subreddit(displayName)
                    .keyColour(event.getKeyColour())
                    .iconImg(event.getIcon())
                    .build();
            builder = DatabaseIntentService.Builder.builder(
                    this,
                    DatabaseIntentService.Actions.INSERT_OR_UPDATE_FOLLOW)
                    .cv(cv);

            toastResult = R.string.following_toast;
        } else {
            builder = DatabaseIntentService.Builder.builder(
                    this,
                    DatabaseIntentService.Actions.DELETE_FOLLOW);

            toastResult = R.string.unfollowing_toast;

            // TODO should include uuid in delete sql
        }
        startService(builder
            .selection(BaseProvider.FollowBase.SUBREDDIT_EQ_SELECTION)
            .selectionArgs(displayName)
            .resultReceiver(new ToastReceiver(
                    this,
                    MessageFormat.format(getString(toastResult),
                            displayName)))
            .build());
    }


    private final ICallback<Response<? extends BaseObject<?>>> mApiResponseHandler =
            new ApiResponseCallback<>(this, FollowEvent.getFactory());

    private final ICallback<Response<? extends BaseObject<?>>> mApiStdEventResponseHandler =
            new ApiResponseCallback<>(this, StandardEvent.getFactory());

    private final QueryCallback<StandardEvent> mCpStdEventHandler =
            new QueryCallback<>(this, StandardEvent.getFactory());

    private final StandardEventProcessor mStdEventProcessor =
            new StandardEventProcessor(this, mApiStdEventResponseHandler, mCpStdEventHandler);

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends AbstractSectionPagerAdapter {

//        public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity, FragmentManager mFragmentManager) {
//            super(fragmentActivity, mFragmentManager);
//        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return AbstractFollowTabFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return Tabs.values().length;
        }
    }

    @Nullable
    @Override
    public Fragment getFragment(int position) {
        return mSectionsPagerAdapter.getFragment(position);
    }

    @Override
    public int getCount() {
        return mSectionsPagerAdapter.getCount();
    }


}
