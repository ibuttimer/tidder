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

package com.ianbuttimer.tidder.widget;

import android.content.Context;
import android.net.Uri;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.ianbuttimer.tidder.net.GlideApp;
import com.ianbuttimer.tidder.net.NetworkUtils;

/**
 * Loader to load images into app widgets
 */
public class WidgetImageLoader implements Runnable {

    private final Context mContext;
    private final AppWidgetTarget mAppWidgetTarget;
    private final RemoteViews mRemoteViews;
    private final Uri mUri;

    /**
     * Constructor
     * @param context           Current context
     * @param appWidgetTarget   Target
     * @param remoteViews       Remote view
     * @param uri               Uri to load
     */
    public WidgetImageLoader(Context context, AppWidgetTarget appWidgetTarget, RemoteViews remoteViews, Uri uri) {
        this.mContext = context;
        this.mAppWidgetTarget = appWidgetTarget;
        this.mRemoteViews = remoteViews;
        this.mUri = NetworkUtils.unescapeUri(uri);
    }

    @Override
    public void run() {
        GlideApp.with(mContext.getApplicationContext())
                .asBitmap()
                .centerCrop()
                .load(mUri)
                .into(mAppWidgetTarget);

    }
}
