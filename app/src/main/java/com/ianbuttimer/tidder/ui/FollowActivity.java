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

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.ApiResponseCallback;
import com.ianbuttimer.tidder.data.DatabaseIntentService;
import com.ianbuttimer.tidder.data.ICallback;
import com.ianbuttimer.tidder.data.QueryCallback;
import com.ianbuttimer.tidder.data.provider.BaseProvider;
import com.ianbuttimer.tidder.data.provider.FollowBuilder;
import com.ianbuttimer.tidder.event.FollowEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.event.StandardEventProcessor;
import com.ianbuttimer.tidder.reddit.RedditClient;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.get.AllSubredditsRequest;
import com.ianbuttimer.tidder.reddit.get.SubredditsSearchRequest;
import com.ianbuttimer.tidder.reddit.post.ApiSearchSubredditsRequest;
import com.ianbuttimer.tidder.ui.util.AbstractSectionPagerAdapter;
import com.ianbuttimer.tidder.ui.util.ISectionsPagerAdapter;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.ui.widgets.ToastReceiver;
import com.ianbuttimer.tidder.utils.PreferenceControl;

import org.greenrobot.eventbus.Subscribe;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowActivity extends AppCompatActivity implements ISectionsPagerAdapter {

    private static final String TAG = FollowActivity.class.getSimpleName();
    private static final String TAG_FOLLOW_EVENT = TAG + ":onFollowEvent";

    public enum Tabs { LIST, SEARCH, ALL;

        @Nullable public static Tabs valueOf(int ordinal) {
            Tabs value = null;
            for (Tabs tab : values()) {
                if (tab.ordinal() == ordinal) {
                    value = tab;
                }
            }
            return value;
        }
    }

    @BindView(R.id.fab_delete_followA) FloatingActionButton fabDelete;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_followA);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_followA);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_followA);

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
                fabDelete.setVisibility(visibility);
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                DatabaseIntentService.Builder builder =
                        DatabaseIntentService.Builder.builder(
                                context,
                                DatabaseIntentService.Actions.DELETE_ALL_FOLLOW);
                context.startService(builder
                        .resultReceiver(new ToastReceiver(context,
                                context.getResources().getString(R.string.unfollowing_all_toast)))
                        .build());
            }
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


    private ICallback<Response> mApiResponseHandler =
            new ApiResponseCallback<>(this, FollowEvent.getFactory());

    private ICallback<Response> mApiStdEventResponseHandler =
            new ApiResponseCallback<>(this, StandardEvent.getFactory());

    private QueryCallback<StandardEvent> mCpStdEventHandler =
            new QueryCallback<>(this, StandardEvent.getFactory());

    private StandardEventProcessor mStdEventProcessor =
            new StandardEventProcessor(this, mApiStdEventResponseHandler, mCpStdEventHandler);

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends AbstractSectionPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return FollowTabFragment.newInstance(position);
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
