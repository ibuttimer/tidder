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

package com.ianbuttimer.tidder.reddit;

import android.content.Context;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Base ResultReceiver class
 */

public abstract class DelayMethodReceiver extends ResultReceiver {

    @Nullable WeakReference<Context> mContext;

    public DelayMethodReceiver(Handler handler, @Nullable Context context) {
        super(handler);
        if (context != null) {
            this.mContext = new WeakReference<>(context);
        } else {
            this.mContext = null;
        }
    }

    @Nullable
    public Context getContext() {
        Context context = null;
        if (mContext != null) {
            context = mContext.get();
        }
        return context;
    }
}
