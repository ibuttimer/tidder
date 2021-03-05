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
import android.widget.Toast;

/**
 * A class to display the a result in a Toast
 */

public class ToastReceiver extends AbstractDisplayReceiver {

    public ToastReceiver(Context context, String okMessage) {
        super(context, okMessage);
    }

    public ToastReceiver(Context context, Handler handler, String okMessage) {
        super(context, handler, okMessage);
    }

    public ToastReceiver(Context context, String okMessage, String ngMesssage) {
        super(context, okMessage, ngMesssage);
    }

    public ToastReceiver(Context context, Handler handler, String okMessage, String ngMesssage) {
        super(context, handler, okMessage, ngMesssage);
    }

    @Override
    protected void show(String string) {
        Toast.makeText(mContext.get(), string, Toast.LENGTH_LONG).show();
    }
}
