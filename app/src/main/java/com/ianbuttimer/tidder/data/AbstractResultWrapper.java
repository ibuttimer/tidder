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

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.ianbuttimer.tidder.net.UriURLPair;

import java.net.URL;


/**
 * Wrapper class for ICallback results
 */
@SuppressWarnings("unused")
public abstract class AbstractResultWrapper {

    public enum ResultType { STRING, STRING_ARRAY, INTEGER, CURSOR, URI, BUNDLE, ERROR;

        public void addToBundle(Bundle bundle, String key) {
            bundle.putInt(key, ordinal() + 1);  // use +1 offset as ordinal starts at 0
        }

        public static ResultType getFromBundle(Bundle bundle, String key) {
            int ordinal = bundle.getInt(key) - 1;
            ResultType resultType = null;
            for (ResultType type : ResultType.values()) {
                if (type.ordinal() == ordinal) {
                    resultType = type;
                }
            }
            if (resultType == null) {
                throw new IllegalArgumentException("Unknown value " + ordinal + " for key " + key);
            }
            return resultType;
        }
    };

    private ICallback.ResponseHandler mHandler;  // type of mHandler required to process this object
    protected UriURLPair mRequest;           // uri/url used to make request
    protected Class mResponseClass;          // class representing response

    protected String mStringResult;          // returned from url & uri call
    protected String[] mStringArrayResult;   // returned from uri call
    protected int mIntResult;                // returned from update & delete
    protected Cursor mCursorResult;          // returned from query
    protected Uri mUriResult;                // returned from insert
    protected Bundle mBundleResult;          // returned from uri call

    // members for error resultText
    protected int mErrorCode;                // error resultText code
    protected String mErrorString;           // error resultText string

    private ResultType mResultType;

    protected Bundle mAdditionalInfo;        // additional info


    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param urlRequest    Original request URL
     * @param stringResult  String response
     * @param responseClass Class representing response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler,
                                 @NonNull URL urlRequest, String stringResult,
                                 @Nullable Class responseClass) {
        initUrl(handler, urlRequest, responseClass);
        this.mStringResult = stringResult;
        this.mResultType = ResultType.STRING;
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param urlRequest    Original request URL
     * @param stringResult  String response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler,
                                 @NonNull URL urlRequest, String stringResult) {
        this(handler, urlRequest, stringResult, null);
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param urlRequest    Original request URL
     * @param errorCode     Error code
     * @param errorString   Error string
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull URL urlRequest, int errorCode, String errorString) {
        initUrl(handler, urlRequest);
        this.mErrorCode = errorCode;
        this.mErrorString = errorString;
        this.mResultType = ResultType.ERROR;
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     * @param errorCode     Error code
     * @param errorString   Error string
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, int errorCode, String errorString) {
        initUri(handler, uriRequest);
        this.mErrorCode = errorCode;
        this.mErrorString = errorString;
        this.mResultType = ResultType.ERROR;
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     * @param stringResult  String response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, String stringResult) {
        initUri(handler, uriRequest);
        this.mStringResult = stringResult;
        this.mResultType = ResultType.STRING;
    }

    /**
     * Constructor
     * @param handler               Type of mHandler required to process this object
     * @param uriRequest            Original request Uri
     * @param stringArrayResult     String array response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, String[] stringArrayResult) {
        initUri(handler, uriRequest);
        this.mStringArrayResult = stringArrayResult;
        this.mResultType = ResultType.STRING_ARRAY;
    }

    /**
     * Initialise the mHandler & uri
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     * @param responseClass Class to representing response
     */
    protected void initUri(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest,
                           @Nullable Class responseClass) {
        this.mHandler = handler;
        this.mRequest = new UriURLPair(uriRequest);
        this.mResponseClass = responseClass;
    }

    /**
     * Initialise the mHandler & uri
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     */
    protected void initUri(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest) {
        initUri(handler, uriRequest, null);
    }

    /**
     * Initialise the mHandler & URL
     * @param handler       Type of mHandler required to process this object
     * @param urlRequest    Original request URL
     * @param responseClass Class to represent response
     */
    protected void initUrl(@NonNull ICallback.ResponseHandler handler, @NonNull URL urlRequest,
                           @Nullable Class responseClass) {
        this.mHandler = handler;
        this.mRequest = new UriURLPair(urlRequest);
        this.mResponseClass = responseClass;
    }

    /**
     * Initialise the mHandler & URL
     * @param handler       Type of mHandler required to process this object
     * @param urlRequest    Original request URL
     */
    protected void initUrl(@NonNull ICallback.ResponseHandler handler, @NonNull URL urlRequest) {
        initUrl(handler, urlRequest, null);
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     * @param intResult     Integer response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, int intResult) {
        initUri(handler, uriRequest);
        this.mIntResult = intResult;
        this.mResultType = ResultType.INTEGER;
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     * @param cursorResult  Cursor response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Cursor cursorResult) {
        initUri(handler, uriRequest);
        this.mCursorResult = cursorResult;
        this.mResultType = ResultType.CURSOR;
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     * @param uriResult     Uri response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Uri uriResult) {
        initUri(handler, uriRequest);
        this.mUriResult = uriResult;
        this.mResultType = ResultType.URI;
    }

    /**
     * Constructor
     * @param handler       Type of mHandler required to process this object
     * @param uriRequest    Original request Uri
     * @param bundleResult  Bundle response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Bundle bundleResult) {
        initUri(handler, uriRequest);
        this.mBundleResult = bundleResult;
        this.mResultType = ResultType.BUNDLE;
    }

    public String getStringResult() {
        if (!isString()) {
            throw new IllegalStateException("Not string result");
        }
        return mStringResult;
    }

    public String[] getStringArrayResult() {
        if (!isStringArray()) {
            throw new IllegalStateException("Not string array result");
        }
        return mStringArrayResult;
    }

    public int getIntResult() {
        if (!isInteger()) {
            throw new IllegalStateException("Not integer result");
        }
        return mIntResult;
    }

    public Cursor getCursorResult() {
        if (!isCursor()) {
            throw new IllegalStateException("Not cursor result");
        }
        return mCursorResult;
    }

    public Uri getUriResult() {
        if (!isUri()) {
            throw new IllegalStateException("Not uri result");
        }
        return mUriResult;
    }

    public Bundle getBundleResult() {
        if (!isBundle()) {
            throw new IllegalStateException("Not bundle result");
        }
        return mBundleResult;
    }

    public Pair<Integer, String> getErrorResult() {
        if (!isError()) {
            throw new IllegalStateException("Not error result");
        }
        return new Pair<>(mErrorCode, mErrorString);
    }

    public ICallback.ResponseHandler getHandler() {
        return mHandler;
    }

    public Uri getUriRequest() {
        return mRequest.getUri();
    }

    public URL getUrlRequest() {
        return mRequest.getUrl();
    }

    @Nullable public Class getResponseClass() {
        return mResponseClass;
    }

    public ResultType getResultType() {
        return mResultType;
    }


    @Nullable public Bundle getAdditionalInfo() {
        return mAdditionalInfo;
    }

    public void setAdditionalInfo(Bundle additionalInfo) {
        mAdditionalInfo = additionalInfo;
    }

    /**
     * Check if this object represents a resultText of the specified type
     * @param type  Type of resultText to check
     * @return  <code>true</code> if this object is a resultText of the specified type, <code>false</code> otherwise
     */
    public boolean isResultType(ResultType type) {
        return (mResultType.equals(type));
    }

    /**
     * Check if this object represents a string resultText
     * @return  <code>true</code> if this object is a string resultText, <code>false</code> otherwise
     */
    public boolean isString() {
        return isResultType(ResultType.STRING);
    }

    /**
     * Check if this object represents a string array resultText
     * @return  <code>true</code> if this object is a string array resultText, <code>false</code> otherwise
     */
    public boolean isStringArray() {
        return isResultType(ResultType.STRING_ARRAY);
    }

    /**
     * Check if this object represents an integer resultText
     * @return  <code>true</code> if this object is an integer resultText, <code>false</code> otherwise
     */
    public boolean isInteger() {
        return isResultType(ResultType.INTEGER);
    }

    /**
     * Check if this object represents a cursor resultText
     * @return  <code>true</code> if this object is a cursor resultText, <code>false</code> otherwise
     */
    public boolean isCursor() {
        return isResultType(ResultType.CURSOR);
    }

    /**
     * Check if this object represents a uri resultText
     * @return  <code>true</code> if this object is a uri resultText, <code>false</code> otherwise
     */
    public boolean isUri() {
        return isResultType(ResultType.URI);
    }

    /**
     * Check if this object represents a bundle resultText
     * @return  <code>true</code> if this object is a bundle resultText, <code>false</code> otherwise
     */
    public boolean isBundle() {
        return isResultType(ResultType.BUNDLE);
    }

    /**
     * Check if this object represents an error resultText
     * @return  <code>true</code> if this object is an error resultText, <code>false</code> otherwise
     */
    public boolean isError() {
        return isResultType(ResultType.ERROR);
    }

}
