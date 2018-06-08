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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.event.PostEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;

/**
 * A fragment representing a single Post detail screen.
 * This fragment is either contained in a {@link PostListActivity}
 * in two-pane mode (on tablets) or a {@link CommentThreadActivity}
 * on handsets.
 */
public class CommentThreadFragment extends Fragment
        implements PostOffice.IAddressable, CommentThreadProcessor.ICommentThread {

    public static final String TAG = CommentThreadFragment.class.getSimpleName();

    protected CommentThreadProcessor mProcessor;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommentThreadFragment() {
        mProcessor = new CommentThreadProcessor(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mProcessor.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProcessor.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mProcessor.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void bind(View view) {
        // no op
    }

    @LayoutRes public int getLayoutId() {
        return R.layout.content_thread;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProcessor.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityCreated() {
        FloatingActionButton fabPin = mProcessor.getFabPin();
        if (fabPin != null) {
            // no pinned functionality for comment thread
            fabPin.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mProcessor.onStart();
    }

    @Override
    public void onStart(boolean emptyList) {
        // no op
    }

    @Override
    public void onStop() {
        mProcessor.onStop();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mProcessor.onSaveInstanceState(outState);

        // save any object specific additional info
    }

    @Override
    public void processSavedInstanceState(@Nullable Bundle savedInstanceState) {
        // process any object specific additional info
    }

    public boolean onPostEvent(PostEvent event) {
        return false;
    }

    public void processGetCommentTreeResult(Link link) {
        // no op
    }

    public boolean onStandardEvent(StandardEvent event) {
        return false;
    }

    @Override
    public void onItemClick(View view, Comment comment) {
        // no op
    }

    @Override
    public boolean onItemLongClick(View view, Comment comment) {
        return false;
    }

    @Override
    public void onItemDoubleClick(View view, Comment comment) {
        // no op
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        // no op
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public String getAddress() {
        return TAG;
    }

}
