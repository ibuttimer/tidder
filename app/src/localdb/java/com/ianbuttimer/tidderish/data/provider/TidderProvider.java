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

package com.ianbuttimer.tidderish.data.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.ianbuttimer.tidderish.BuildConfig;
import com.ianbuttimer.tidderish.data.db.FollowColumns;
import com.ianbuttimer.tidderish.data.db.PinnedColumns;
import com.ianbuttimer.tidderish.data.db.TidderDatabase;
import com.ianbuttimer.tidderish.data.provider.BaseProvider.Path;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

import static com.ianbuttimer.tidderish.data.provider.BaseProvider.TYPE_DIR;
import static com.ianbuttimer.tidderish.data.provider.BaseProvider.TYPE_ITEM;

/**
 * Content provider for database
 */

@ContentProvider(
        authority = TidderProvider.AUTHORITY,
        database = TidderDatabase.class,
        packageName = "com.ianbuttimer.tidderish.data.db.gen"
)
public final class TidderProvider {

    /** Base Uri for content provider */
    public static final Uri BASE_CONTENT_URI;

    public static final String AUTHORITY = BuildConfig.PROVIDER_AUTHORITY;

    static {
        Uri.Builder builder = new Uri.Builder().
                scheme(ContentResolver.SCHEME_CONTENT).
                encodedAuthority(AUTHORITY);
        BASE_CONTENT_URI = builder.build();
    }

    private TidderProvider() {
    }



    public static Uri buildUri(String... paths) {
        return BaseProvider.buildUri(BASE_CONTENT_URI, paths);
    }

    @TableEndpoint(table = TidderDatabase.Tables.FOLLOW)
    public static class Follow extends BaseProvider.FollowBase {

        @ContentUri(
                path = Path.FOLLOW,
                type = TYPE_DIR + Path.FOLLOW)
        public static final Uri CONTENT_URI = buildUri(Path.FOLLOW);

        @InexactContentUri(
                name = TidderDatabase.Tables.FOLLOW + FollowColumns.ID,
                path = Path.FOLLOW_FRAGMENT,
                type = TYPE_ITEM + Path.FOLLOW,
                whereColumn = FollowColumns.ID,
                pathSegment = BaseProvider.Path.FOLLOW_FRAGMENT_INDEX)
        public static Uri withId(long id) {
            return buildUri(Path.FOLLOW, String.valueOf(id));
        }

        @NotifyInsert(paths = Path.FOLLOW)
        public static Uri[] onInsert(Uri uri, ContentValues values) {
            return BaseProvider.onInsert(uri, values);
        }

        @NotifyBulkInsert(paths = Path.FOLLOW)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids) {
            return BaseProvider.onBulkInsert(context, uri, values, ids);
        }

        @NotifyUpdate(paths = Path.FOLLOW_FRAGMENT)
        public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs) {
            return BaseProvider.onUpdate(context, uri, where, whereArgs);
        }

        @NotifyDelete(paths = Path.FOLLOW_FRAGMENT)
        public static Uri[] onDelete(Context context, Uri uri) {
            return BaseProvider.onDelete(context, uri);
        }
    }

    @TableEndpoint(table = TidderDatabase.Tables.PINNED)
    public static class Pinned extends BaseProvider.PinnedBase {

        @ContentUri(
                path = Path.PINNED,
                type = TYPE_DIR + Path.PINNED)
        public static final Uri CONTENT_URI = buildUri(Path.PINNED);

        @InexactContentUri(
                name = TidderDatabase.Tables.PINNED + PinnedColumns.ID,
                path = Path.PINNED_FRAGMENT,
                type = TYPE_ITEM + Path.PINNED,
                whereColumn = PinnedColumns.ID,
                pathSegment = BaseProvider.Path.PINNED_FRAGMENT_INDEX)
        public static Uri withId(long id) {
            return buildUri(Path.PINNED, String.valueOf(id));
        }

        @NotifyInsert(paths = Path.PINNED)
        public static Uri[] onInsert(Uri uri, ContentValues values) {
            return BaseProvider.onInsert(uri, values);
        }

        @NotifyBulkInsert(paths = Path.PINNED)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids) {
            return BaseProvider.onBulkInsert(context, uri, values, ids);
        }

        @NotifyUpdate(paths = Path.PINNED_FRAGMENT)
        public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs) {
            return BaseProvider.onUpdate(context, uri, where, whereArgs);
        }

        @NotifyDelete(paths = Path.PINNED_FRAGMENT)
        public static Uri[] onDelete(Context context, Uri uri) {
            return BaseProvider.onDelete(context, uri);
        }
    }

}

