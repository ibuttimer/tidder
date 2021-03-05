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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ianbuttimer.tidder.data.adapter.CommentViewHolder;
import com.ianbuttimer.tidder.databinding.CommentListItemBinding;
import com.ianbuttimer.tidder.databinding.ContentThreadBinding;
import com.ianbuttimer.tidder.event.PostEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;
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

    private ContentThreadBinding binding;

    protected CommentThreadProcessor<Comment, ContentThreadBinding, ?> mProcessor;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommentThreadFragment() {
        mProcessor = new CommentThreadProcessor<>(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mProcessor.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProcessor.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mProcessor.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public ViewBinding getViewBinding() {
        binding = ContentThreadBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    public ConstraintLayout getContents() {
        return binding.clContentPostOrThread;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.incListingLayout.rvListListingL;
    }

    @Override
    public ProgressBar getProgressBar() {
        return binding.incListingLayout.pbProgressListingL;
    }

    @Override
    public TextView getMessageTv() {
        return binding.incListingLayout.tvMessageListingL;
    }

    @Override
    public TextView getTitleTv() {
        return binding.tvTitlePostOrThread;
    }

    @Override
    public BasicStatsView getBasicStatsView() {
        return binding.bsvPostOrThread;
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
            ((ImageButton)fabPin).setVisibility(View.GONE);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
