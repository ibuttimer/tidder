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

package com.ianbuttimer.tidderish.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ianbuttimer.tidderish.data.provider.ProviderUri;
import com.ianbuttimer.tidderish.net.UriUtils;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.ianbuttimer.tidderish.data.db.DbUtils.DB_DELETE_ALL;
import static com.ianbuttimer.tidderish.utils.Utils.getParcelableArrayFromBundle;

/**
 * IntentService to handle database functionality
 */

public class DatabaseIntentService extends AbstractIntentService {

    public enum Actions {
        /** Insert a follow action */
        INSERT_FOLLOW,
        /** Insert multiple follows action */
        BULK_INSERT_FOLLOW,
        /** Insert or update a follow action */
        INSERT_OR_UPDATE_FOLLOW,
        /** Update a follow action */
        UPDATE_FOLLOW,
        /** Delete a follow action */
        DELETE_FOLLOW,
        /** Delete all follow action */
        DELETE_ALL_FOLLOW,
        /** Insert a pinned action */
        INSERT_PINNED,
        /** Insert multiple pinneds action */
        BULK_INSERT_PINNED,
        /** Insert or update a pinned action */
        INSERT_OR_UPDATE_PINNED,
        /** Update a pinned action */
        UPDATE_PINNED,
        /** Delete a pinned action */
        DELETE_PINNED,
        /** Delete all pinned action */
        DELETE_ALL_PINNED;

        boolean isFollow() {
            return this.toString().contains(FOLLOW);
        }

        boolean isPinned() {
            return this.toString().contains(PINNED);
        }
    }

    private static final String FOLLOW = "FOLLOW";
    private static final String PINNED = "PINNED";

    /** Name for selection in intent */
    public static final String SELECTION_EXTRA = "selection_extra";
    /** Name for selection in intent */
    public static final String SELECTION_ARGS_EXTRA = "selection_args_extra";
    /** Name for Uri in intent */
    public static final String URI_EXTRA = "uri_extra";
    /** Name for ContentValues in intent */
    public static final String CV_EXTRA = "cv_extra";
    /** Name for ContentValues array in intent */
    public static final String CV_ARRAY_EXTRA = "cv_array_extra";

    /** Name for result count in result bundle */
    public static final String RESULT_COUNT = "result_count";


    public DatabaseIntentService() {
        super(DatabaseIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        Extractor extractor = new Extractor(intent);
        ContentValues cv = extractor.cv();
        Cursor cursor;
        int count = 0;
        Uri uri = null;
        ResultReceiver resultReceiver = extractor.resultReceiver();
        String selection = extractor.selection();
        String[] selectionArgs = extractor.selectionArgs();
        Uri requestUri = extractor.uri();

        Actions action = null;
        try {
            action = Actions.valueOf(intent.getAction());
        } catch (IllegalArgumentException e) {
            Timber.e("Unknown service action");
        }
        switch (action) {
            // recipe related actions
            case INSERT_OR_UPDATE_FOLLOW:
            case INSERT_OR_UPDATE_PINNED:
                cursor = dbGetBySelection(action, requestUri, selection, selectionArgs);
                if (cursor != null) {
                    // row exists so update
                    count = dbUpdate(getUri(action, requestUri), cv, selection, selectionArgs);
                    break;
                }
                // else fall through to insert
            case INSERT_FOLLOW:
            case INSERT_PINNED:
                uri = dbInsert(action, cv);
                break;
            case BULK_INSERT_FOLLOW:
            case BULK_INSERT_PINNED:
                ContentValues[] cvArray = extractor.cvArray();
                if (cvArray != null) {
                    count = dbBulkInsert(action, cvArray);
                }
                break;
            case UPDATE_FOLLOW:
            case UPDATE_PINNED:
                count = dbUpdate(getUri(action, requestUri), cv, selection, selectionArgs);
                break;
            case DELETE_FOLLOW:
            case DELETE_PINNED:
                requestUri = getUri(action, requestUri);
                count = dbDeleteBySelection(action, requestUri, selection, selectionArgs);
                break;
            case DELETE_ALL_FOLLOW:
            case DELETE_ALL_PINNED:
                count = dbDeleteAll(action);
                break;
            default:
                throw new UnsupportedOperationException("Unknown service action: " + action);
        }

        if (resultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(RESULT_COUNT, count);
            resultReceiver.send(RESULT_OK, bundle);
        }
    }

    /**
     * Get the base mUri for the specified action
     * @param action        Action to get Uri for
     * @param requestUri    Uri specified in request
     * @return  'with id' Uri or <code>null</code>
     */
    protected Uri getBaseUri(Actions action, @Nullable Uri requestUri) {
        Uri uri = requestUri;   // default to uri specified in request
        if (uri == null) {
            // uri not specified so determine from action
            if (action.isFollow()) {
                uri = ProviderUri.FOLLOW_CONTENT_URI;
            } else if (action.isPinned()) {
                uri = ProviderUri.PINNED_CONTENT_URI;
            }
        }
        return uri;
    }

    /**
     * Get the Uri for the specified action
     * @param action    Action to get Uri for
     * @param requestUri    Uri specified in request
     * @return  Uri or <code>null</code>
     */
    protected Uri getUri(Actions action, @Nullable Uri requestUri) {
        Uri uri = requestUri;
        if (uri == null) {
            uri = getBaseUri(action, requestUri);
        }
        return uri;
    }

    /**
     * Get the 'with id' Uri for the specified action
     * @param action    Action to get Uri for
     * @param id        Id to append to base Uri
     * @return  'with id' Uri or <code>null</code>
     */
    protected Uri getWithIdUri(Actions action, int id) {
        return UriUtils.getWithIdUri(getBaseUri(action, null), id);
    }

    /**
     * Add a row to the database
     * @param action    Action to do insert for
     * @param cv        Values to add
     * @return  Uri of new addition, or <code>null</code>
     */
    private Uri dbInsert(Actions action, ContentValues cv) {
        Uri resultUri = null;
        Uri uri = getBaseUri(action, null);
        if ((uri != null) && (cv != null)) {
            resultUri = getContentResolver().insert(uri, cv);
        }
        return resultUri;
    }

    /**
     * Add multiple rows to the database
     * @param action    Action to do insert for
     * @param cv        Values to add
     * @return  Number of inserted items
     */
    private int dbBulkInsert(Actions action, ContentValues[] cv) {
        int count = 0;
        Uri uri = getBaseUri(action, null);
        if ((uri != null) && (cv != null)) {
            count = getContentResolver().bulkInsert(uri, cv);
        }
        return count;
    }

    /**
     * Update an existing recipe in the database
     * @param uri   Uri to use for update
     * @param cv    Values to add
     * @return  <code>1</code> if update successful, <code>0</code> otherwise
     */
    private int dbUpdate(@NonNull Uri uri, ContentValues cv, @Nullable String where, @Nullable String[] selectionArgs) {
        int count = 0;
        if (cv != null) {
            count = getContentResolver().update(uri, cv, where, selectionArgs);
        }
        return count;
    }

    /**
     * Get a row from the database
     * @param action        Action to do get for
     * @param where         'Where' for selection
     * @param selectionArgs Arguments for 'where' selection
     * @return  Cursor containing recipe info
     */
    private Cursor dbGetBySelection(Actions action, @Nullable Uri requestUri, @Nullable String where, @Nullable String[] selectionArgs) {
        Cursor cursor = null;
        Uri uri = getBaseUri(action, requestUri);
        if (uri != null) {
            cursor = getContentResolver().query(uri, null, where, selectionArgs, null);
            if ((cursor != null) && (cursor.getCount() == 0)) {
                // doesn't exist so return null for convenience
                cursor.close();
                cursor = null;
            }
        }
        return cursor;
    }

    /**
     * Delete multiple rows from the database
     * @param action        Action to do delete for
     * @param where         'Where' for selection
     * @param selectionArgs Arguments for 'where' selection
     * @return  Number of deleted items
     */
    private int dbDeleteBySelection(Actions action, @Nullable Uri requestUri, @Nullable String where, @Nullable String[] selectionArgs) {
        int count = 0;
        Uri uri = getBaseUri(action, requestUri);
        if (uri != null) {
            count = getContentResolver().delete(uri, where, selectionArgs);
        }
        return count;
    }

    /**
     * Delete all rows from the database
     * @param action    Action to do delete for
     * @return  Number of deleted items
     */
    private int dbDeleteAll(Actions action) {
        int count = 0;
        Uri uri = getBaseUri(action, null);
        if (uri != null) {
            count = getContentResolver().delete(uri, DB_DELETE_ALL, null);

            Timber.i("Deleted " + count + " row(s) from db");
        }
        return count;
    }

    /**
     * Make a selection args array, and closes cursor
     * @param cursor    Cursor to generate selection args from
     * @param colIdx    Index of cursor column
     * @return  selection args array
     */
    private String[] getSelectionArgs(Cursor cursor, int colIdx) {
        String[] argArray = null;
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                argArray = new String[count];

                while (cursor.moveToNext()) {
                    --count;    // use count var to avoid creating new var
                    argArray[count] = cursor.getString(colIdx);
                }
            }
            cursor.close();
        }
        return argArray;
    }

    public static class Builder extends AbstractIntentService.Builder {

        /**
         * Constructor
         */
        public Builder(Context context, Actions action) {
            super(context, DatabaseIntentService.class, action.toString());
        }

        public Builder uri(Uri uri) {
            mIntent.putExtra(URI_EXTRA, uri);
            return this;
        }

        public Builder cv(ContentValues cv) {
            mIntent.putExtra(CV_EXTRA, cv);
            return this;
        }

        public Builder selection(String selection) {
            mIntent.putExtra(SELECTION_EXTRA, selection);
            return this;
        }

        public Builder selectionArgs(String[] selectionArgs) {
            mIntent.putExtra(SELECTION_ARGS_EXTRA, selectionArgs);
            return this;
        }

        public Builder selectionArgs(String selectionArg) {
            return selectionArgs(new String[] { selectionArg });
        }

        public Builder cvArray(ContentValues[] cvArray) {
            mIntent.putExtra(CV_ARRAY_EXTRA, cvArray);
            return this;
        }

        @Override
        public Builder resultReceiver(ResultReceiver resultReceiver) {
            super.resultReceiver(resultReceiver);
            return this;
        }

        /**
         * Get a builder instance
         * @return  New builder instance
         */
        public static Builder builder(Context context, Actions action) {
            return new Builder(context, action);
        }
    }

    public static class Extractor extends AbstractIntentService.Extractor {

        public Extractor(@Nullable Intent intent) {
            super(intent);
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable public Uri uri() {
            Uri uri = null;
            if (hasExtra(URI_EXTRA)) {
                uri = mIntent.getParcelableExtra(URI_EXTRA);
            }
            return uri;
        }

        @Nullable public ContentValues cv() {
            return getContentValues(CV_EXTRA);
        }

        @Nullable public String selection() {
            return getString(SELECTION_EXTRA);
        }

        @Nullable public String[] selectionArgs() {
            return getStringArray(SELECTION_ARGS_EXTRA);
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable public ContentValues[] cvArray() {
            ContentValues[] cvArray = null;
            if (hasExtra(CV_ARRAY_EXTRA)) {
                cvArray = (ContentValues[]) getParcelableArrayFromBundle(
                        mIntent.getExtras(), CV_ARRAY_EXTRA, ContentValues[].class);
            }
            return cvArray;
        }
    }

}
