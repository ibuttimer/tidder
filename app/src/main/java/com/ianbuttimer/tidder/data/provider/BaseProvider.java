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

package com.ianbuttimer.tidder.data.provider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.ianbuttimer.tidder.data.db.AbstractDatabase;
import com.ianbuttimer.tidder.net.NetworkUtils;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.BaseColumns._ID;
import static com.ianbuttimer.tidder.data.db.FollowColumns.SUBREDDIT;
import static com.ianbuttimer.tidder.data.db.PinnedColumns.FULLNAME;
import static com.ianbuttimer.tidder.net.NetworkUtils.PATH_JOIN;

public class BaseProvider {

    public static final String TYPE_DIR = "vnd.android.cursor.dir/";
    public static final String TYPE_ITEM = "vnd.android.cursor.item/";

    public static final String[] ID_PROJECTION = new String[] { _ID };

    /** String for a selection by id */
    public static final String EQ_SELECTION = "=?";

    public static final String FRAGMENT_MARKER = "#";

    private final static String EQ_SELECTION_MATCH = "\\s*(\\w+)\\s*=\\s*\\?";
    private static Pattern sEqSelectionPattern = Pattern.compile(EQ_SELECTION_MATCH,
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private final static String IN_SELECTION_MATCH = "\\s*(\\w+)\\s+IN\\s*\\((.+)\\)\\s*";
    private static Pattern sInSelectionPattern = Pattern.compile(IN_SELECTION_MATCH,
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


    /** String for a selection by id */
    public static final String ID_EQ_SELECTION = columnEqSelection(_ID);

    interface Path {
        String FOLLOW = AbstractDatabase.TableNames.FOLLOW;
        /** Path for Uri 'follow + id fragment' */
        String FOLLOW_FRAGMENT = FOLLOW + PATH_JOIN + FRAGMENT_MARKER;
        /** Follow id fragment path segment index */
        int FOLLOW_FRAGMENT_INDEX = 1;

        String PINNED = AbstractDatabase.TableNames.PINNED;
        /** Path for Uri 'pinned + id fragment' */
        String PINNED_FRAGMENT = PINNED + PATH_JOIN + FRAGMENT_MARKER;
        /** Pinned id fragment path segment index */
        int PINNED_FRAGMENT_INDEX = 1;
    }

    public static class FollowBase {

        public static final String[] SUBREDDIT_PROJECTION = new String[] { SUBREDDIT };
        public static final String SUBREDDIT_EQ_SELECTION = columnEqSelection(SUBREDDIT);
    }

    public static class PinnedBase {

        public static final String NAME_EQ_SELECTION = columnEqSelection(FULLNAME);
    }

    private BaseProvider() {
        // can't instantiate
    }


    public static Uri buildUri(Uri base, String... paths) {
        Uri.Builder builder = base.buildUpon();
        for (String path : paths) {
            // if the path has multiple parts, need to add them individually, otherwise it gets encoded
            String[] splits = path.split("/");
            for (String split : splits) {
                builder.appendPath(split);
            }
        }
        return builder.build();
    }


    public static Uri[] onInsert(Uri uri, ContentValues values) {
        return new Uri[] {
                uri,
        };
    }

    public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids) {
        return new Uri[] {
                uri
        };
    }

    public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, String[] ids) {
        return new Uri[] {
                uri
        };
    }

    public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs) {
        return new Uri[] {
                uri
        };
    }

    public static Uri[] onDelete(Context context, Uri uri) {
        return new Uri[] {
                uri
        };
    }

    /**
     * Make a fragment path
     * @param path  PAth to append fragment marker to
     * @return  Fragment path string
     */
    public static String fragmentPath(String path) {
        return NetworkUtils.joinUrlPaths(path, FRAGMENT_MARKER);
    }

    /**
     * Make a column equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnEqSelection(String column) {
        return column + EQ_SELECTION;
    }

    /**
     * Check if the argument represents a column equals selection
     * @param selection     Selection argument
     * @return  Column name selection is being done on, or <code>null</code> if not a column equals selection
     */
    @Nullable public static String isColumnEqSelection(String selection) {
        String name = null;
        Matcher m = sEqSelectionPattern.matcher(selection);
        if (m.find()) {
            name =  m.group(1);
        }
        return name;
    }

    /**
     * Make a column in selection argument
     * @param column    Column name
     * @param count     Number of items to delete
     * @return  Selection string
     */
    public static String columnInSelection(String column, int count) {
        return String.format(column + " IN (%s)",
                TextUtils.join(",", Collections.nCopies(count, "?")));
    }

    /**
     * Check if the argument represents a column in selection
     * @param selection     Selection argument
     * @return  Pair containing the column name selection is being done on and argument count, or <code>null</code> if not a column in selection
     */
    @Nullable public static Pair<String, Integer> isColumnInSelection(String selection) {
        Pair<String, Integer> pair = null;
        Matcher m = sInSelectionPattern.matcher(selection);
        if (m.find()) {
            String name = m.group(1);
            String count = m.group(2);
            String[] args = count.split(",");
            pair = new Pair<>(name, args.length);
        }
        return pair;
    }

    /**
     * Make a column greater than or equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnGtEqSelection(String column) {
        return column + ">=?";
    }

    /**
     * Make a column less than or equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnLtEqSelection(String column) {
        return column + "<=?";
    }

}
