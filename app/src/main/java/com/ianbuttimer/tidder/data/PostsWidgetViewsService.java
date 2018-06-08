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

package com.ianbuttimer.tidder.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.db.FollowColumns;
import com.ianbuttimer.tidder.data.provider.ProviderUri;
import com.ianbuttimer.tidder.net.NetworkUtils;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.RedditClient;
import com.ianbuttimer.tidder.utils.PreferenceControl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.DETAIL_ARGS;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.LINK_NAME;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.LINK_TITLE;
import static com.ianbuttimer.tidder.ui.CommentThreadProcessor.PERMALINK;
import static com.ianbuttimer.tidder.utils.Utils.getCountIndication;

public class PostsWidgetViewsService extends RemoteViewsService {

    public static final String LAYOUT_EXTRA = "layout_extra";
    public static final String WIDGET_EXTRA = "widget_extra";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        @LayoutRes int layoutId = intent.getIntExtra(LAYOUT_EXTRA, R.layout.widget_list_item);
        int widgetId = intent.getIntExtra(WIDGET_EXTRA, 0);
        return new PostsWidgetViewsFactory(this, layoutId, widgetId);
    }

    /**
     * Get a launcher intent for this service
     * @param context   Current context
     * @return  intent
     */
    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, PostsWidgetViewsService.class);
    }

    /**
     * Get a launcher intent for this service
     * @param context   Current context
     * @param layoutId  Id of layout to use
     * @return  intent
     */
    public static Intent getLaunchIntent(Context context, @LayoutRes int layoutId, int widgetId) {
        Intent intent = getLaunchIntent(context);
        // setting the data to a "unique" uri avoids caching issues
        intent.setData(Uri.fromParts(ContentResolver.SCHEME_CONTENT,
                    NetworkUtils.joinUrlPaths(new String[] {
                        context.getString(R.string.widget_authority_provider),
                        String.valueOf(widgetId),
                        String.valueOf(layoutId)
                }), null));
        intent.putExtra(LAYOUT_EXTRA, layoutId);
        intent.putExtra(WIDGET_EXTRA, widgetId);

        Timber.i("Launch intent %s", intent);
        return intent;
    }

    private static final int[] sTextViewIds;
    private static final int[] sClickIds;

    static {
        int[] tvIds = new int[] {
                R.id.tv_title_link_item,
                R.id.tv_score_basic_stats,
                R.id.tv_comments_basic_stats,
                R.id.tv_subreddit_link_item
        };
        int[] clickIds = new int[] {
                R.id.img_thumbnail_link_item,
                R.id.img_score_link_item,
                R.id.tv_msg_widget_list_item
        };
        int[] allClickIds = Arrays.copyOf(tvIds, tvIds.length + clickIds.length);

        System.arraycopy(clickIds, 0, allClickIds, tvIds.length, clickIds.length);

        sTextViewIds = tvIds;
        sClickIds = allClickIds;
    }

    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_COMMENTS = "comments";
    private static final String COLUMN_SUBREDDIT = "subreddit";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PERMALINK = "permalink";
    private static final String COLUMN_THUMBNAIL = "thumbnail";

    public static final String[] WIDGET_CURSOR_COLUMNS = new String[] {
        COLUMN_TITLE, COLUMN_SCORE, COLUMN_COMMENTS, COLUMN_SUBREDDIT,  // first 4 same order as sTextViewIds
            COLUMN_NAME, COLUMN_PERMALINK, COLUMN_THUMBNAIL
    };

    private static final int TITLE_INDEX = 0;       // index of title in WIDGET_CURSOR_COLUMNS
    private static final int NAME_INDEX = 4;        // index of name in WIDGET_CURSOR_COLUMNS
    private static final int PERMALINK_INDEX = 5;   // index of permalink in WIDGET_CURSOR_COLUMNS
    private static final int THUMBNAIL_INDEX = 6;   // index of thumbnail in WIDGET_CURSOR_COLUMNS

    public static Object[] makeCursorRow(Link link) {
        Object[] row = new Object[WIDGET_CURSOR_COLUMNS.length];
        if (link != null) {
            int index = 0;
            row[index++] = link.getTitle();
            row[index++] = getCountIndication(link.getScore(), 0, 0);
            row[index++] = getCountIndication(link.getNumComments(), R.string.item_comment, R.string.item_comments);
            row[index++] = link.getSubredditNamePrefixed();
            row[index++] = link.getName();
            row[index++] = link.getPermalink();
            String thumbnail = "";
            if (link.isLoadableThumbnail()) {
                Uri uri = link.getThumbnail();
                if (uri != null) {
                    thumbnail = uri.toString();
                }
            }
            row[index++] = thumbnail;
        } else {
            for (int i = 0; i < row.length; i++) {
                row[i] = "";
            }
        }
        return row;
    }

    public static boolean isEmptyCursorRow(Cursor cursor) {
        boolean empty = true;
        int colCount = cursor.getColumnCount();
        for (int i = 0; (i < colCount) && empty; i++) {
            empty = TextUtils.isEmpty(cursor.getString(i));
        }
        return empty;
    }


    /**
     * Class to generate ingredient views for the app widget
     */
    private class PostsWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private WeakReference<Context> mContext;
        private Cursor mCursor;
        @LayoutRes private int mLayoutId;   // layout id of list item
        private int mWidgetId;

        /**
         * Constructor
         * @param context  The current context
         */
        PostsWidgetViewsFactory(Context context, @LayoutRes int layoutId, int widgetId) {
            this.mContext = new WeakReference<>(context.getApplicationContext());
            this.mLayoutId = layoutId;
            this.mWidgetId = widgetId;
        }

        @Override
        public void onCreate() {
            // no op
        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) {
                mCursor.close();
            }

            Context context = mContext.get();
            if (RedditClient.getClient().isAuthorised()) {

                Cursor cursor = context.getContentResolver()
                        .query(ProviderUri.FOLLOW_CONTENT_URI, null, null, null, null);


                if (cursor != null) {
                    // create posts cursor
                    int count = cursor.getCount();

                    mCursor = new MatrixCursor(WIDGET_CURSOR_COLUMNS, count);

                    if (count > 0) {
                        int index = cursor.getColumnIndex(FollowColumns.SUBREDDIT);

                        ArrayList<String> subreddits = new ArrayList<>(count);

                        while (cursor.moveToNext()) {
                            subreddits.add(cursor.getString(index));
                        }

                        mCursor = new PostsCollector(context, subreddits,
                                PreferenceControl.getPostSourcePreference(context))
                                .getPosts();
                    }
                    cursor.close();
                }
            } else {
                MatrixCursor cursor = new MatrixCursor(WIDGET_CURSOR_COLUMNS, 1);
                cursor.addRow(makeCursorRow(null));

                mCursor = cursor;
            }
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mCursor != null) {
                count = mCursor.getCount();
            }
            return count;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = null;

            if (getCount() > 0) {
                if (mCursor.moveToPosition(position)) {
                    Context context = mContext.get();

                    views = new RemoteViews(context.getPackageName(), mLayoutId);

                    Intent fillInIntent = new Intent();
                    int dataVis;
                    int msgVis;
                    if (isEmptyCursorRow(mCursor)) {
                        dataVis = View.INVISIBLE;
                        msgVis = View.VISIBLE;
                    } else {
                        dataVis = View.VISIBLE;
                        msgVis = View.INVISIBLE;

                        // same order as sTextViewIds
                        String title = "";
                        String name = "";
                        String permalink = "";
                        String thumbnail = "";
                        for (int i = 0; i < WIDGET_CURSOR_COLUMNS.length; i++) {
                            int index = mCursor.getColumnIndex(WIDGET_CURSOR_COLUMNS[i]);
                            String value = mCursor.getString(index);
                            switch (i) {
                                case TITLE_INDEX:
                                    title = value;
                                    break;
                                case NAME_INDEX:
                                    name = value;
                                    break;
                                case PERMALINK_INDEX:
                                    permalink = value;
                                    break;
                                case THUMBNAIL_INDEX:
                                    thumbnail = value;
                                    break;
                            }
                            if (i < sTextViewIds.length) {
                                views.setTextViewText(sTextViewIds[i], value);
                            }

                            // FIXME: 23/05/2018 Using Glide to download images into the list items, results in everything bar the images 'disappearing'
//                            if (!TextUtils.isEmpty(thumbnail)) {
//                                Uri uri = Uri.parse(thumbnail);
//
//                                AppWidgetTarget appWidgetTarget;
//                                appWidgetTarget = new AppWidgetTarget(context, R.id.img_thumbnail_link_item, views, mWidgetId) {
//                                    @Override
//                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                                        super.onResourceReady(resource, transition);
//                                    }
//                                };
//
//                                Handler handler = new Handler(Looper.getMainLooper());
//                                handler.post(
//                                        new WidgetImageLoader(context, appWidgetTarget, views, uri));
//                            }
                        }

                        // set the template fill in intent
                        Bundle args = new Bundle();
                        args.putString(LINK_NAME, name);
                        args.putString(LINK_TITLE, title);
                        args.putString(PERMALINK, permalink);
                        fillInIntent.putExtra(DETAIL_ARGS, args);

                    }

                    for (int id : sClickIds) {
                        views.setOnClickFillInIntent(id, fillInIntent);
                    }

                    views.setViewVisibility(R.id.ll_data_widget_list_item, dataVis);

                    views.setViewVisibility(R.id.tv_msg_widget_list_item, msgVis);
                }
            }
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;   // only 1 view
        }

        @Override
        public long getItemId(int i) {
            return i;   // use index as id
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

    }

}
