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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import static com.ianbuttimer.tidderish.data.AsyncCallback.getSubLoaderId;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_DELETE_LOADER;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_INSERT_LOADER;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_QUERY_LOADER;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_UPDATE_LOADER;


/**
 * Class to asynchronously handle a call to one of the ContentProvider CRUD interfaces.
 */

public class ContentProviderCrudLoader extends ContentProviderLoader {

    /**
     * Constructor
     * @param context   Current context
     * @param args      Loader argument bundle
     */
    public ContentProviderCrudLoader(Context context, Bundle args) {
        // don't use cache results
        super(context, args, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractResultWrapper loadInBackground() {

        // Extract the call arguments from the args
        Extractor extractor = getExtractor(args);
        Uri uri = extractor.getUri();

        if (uri == null) {
            return null;    // can't do anything
        }

        ContentValues contentValues = extractor.getContentValues();
        String[] projection = extractor.getProjection();
        String selection = extractor.getSelection();
        String[] selectionArgs = extractor.getSelectionArgs();
        String sortOrder = extractor.getSortOrder();

        ContentResolver resolver = getContext().getContentResolver();
        AbstractResultWrapper result;

        switch (getSubLoaderId(getId())) {
            case CONTENT_PROVIDER_INSERT_LOADER:
                result = new ICallback.InsertResultWrapper(
                        uri, resolver.insert(uri, contentValues));
                break;
            case CONTENT_PROVIDER_QUERY_LOADER:
                result = new ICallback.QueryResultWrapper(
                        uri, resolver.query(uri, projection, selection, selectionArgs, sortOrder));
                break;
            case CONTENT_PROVIDER_UPDATE_LOADER:
                result = new ICallback.UpdateResultWrapper(
                        uri, resolver.update(uri, contentValues, selection, selectionArgs));
                break;
            case CONTENT_PROVIDER_DELETE_LOADER:
                result = new ICallback.DeleteResultWrapper(
                        uri, resolver.delete(uri, selection, selectionArgs));
                break;
            default:
                result = null;
                break;
        }
        if (result != null) {
            result.setAdditionalInfo(extractor.getAdditionalInfo());    // echo back additional info
        }
        return result;
    }




}
