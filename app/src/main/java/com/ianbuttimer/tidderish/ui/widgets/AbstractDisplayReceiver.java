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

package com.ianbuttimer.tidderish.ui.widgets;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A class to display the a result in a Toast
 */

public abstract class AbstractDisplayReceiver extends ResultReceiver {

    protected WeakReference<Context> mContext;
    @Nullable protected String mOkMessage;
    @Nullable protected String mNgMessage;

    public AbstractDisplayReceiver(Context context, String okMessage) {
        this(context, new Handler(), okMessage);
    }

    public AbstractDisplayReceiver(Context context, Handler handler, String okMessage) {
        this(context, handler, okMessage, null);
    }

    public AbstractDisplayReceiver(Context context, String okMessage, String ngMesssage) {
        this(context, new Handler(), okMessage, ngMesssage);
    }

    public AbstractDisplayReceiver(Context context, Handler handler, String okMessage, String ngMesssage) {
        super(handler);
        this.mContext = new WeakReference<>(context);
        this.mOkMessage = okMessage;
        this.mNgMessage = ngMesssage;
    }


    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if ((resultCode == RESULT_OK) && !TextUtils.isEmpty(mOkMessage)) {
            show(mOkMessage);
        } else if ((resultCode == RESULT_CANCELED) && !TextUtils.isEmpty(mNgMessage)) {
            show(mNgMessage);
        }
    }

    protected abstract void show(String string);

}
