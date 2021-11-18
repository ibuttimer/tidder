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
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import static com.ianbuttimer.tidderish.data.ICallback.ADDITIONAL_INFO;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_ARG;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_EXTRAS;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_METHOD;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_PROJECTION;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_SELECTION;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_SELECTION_ARGS;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_SORT_ORDER;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_URI;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_VALUES;


/**
 * Class to asynchronously handle a call to the ContentProvider <code>call</code> interface.<br>
 */

public abstract class ContentProviderLoader extends AsyncTaskLoader<AbstractResultWrapper> {

    @Nullable protected Bundle args;

    protected AbstractResultWrapper mRaw;   // raw results
    protected boolean mUseCache;            // enable cached results flag

    /**
     * Constructor
     * @param context   Current context
     * @param args      Loader argument bundle
     */
    public ContentProviderLoader(Context context, @Nullable Bundle args) {
        this(context, args, true);
    }

    /**
     * Constructor
     * @param context   Current context
     * @param args      Loader argument bundle
     * @param useCache  Enable cached results flag
     */
    public ContentProviderLoader(Context context, @Nullable Bundle args, boolean useCache) {
        super(context);
        this.args = args;
        this.mUseCache = useCache;
    }

    @Override
    protected void onStartLoading() {

        if (args == null) {
            return; // no args, nothing to do
        }

        /*
         * If we already have cached results, just deliver them now. If we don't have any
         * cached results, force a load.
         */
        if ((mRaw != null) && mUseCache) {
            deliverResult(mRaw);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(AbstractResultWrapper result) {
        mRaw = result;
        super.deliverResult(result);
    }


    @Override
    protected void onReset() {
        mRaw = null;
    }

    /**
     * Builder class for content provider arguments
     */
    public static class Builder {

        protected Bundle bundle;

        public Builder() {
            this.bundle = new Bundle();
        }

        @NonNull public Builder putUri(Uri uri) {
            return putParcelable(CONTENT_PROVIDER_URI, uri);
        }

        @NonNull public Builder putContentValues(ContentValues contentValues) {
            return putParcelable(CONTENT_PROVIDER_VALUES, contentValues);
        }

        @NonNull public Builder putProjection(String[] projection) {
            return putStringArray(CONTENT_PROVIDER_PROJECTION, projection);
        }

        @NonNull public Builder putSelection(String selection) {
            return putString(CONTENT_PROVIDER_SELECTION, selection);
        }

        @NonNull public Builder putSelectionArgs(String[] selectionArgs) {
            return putStringArray(CONTENT_PROVIDER_SELECTION_ARGS, selectionArgs);
        }

        @NonNull public Builder putSortOrder(String sortOrder) {
            return putString(CONTENT_PROVIDER_SORT_ORDER, sortOrder);
        }

        /**
         * Add a method argument
         * @param method    Methos to add
         * @return  Builder to facilitate chaining
         */
        public Builder putMethod(@NonNull String method) {
            return putString(CONTENT_PROVIDER_METHOD, method);
        }

        /***
         * Add an arg argument
         * @param arg   Arg ro add
         * @return  Builder to facilitate chaining
         */
        public Builder putArg(@Nullable String arg) {
            return putString(CONTENT_PROVIDER_ARG, arg);
        }

        /**
         * Add an extras argument
         * @param extras    Extras to add
         * @return  Builder to facilitate chaining
         */
        public Builder putExtras(@Nullable Bundle extras) {
            bundle.putBundle(CONTENT_PROVIDER_EXTRAS, extras);
            return this;
        }

        /**
         * Add additional info to be included with result
         * @param additionalInfo    info to add
         * @return  Builder to facilitate chaining
         */
        public Builder putAdditionalInfo(@Nullable Bundle additionalInfo) {
            bundle.putBundle(ADDITIONAL_INFO, additionalInfo);
            return this;
        }

        /**
         * Add a string using the specified key
         * @param key   Key to use
         * @param value Value to add
         * @return  Builder to facilitate chaining
         */
        private Builder putString(@NonNull String key, @Nullable String value) {
            if (value != null) {
                bundle.putString(key, value);
            }
            return this;
        }

        /**
         * Add a string array argument
         * @param key   Key to use
         * @param array Array to add
         * @return  Builder to facilitate chaining
         */
        private Builder putStringArray(@NonNull String key, @Nullable String[] array) {
            if (array != null) {
                bundle.putStringArray(key, array);
            }
            return this;
        }

        /**
         * Add a parcelable argument
         * @param key           Key to use
         * @param parcelable    Parcelable to add
         * @return  Builder to facilitate chaining
         */
        private Builder putParcelable(@NonNull String key, @Nullable Parcelable parcelable) {
            if (parcelable != null) {
                bundle.putParcelable(key, parcelable);
            }
            return this;
        }

        @NonNull public Builder clear() {
            bundle.clear();
            return this;
        }

        @NonNull public Bundle build() {
            return (Bundle)bundle.clone();
        }

    }

    @NonNull public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Content provider arguments extractor class
     */
    public static class Extractor {

        @Nullable protected Bundle bundle;

        public Extractor(@Nullable Bundle bundle) {
            this.bundle = bundle;
        }

        @Nullable public Uri getUri() {
            if (bundle != null) {
                return bundle.getParcelable(CONTENT_PROVIDER_URI);
            } else {
                return null;
            }
        }

        @Nullable public ContentValues getContentValues() {
            if (bundle != null) {
                return bundle.getParcelable(CONTENT_PROVIDER_VALUES);
            } else {
                return null;
            }
        }

        @Nullable public String[] getProjection() {
            return getStringArray(CONTENT_PROVIDER_PROJECTION);
        }

        @Nullable public String getSelection() {
            return getString(CONTENT_PROVIDER_SELECTION);
        }

        @Nullable public String[] getSelectionArgs() {
            return getStringArray(CONTENT_PROVIDER_SELECTION_ARGS);
        }

        @Nullable public String getSortOrder() {
            return getString(CONTENT_PROVIDER_SORT_ORDER);
        }

        /**
         * Return the value associated with the given key
         * @param key   Key to use
         * @return  A string or null
         */
        private String getString(String key) {
            if (bundle != null) {
                return bundle.getString(key);
            } else {
                return null;
            }
        }

        /**
         * Return the value associated with the given key
         * @param key   Key to use
         * @return  A string array or null
         */
        private String[] getStringArray(String key) {
            if (bundle != null) {
                return bundle.getStringArray(key);
            } else {
                return null;
            }
        }

        /**
         * Return the value associated with the given key
         * @param key   Key to use
         * @return  A Bundle or null
         */
        private Bundle getBundle(String key) {
            if (bundle != null) {
                return bundle.getBundle(key);
            } else {
                return null;
            }
        }

        /**
         * Get the method argument
         * @return  A string or null
         */
        @Nullable public String getMethod() {
            return getString(CONTENT_PROVIDER_METHOD);
        }

        /***
         * Get the arg argument
         * @return  A string or null
         */
        @Nullable public String getArg() {
            return getString(CONTENT_PROVIDER_ARG);
        }

        /**
         * Get the extras argument
         * @return  A bundle or null
         */
        @Nullable public Bundle getExtras() {
            return getBundle(CONTENT_PROVIDER_EXTRAS);
        }

        /**
         * Get the additional info included with result
         * @return  A bundle or null
         */
        @Nullable public Bundle getAdditionalInfo() {
            return getBundle(ADDITIONAL_INFO);
        }
    }

    @NonNull public static Extractor getExtractor(Bundle bundle) {
        return new Extractor(bundle);
    }

}
