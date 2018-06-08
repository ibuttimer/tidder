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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.widget.RemoteViews;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.PostsWidgetViewsService;
import com.ianbuttimer.tidder.ui.PostDetailActivity;

/**
 * Application widget provider
 */
public class PostsWidgetProvider extends AppWidgetProvider {

    /**
     * Update a widget
     * @param context           The current context
     * @param appWidgetManager  AppWidgetManager
     * @param appWidgetId       Widget id
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.posts_widget);

        // determine which widget layout to use
        @LayoutRes int layoutId = R.layout.widget_list_item;

        Intent intent = PostsWidgetViewsService.getLaunchIntent(context, layoutId, appWidgetId);
        views.setRemoteAdapter(R.id.appwidget_listview, intent);

        // set the widget onclick to load the recipe
        Intent appIntent = new Intent(context, PostDetailActivity.class);

        // set the request code to the widget id to ensure its unique and avoid issues with cached intents
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, appIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
        views.setPendingIntentTemplate(R.id.appwidget_listview, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Notify the AppWidgetManager that a dataset has changed
     * @param appWidgetManager  The AppWidgetManager
     * @param appWidgetIds      Widget ids
     */
    public static void notifyAppWidgetViewDataChanged(AppWidgetManager appWidgetManager, int... appWidgetIds) {
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_listview);
    }

    /**
     * Get the ids of widgets associated with this app
     * @param context   The current context
     * @return  Intent
     */
    public static int[] getAppWidgetIds(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), PostsWidgetProvider.class);
        return appWidgetManager.getAppWidgetIds(thisWidget);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        if (appWidgetIds != null) {
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // no op
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        // no op
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        // update widget to suit dimension change if applicable
        updateAppWidget(context, appWidgetManager, appWidgetId);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

}
