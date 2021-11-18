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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ianbuttimer.tidderish.reddit.Request;
import com.ianbuttimer.tidderish.reddit.post.PostRequest;

import java.net.URL;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * Interface for handling of both okhttp3 and content provider responses.<br>
 * The class <code>T</code> specifies the type of object to be processed by the application.<br>
 */
@SuppressWarnings("unused")
public interface ICallback<T> extends Callback {

    /** Sub id of loader used for content provider call method */
    int CONTENT_PROVIDER_CALL_LOADER = 1;
    /** Sub id of loader used for content provider insert method */
    int CONTENT_PROVIDER_INSERT_LOADER = 2;
    /** Sub id of loader used for content provider query method */
    int CONTENT_PROVIDER_QUERY_LOADER = 3;
    /** Sub id of loader used for content provider update method */
    int CONTENT_PROVIDER_UPDATE_LOADER = 4;
    /** Sub id of loader used for content provider delete method */
    int CONTENT_PROVIDER_DELETE_LOADER = 5;
    /** Factor to multiply client loader ids by to generate content provider loader ids */
    int LOADER_FACTOR = 100;

    /** Uri argument name for call & crud loaders */
    String CONTENT_PROVIDER_URI = "Uri";
    /** Method argument name for call loader */
    String CONTENT_PROVIDER_METHOD = "method";
    /** Arg argument name for call loader */
    String CONTENT_PROVIDER_ARG = "arg";
    /** Extras argument name for call loader */
    String CONTENT_PROVIDER_EXTRAS = "extras";

    /** Content values argument name for crud loader */
    String CONTENT_PROVIDER_VALUES = "values";
    /** Selection argument name for crud loader */
    String CONTENT_PROVIDER_SELECTION = "selection";
    /** Selection args argument name for crud loader */
    String CONTENT_PROVIDER_SELECTION_ARGS = "selection_args";
    /** Projection argument name for crud loader */
    String CONTENT_PROVIDER_PROJECTION = "projection";
    /** Sort order argument name for crud loader */
    String CONTENT_PROVIDER_SORT_ORDER = "sort_order";

    /** Additional info argument name for loader */
    String ADDITIONAL_INFO = "additional_info";

    /** Type of resultText returned */
    String CONTENT_PROVIDER_RESULT_TYPE = "result_type";
    /** Error code of resultText returned */
    String CONTENT_PROVIDER_ERROR_CODE = "result_error_code";
    /** Error string of resultText returned */
    String CONTENT_PROVIDER_ERROR_STRING = "result_error_string";



    enum ResponseHandler { URL_HANDLER, URL_PROVIDER_HANDLER,
                            INSERT_HANDLER, QUERY_HANDLER, UPDATE_HANDLER, DELETE_HANDLER }

    /*
    ///////// The methods below are for Url requests //////////
    */

    /**
     * Send a get request using the specified Url
     * @param url   Url to send
     */
    void requestGet(@NonNull URL url);

    /**
     * Send a get request using the specified Url via the IntentService
     * @param request   request to send
     */
    void requestGetService(@NonNull Request request);

    /**
     * Send a post request using the specified Url via the IntentService
     * @param request   request to send
     */
    void requestPostService(@NonNull PostRequest request);

    /**
     * Process the response from a Url request, converting the raw response to an object
     * @param request   Request url
     * @param response  Response from the server
     * @return  Response object
     */
    T processUrlResponse(@NonNull URL request, @NonNull Response response);


    /*
    ///////// The methods below are for Uri requests //////////
    */

    /**
     * Insert a new entry using a content provider
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param contentValues A set of column_name/value pairs to add to the database.
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#insert(android.net.Uri, android.content.ContentValues)">ContentProvider.insert(Uri, ContentValues)</a>
     */
    void insert(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues);

    /**
     * Insert a new entry using a content provider
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param args          The arguments for the content provider
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#insert(android.net.Uri, android.content.ContentValues)">ContentProvider.insert(Uri, ContentValues)</a>
     */
    void insert(@NonNull FragmentActivity activity, int loaderId, @NonNull Bundle args);

    /**
     * Query rows
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param projection    The list of columns to put into the cursor
     * @param selection     A selection criteria to apply when filtering rows
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection
     * @param sortOrder     How the rows in the cursor should be sorted
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)">ContentProvider.query(Uri, String[], String, String[], String)</a>
     */
    void query(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder);

    /**
     * Query rows
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param args          The arguments for the content provider
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)">ContentProvider.query(Uri, String[], String, String[], String)</a>
     */
    void query(@NonNull FragmentActivity activity, int loaderId, @NonNull Bundle args);

    /**
     * Update one or more rows
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param contentValues A set of column_name/value pairs to update in the database
     * @param selection     An optional filter to match rows to update
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])">ContentProvider.update(Uri, ContentValues, String, String[])</a>
     */
    void update(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs);

    /**
     * Update one or more rows
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param args          The arguments for the content provider
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])">ContentProvider.update(Uri, ContentValues, String, String[])</a>
     */
    void update(@NonNull FragmentActivity activity, int loaderId, @NonNull Bundle args);

    /**
     * Delete one or more rows.
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     * @param selection     An optional filter to match rows to update
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#delete(android.net.Uri, java.lang.String, java.lang.String[])">ContentProvider.delete(Uri, String, String[])</a>
     */
    void delete(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs);

    /**
     * Delete one or more rows.
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param args          The arguments for the content provider
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#delete(android.net.Uri, java.lang.String, java.lang.String[])">ContentProvider.delete(Uri, String, String[])</a>
     */
    void delete(@NonNull FragmentActivity activity, int loaderId, @NonNull Bundle args);

    /**
     * Asynchronously create an entry for the specified Uri
     * @param activity  Current activity
     * @param loaderId  Id of loader
     * @param uri       Uri to send
     * @param method    Provider-defined method to call
     * @param arg       Provider-defined String argument
     * @param extras    Provider-defined Bundle argument
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#call(java.lang.String, java.lang.String, android.os.Bundle)">ContentProvider.call(String, String, Bundle)</a>
     */
    void call(@NonNull FragmentActivity activity, int loaderId, @NonNull Uri uri, @NonNull String method, @Nullable String arg, @Nullable Bundle extras);

    /**
     * Asynchronously create an entry for the specified Uri
     * @param activity  Current activity
     * @param loaderId  Id of loader
     * @param args          The arguments for the content provider
     * @see <a href="https://developer.android.com/reference/android/content/ContentProvider.html#call(java.lang.String, java.lang.String, android.os.Bundle)">ContentProvider.call(String, String, Bundle)</a>
     */
    void call(@NonNull FragmentActivity activity, int loaderId, @NonNull Bundle args);

    /**
     * Process the response from a Url request, converting the raw response to an object
     * @param response  Response from the content provider
     * @return  Response object
     */
    T processUriResponse(@Nullable AbstractResultWrapper response);

    /**
     * Process the response from an insert request
     * @param response  Response from the content provider
     */
    void processInsertResponse(@Nullable InsertResultWrapper response);

    /**
     * Process the response from a query request
     * @param response  Response from the content provider
     */
    void processQueryResponse(@Nullable QueryResultWrapper response);

    /**
     * Process the response from an update request
     * @param response  Response from the content provider
     */
    void processUpdateResponse(@Nullable UpdateResultWrapper response);

    /**
     * Process the response from a delete request
     * @param response  Response from the content provider
     */
    void processDeleteResponse(@Nullable DeleteResultWrapper response);

    /*
    ///////// The methods below are common to both Url & Uri requests //////////
    */

    /**
     * Handle the response from a request
     * @param result    Response object
     */
    void onResponse(T result);

    /**
     * Handle a failure response from a request
     * @param code  Failure code
     * @param message   Failure message
     */
    void onFailure(int code, String message);

    /**
     * Response class for a Http request
     */
    class UrlResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param urlRequest    Original request URL
         * @param stringResult  String response
         */
        public UrlResultWrapper(URL urlRequest, String stringResult) {
            this(urlRequest, stringResult, null);
        }
        /**
         * Constructor
         * @param urlRequest    Original request URL
         * @param stringResult  String response
         * @param responseClass Class representing response
         */
        public UrlResultWrapper(@NonNull URL urlRequest, String stringResult,
                                @Nullable Class<?> responseClass) {
            super(ResponseHandler.URL_HANDLER, urlRequest, stringResult, responseClass);
        }
    }

    /**
     * Response class for a Url http request
     */
    class UrlProviderResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param stringResult  String response
         */
        public UrlProviderResultWrapper(Uri uriRequest, String stringResult) {
            super(ResponseHandler.URL_PROVIDER_HANDLER, uriRequest, stringResult);
        }
        /**
         * Constructor
         * @param uriRequest       Original request Uri
         * @param bundleResult     Bundle response
         */
        public UrlProviderResultWrapper(Uri uriRequest, Bundle bundleResult) {
            super(ResponseHandler.URL_PROVIDER_HANDLER, uriRequest, bundleResult);
        }
        /**
         * Constructor
         * @param urlRequest    Original request URL
         * @param stringResult  String response
         */
        public UrlProviderResultWrapper(URL urlRequest, String stringResult) {
            super(ResponseHandler.URL_PROVIDER_HANDLER, urlRequest, stringResult);
        }
        /**
         * Constructor
         * @param urlRequest    Original request URL
         * @param errorCode     Error code
         * @param errorString   Error string
         */
        public UrlProviderResultWrapper(URL urlRequest, int errorCode, String errorString) {
            super(ResponseHandler.URL_PROVIDER_HANDLER, urlRequest, errorCode, errorString);
        }
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param errorCode     Error code
         * @param errorString   Error string
         */
        public UrlProviderResultWrapper(Uri uriRequest, int errorCode, String errorString) {
            super(ResponseHandler.URL_PROVIDER_HANDLER, uriRequest, errorCode, errorString);
        }
    }

    /**
     * Response class for a Uri update request
     */
    class UpdateResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param intResult     Integer response
         */
        public UpdateResultWrapper(Uri uriRequest, int intResult) {
            super(ResponseHandler.UPDATE_HANDLER, uriRequest, intResult);
        }
    }

    /**
     * Response class for a Uri delete request
     */
    class DeleteResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param intResult     Integer response
         */
        public DeleteResultWrapper(Uri uriRequest, int intResult) {
            super(ResponseHandler.DELETE_HANDLER, uriRequest, intResult);
        }
    }

    /**
     * Response class for a Uri query request
     */
    class QueryResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param cursorResult  Cursor response
         */
        public QueryResultWrapper(Uri uriRequest, Cursor cursorResult) {
            super(ResponseHandler.QUERY_HANDLER, uriRequest, cursorResult);
        }
    }

    /**
     * Response class for a Uri insert request
     */
    class InsertResultWrapper extends AbstractResultWrapper {
        /**
         * Constructor
         * @param uriRequest    Original request Uri
         * @param uriResult     Uri response
         */
        public InsertResultWrapper(Uri uriRequest, Uri uriResult) {
            super(ResponseHandler.INSERT_HANDLER, uriRequest, uriResult);
        }

    }

}
