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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.ianbuttimer.tidder.data.adapter.AbstractViewHolder;
import com.ianbuttimer.tidder.data.adapter.CommentViewHolder;
import com.ianbuttimer.tidder.databinding.CommentListItemBinding;
import com.ianbuttimer.tidder.databinding.DialogThreadBinding;
import com.ianbuttimer.tidder.event.PostEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.ScreenUtils;

import java.util.Objects;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CommentThreadDialog extends AppCompatDialogFragment
        implements PostOffice.IAddressable, CommentThreadProcessor.ICommentThread {

    public static final String TAG = CommentThreadDialog.class.getSimpleName();

    private DialogThreadBinding binding;

    protected CommentThreadProcessor<Comment, CommentListItemBinding, CommentViewHolder> mProcessor;

    public CommentThreadDialog() {
        mProcessor = new CommentThreadProcessor<>(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProcessor.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mProcessor.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mProcessor.onCreateView(inflater, container, savedInstanceState);
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

    @Override
    public ViewBinding getViewBinding() {
        binding = DialogThreadBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    public ConstraintLayout getContents() {
        return binding.incLayoutContentThread.clContentPostOrThread;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.incLayoutContentThread.incListingLayout.rvListListingL;
    }

    @Override
    public ProgressBar getProgressBar() {
        return binding.incLayoutContentThread.incListingLayout.pbProgressListingL;
    }

    @Override
    public TextView getMessageTv() {
        return binding.incLayoutContentThread.incListingLayout.tvMessageListingL;
    }

    @Override
    public TextView getTitleTv() {
        return binding.incLayoutContentThread.tvTitlePostOrThread;
    }

    @Override
    public BasicStatsView getBasicStatsView() {
        return binding.incLayoutContentThread.bsvPostOrThread;
    }

    @Override
    public void onActivityCreated() {
        // no op
    }

    @Override
    public void onStart() {
        super.onStart();
        mProcessor.onStart();
    }

    @Override
    public void onStart(boolean emptyList) {
        // Set the dialog width
        ScreenUtils.setDialogSize(Objects.requireNonNull(getDialog()), 0.75f, WRAP_CONTENT, Gravity.CENTER);
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
    public boolean onPostEvent(PostEvent event) {
        return false;
    }

    @Override
    public boolean onStandardEvent(StandardEvent event) {
        return false;
    }

    @Override
    public void processGetCommentTreeResult(Link link) {
        // no op
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
