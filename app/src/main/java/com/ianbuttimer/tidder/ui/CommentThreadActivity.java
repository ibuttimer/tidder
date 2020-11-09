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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.DETAIL_ARGS;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.PARENT_ARGS;

public class CommentThreadActivity extends PostDetailActivity {

    protected Bundle mParentArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = savedInstanceState;
        if (b == null) {
            b = getIntent().getExtras();
        }
        if ((b != null) && (b.containsKey(PARENT_ARGS))) {
            mParentArgs = b.getBundle(PARENT_ARGS);
        }
    }

    @Override
    protected Fragment getNewFragment() {
        return new CommentThreadFragment();
    }

    @Override
    protected Bundle getFragmentArgs() {
        Bundle args = super.getFragmentArgs();

        args.putBoolean(CommentThreadProcessor.THREAD, true);

        return args;
    }


    @Override
    public Intent getParentActivityIntent() {
        // required for back navigation
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra(DETAIL_ARGS, mParentArgs);
        }
        return intent;
    }

}
