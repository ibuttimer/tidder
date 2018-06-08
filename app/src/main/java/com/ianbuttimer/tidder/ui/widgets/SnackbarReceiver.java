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

package com.ianbuttimer.tidder.ui.widgets;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.ianbuttimer.tidder.R;

/**
 * A class to display the a result in a Toast
 */

public class SnackbarReceiver extends AbstractDisplayReceiver {

    protected View mView;

    public SnackbarReceiver(Context context, View view, String okMessage) {
        this(context, view, new Handler(), okMessage);
    }

    public SnackbarReceiver(Context context, View view, Handler handler, String okMessage) {
        this(context, view, handler, okMessage, null);
    }

    public SnackbarReceiver(Context context, View view, String okMessage, String ngMesssage) {
        this(context, view, new Handler(), okMessage, ngMesssage);
    }

    public SnackbarReceiver(Context context, View view, Handler handler, String okMessage, String ngMesssage) {
        super(context, handler, okMessage, ngMesssage);
        this.mView = view;
    }

    @Override
    protected void show(String string) {
        Snackbar.make(mView, string, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
    }
}
