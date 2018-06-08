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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.event.PostEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;

public class CommentThreadDialog extends AppCompatDialogFragment
        implements PostOffice.IAddressable, CommentThreadProcessor.ICommentThread {

    public static final String TAG = CommentThreadDialog.class.getSimpleName();

    protected CommentThreadProcessor mProcessor;

    public CommentThreadDialog() {
        mProcessor = new CommentThreadProcessor(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProcessor.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mProcessor.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mProcessor.onCreateView(inflater, container, savedInstanceState);
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

    @Override
    public int getLayoutId() {
        return R.layout.dialog_thread;
//        return R.layout.content_thread;
    }

    @Override
    public void bind(View view) {
        // no op
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
        // no op
    }

    @Override
    public void onStop() {
        mProcessor.onStop();
        super.onStop();
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
