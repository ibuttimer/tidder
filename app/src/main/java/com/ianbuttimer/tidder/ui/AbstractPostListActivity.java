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

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.ApiResponseCallback;
import com.ianbuttimer.tidder.data.ICallback;
import com.ianbuttimer.tidder.data.QueryCallback;
import com.ianbuttimer.tidder.databinding.ActivityPostListBinding;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.event.PostsEvent;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.event.StandardEventProcessor;
import com.ianbuttimer.tidder.event.StandardEventProcessor.IStandardEventProcessorExt;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.RedditClient;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.get.SubredditLinkRequest;
import com.ianbuttimer.tidder.ui.util.AbstractSectionPagerAdapter;
import com.ianbuttimer.tidder.ui.util.ISectionsPagerAdapter;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.Dialog;
import com.ianbuttimer.tidder.utils.Utils;
import com.ianbuttimer.tidder.widget.PostsWidgetProvider;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.EXTRA_TITLE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.DETAIL_ARGS;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.LINK_NAME;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.LINK_TITLE;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.PERMALINK;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.TWO_PANE;

/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public abstract class AbstractPostListActivity extends AppCompatActivity
        implements PostOffice.IAddressable, ISectionsPagerAdapter, CommentThreadProcessor.ICommentThreadHost {

    private static final String TAG_POSTS_EVENT =
            AbstractPostListActivity.class.getSimpleName() + ":onPostsEvent";

    protected ActivityPostListBinding binding;

    public enum Tabs { NEW_POSTS, PINNED_POSTS;

        @Nullable
        public static Tabs valueOf(int ordinal) {
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

    private ICallback<Response<? extends BaseObject<?>>> mApiResponseHandler;
    private StandardEventProcessor mStdEventProcessor;

    @Nullable protected FloatingActionButton fabPin;
    @Nullable protected FloatingActionButton fabRefresh;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPostListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fabPin = binding.incLayoutActivityPostListPinFab.fabPinPostDetailA;
        fabRefresh = binding.incLayoutActivityPostListRefreshFab.fabRefreshPostListA;

        Toolbar toolbar = binding.toolbarPostListA;
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Create the adapter that will return a fragment for each of the primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, that will host the section contents, with the sections adapter.
        ViewPager mViewPager = binding.incLayoutPostList.containerPostList;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = binding.tabsPostListA;

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int visibility;
                if (Tabs.NEW_POSTS.equals(Tabs.valueOf(position))) {
                    visibility = View.VISIBLE;
                } else {
                    visibility = View.INVISIBLE;
                }

                ((ImageButton)fabRefresh).setVisibility(visibility);
            }
        });

        fabRefresh.setOnClickListener(view1 ->
                EventBus.getDefault()
                        .post(PostsEvent.newRefreshPostsCommand()
                        .setAddress(Tabs.NEW_POSTS.name()))
        );

        if (findViewById(R.id.post_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        mApiResponseHandler = new ApiResponseCallback<>(this, PostsEvent.getFactory());

        mStdEventProcessor = new StandardEventProcessor(this,
                new ApiResponseCallback<>(this, StandardEvent.getFactory()),
                new QueryCallback<>(this, StandardEvent.getFactory()));

        ArrayList<IStandardEventProcessorExt> extensions = getExtensions();
        if (extensions != null) {
            for (IStandardEventProcessorExt ext :
                    extensions) {
                mStdEventProcessor.addExtension(ext);
            }
        }

        if (RedditClient.getClient().isAuthorised()) {
            // get user details
            RedditClient.getClient().retrieveUser(this);

            // refresh any widgets
            updateAppWidgets();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_posts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean handled = Utils.onOptionsItemSelected(this, item);
        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

    /**
     * Return list of standard event processor extensions
     * @return  List of extensions
     */
    protected abstract ArrayList<IStandardEventProcessorExt> getExtensions();

    @Subscribe
    public void onMessageEvent(RedditClientEvent event) {

        if (PostOffice.deliverEventOrBroadcast(event, getAddress())) {
            if (event.isLogoutEvent()) {
                // logged out, so return to login screen
                updateAppWidgets();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(EXTRA_TITLE, R.string.info_title);
                intent.putExtra(EXTRA_TEXT, R.string.logged_out);

                Utils.startActivity(this, intent);
            } else if (event.isAuthErrorEvent()) {
                Dialog.showAlertDialog(this, event.getErrorMessage());
            }
        }
    }

    protected void updateAppWidgets() {
        PostsWidgetProvider.notifyAppWidgetViewDataChanged(
                        AppWidgetManager.getInstance(this),
                        PostsWidgetProvider.getAppWidgetIds(this));
    }

    @Subscribe
    public void processMessageEvent(AbstractEvent<?> event) {
        if (event instanceof PostsEvent) {
            onPostsEvent((PostsEvent)event);
        } else if (event instanceof StandardEvent) {
            onStandardEvent((StandardEvent)event);
        }
    }

    public void onStandardEvent(StandardEvent event) {
        mStdEventProcessor.onStandardEvent(event);
    }

    public void onPostsEvent(PostsEvent event) {
        boolean handled = true;

        PostOffice.logDeliver(event, TAG_POSTS_EVENT, true);

        if (event.isViewPostRequest()) {
            Bundle args = getDetailArgs(event);
            if (mTwoPane) {
                PostDetailFragment fragment = new PostDetailFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.post_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(DETAIL_ARGS, args);
                Utils.startActivity(this, intent);
            }
        } else if (event.isGetPostRequest()) {
            // NEW POST FLOW 5. send request for post from subreddit following list
            // additional info bundle for request
            Bundle additionalInfo = PostsEvent.getFactory().additionalInfoAll(event);
            mApiResponseHandler.requestGetService(
                    SubredditLinkRequest.builder()
                            .subreddit(event.getName(), event.getSource())
                            .listing(event)
                            .build()    // build request
                            .setAdditionalInfo(additionalInfo)  // add additional info
            );
        } else if (event.isClearPostsCommand()) {
            if (fabPin != null) {
                ((ImageButton)fabPin).setVisibility(View.INVISIBLE);
            }
        } else {
            handled = false;
        }

        PostOffice.logHandled(event, TAG_POSTS_EVENT, handled);
    }

    private Bundle getDetailArgs(PostsEvent event) {
        Bundle arguments = new Bundle();
        arguments.putString(LINK_NAME, event.getName());
        arguments.putString(LINK_TITLE, event.getTitle());
        arguments.putString(PERMALINK, event.getSource());
        arguments.putBoolean(TWO_PANE, mTwoPane);
        return arguments;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends AbstractSectionPagerAdapter {

        private Object mCurrentPrimaryItem = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public @NonNull Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            AbstractBasePostsTabFragment fragment;
            Tabs tab = Tabs.valueOf(position);

            if (Tabs.NEW_POSTS.equals(tab)) {
                fragment = new PostsNewTabFragment();
            } else if (Tabs.PINNED_POSTS.equals(tab)) {
                fragment = new PostsPinnedTabFragment();
            } else if (tab == null){
                throw new IllegalStateException("No corresponding tab for position " + position);
            } else {
                throw new IllegalStateException("Unknown tab: " + tab.name());
            }
            if (mTwoPane) {
                Bundle args = new Bundle();
                args.putBoolean(TWO_PANE, mTwoPane);
                fragment.setArguments(args);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return Tabs.values().length;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);

            if (mTwoPane) {
                if (!object.equals(mCurrentPrimaryItem)) {
                    mCurrentPrimaryItem = object;

                    // clear detail fragment
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentById(R.id.post_detail_container);
                    if (fragment != null) {
                        fm.beginTransaction()
                                .remove(fragment)
                                .commit();
                    }

                    PostOffice.postEvent(PostsEvent.newClearPostsCommand()
                                            .addAddress(PostsPinnedTabFragment.getTabAddress(),
                                                            PostsNewTabFragment.getTabAddress()));
                }
            }
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

    @Override
    public FloatingActionButton getFabPin() {
        return fabPin;
    }

    @Override
    public FloatingActionButton getFabRefresh() {
        return fabRefresh;
    }

    @Override
    public CollapsingToolbarLayout getAppBarLayout() {
        return null;
    }
}
