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

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.MenuItem;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.event.PostEvent;
import com.ianbuttimer.tidder.reddit.RedditClient;
import com.ianbuttimer.tidder.ui.util.DoubleEnterKeyInterceptor;
import com.ianbuttimer.tidder.ui.util.KeyInterceptor;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.Utils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.DETAIL_ARGS;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.LINK_NAME;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.LINK_TITLE;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.PERMALINK;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.THREAD;


/**
 * An activity representing a single Post detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PostListActivity}.
 */
public class PostDetailActivity extends AppCompatActivity implements KeyInterceptor.IKeyInterceptor {

    public static final String TAG = PostDetailActivity.class.getSimpleName();

    protected static final String[] FRAG_ARGS = new String[] {
            LINK_NAME, LINK_TITLE, PERMALINK
    };

    private DoubleEnterKeyInterceptor mInterceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!RedditClient.getClient().isAuthorised()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Utils.startActivity(this, intent);

            finish();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_postDetailA);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mInterceptor = new DoubleEnterKeyInterceptor(findViewById(R.id.layout_postDetailsA), this);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle argsOut = getFragmentArgs();

            Fragment fragment = getNewFragment();
            fragment.setArguments(argsOut);
            getSupportFragmentManager().beginTransaction()
                    .add(getContainerId(), fragment)
                    .commit();
        }
    }

    protected Bundle getFragmentArgs() {
        Bundle argsOut = new Bundle();
        Bundle argsIn = getIntent().getBundleExtra(DETAIL_ARGS);
        if (argsIn != null) {
            for (String arg : FRAG_ARGS) {
                argsOut.putString(arg, argsIn.getString(arg));
            }
            argsOut.putBoolean(THREAD, argsIn.getBoolean(THREAD));
        }
        return argsOut;
    }

    protected Fragment getNewFragment() {
        return new PostDetailFragment();
    }

    @IdRes
    protected int getContainerId() {
        return R.id.post_detail_container;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = Utils.onOptionsItemSelected(this, item);
        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean consumed = mInterceptor.dispatchKeyEvent(event);
        if (!consumed) {
            consumed = super.dispatchKeyEvent(event);
        }
        return consumed;
    }

    @Override
    public void onIntercept() {
        PostOffice.postEvent(PostEvent.newViewThreadRequest()
                                    .setAddress(PostDetailFragment.getTabAddress()));
    }
}
