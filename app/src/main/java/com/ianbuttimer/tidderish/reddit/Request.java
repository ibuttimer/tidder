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

package com.ianbuttimer.tidderish.reddit;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.ianbuttimer.tidderish.net.NetworkUtils;

import java.net.URL;

import static com.ianbuttimer.tidderish.net.NetworkUtils.joinUrlPaths;
import static com.ianbuttimer.tidderish.net.RedditUriBuilder.SUBREDDIT_URL_R;

/**
 * Base class for requests
 */
@SuppressWarnings("unused")
public abstract class Request {

    protected Uri mUri; // request uri

    @Nullable protected Class<? extends Response<? extends BaseObject<?>>> mResponseClass;

    protected Bundle mAdditionalInfo;


    public Request(Uri uri) {
        this(uri, null);
    }

    public Request(Uri uri, @Nullable Class<? extends Response<? extends BaseObject<?>>> responseClass) {
        this.mUri = uri;
        this.mResponseClass = responseClass;
    }

    public Uri getUri() {
        return mUri;
    }

    public URL getURL() {
        return NetworkUtils.convertUriToURL(mUri);
    }

    @Nullable public Class<? extends Response<? extends BaseObject<?>>> getResponseClass() {
        return mResponseClass;
    }

    @Nullable public Bundle getAdditionalInfo() {
        return mAdditionalInfo;
    }

    public Request setAdditionalInfo(@Nullable Bundle additionalInfo) {
        this.mAdditionalInfo = additionalInfo;
        return this;
    }

    public abstract static class Builder {

        protected Uri.Builder builder;

        protected boolean mValid;

        /**
         * Constructor
         */
        public Builder() {
            builder = new Uri.Builder();
            mValid = false;
        }

        /**
         * Constructor
         */
        public Builder(String urlString) {
            this(Uri.parse(urlString));
        }

        /**
         * Constructor
         */
        public Builder(Uri uri) {
            builder = uri.buildUpon();
        }

        /**
         * Build the request Uri
         * @return Uri
         */
        public Uri buildUri() {
            return builder.build();
        }

        /**
         * Build the request URL
         * @return URL
         */
        public URL buildURL() {
            return NetworkUtils.convertUriToURL(buildUri());
        }

        public void setValid(boolean valid) {
            mValid = valid;
        }

        public boolean isValid() {
            return mValid;
        }

        /**
         * Build a request
         * @return Request object
         * @throws IllegalStateException    if id has not been set
         */
        public abstract Request build();

        /**
         * Add a new segment to the path parameter
         * @param newSegment    segment to add
         * @return  Builder to facilitate chaining
         */
        protected Builder appendPath(String newSegment) {
            if (!TextUtils.isEmpty(newSegment)) {
                builder.appendPath(newSegment);
            }
            return this;
        }

        /**
         * Add a query parameter
         * @param key       query parameter key
         * @param value     query parameter value
         * @return  Builder to facilitate chaining
         */
        protected Builder appendQueryParameter(String key, String value) {
            if (!TextUtils.isEmpty(value)) {
                builder.appendQueryParameter(key, value);
            }
            return this;
        }

        /**
         * Add a query parameter
         * @param key       query parameter key
         * @param value     query parameter value
         * @return  Builder to facilitate chaining
         */
        protected Builder appendQueryParameter(String key, int value) {
            builder.appendQueryParameter(key, Integer.toString(value));
            return this;
        }

        /**
         * Add a query parameter
         * @param key       query parameter key
         * @param value     query parameter value
         * @param min       minimum value
         * @param max       maximum value
         * @return  Builder to facilitate chaining
         */
        protected Builder appendQueryParameter(String key, int value, int min, int max) {
            if (value < min) {
                value = min;
            } else if (value > max) {
                value = max;
            }
            return appendQueryParameter(key, value);
        }

        /**
         * Add a query parameter
         * @param key       query parameter key
         * @param value     query parameter value
         * @return  Builder to facilitate chaining
         */
        protected Builder appendQueryParameter(String key, boolean value) {
            builder.appendQueryParameter(key, Boolean.toString(value));
            return this;
        }

        /**
         * Clear the builder contents
         * @return  Builder to facilitate chaining
         */
        public Builder clearQuery() {
            builder.clearQuery();
            return this;
        }

        /**
         * Construct an '/r/subreddit' path
         * @param subreddit    subreddit url/name
         * @return  Subreddit R path
         * @throws IllegalArgumentException if no subreddit provided
         */
        protected String subredditRPath(String subreddit) {
            String path;
            if (TextUtils.isEmpty(subreddit)) {
                throw new IllegalArgumentException("Subreddit name or url required");
            }
            if (subreddit.startsWith(NetworkUtils.PATH_JOIN)) {
                path = subreddit;   // looks like a url
            } else {
                path = joinUrlPaths(SUBREDDIT_URL_R, subreddit);
            }
            return path;
        }


    }

}
