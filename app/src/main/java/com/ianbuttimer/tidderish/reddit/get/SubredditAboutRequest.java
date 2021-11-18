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

package com.ianbuttimer.tidderish.reddit.get;

import android.net.Uri;

import com.ianbuttimer.tidderish.reddit.BaseObject;
import com.ianbuttimer.tidderish.reddit.Request;
import com.ianbuttimer.tidderish.reddit.Response;

import static com.ianbuttimer.tidderish.net.NetworkUtils.joinUrlPaths;
import static com.ianbuttimer.tidderish.net.NetworkUtils.trimUrlPathStart;
import static com.ianbuttimer.tidderish.net.RedditUriBuilder.SUBREDDIT_ABOUT_BASE_URL;
import static com.ianbuttimer.tidderish.net.RedditUriBuilder.SUBREDDIT_ABOUT_URL_END;


/**
 * A GET /r/<i>subreddit</i>/about request
 * @see <a href="https://www.reddit.com/dev/api#GET_r_{subreddit}_about">/r/<i>subreddit</i>/about</a>
 */

public class SubredditAboutRequest extends Request {

    private static final Uri BASE_URI = Uri.parse(SUBREDDIT_ABOUT_BASE_URL);

    public SubredditAboutRequest(Uri uri) {
        super(uri);
    }

    public SubredditAboutRequest(Uri uri, Class<? extends Response<? extends BaseObject<?>>> responseClass) {
        super(uri, responseClass);
    }

    public static class Builder extends Request.Builder {

        /**
         * Constructor
         */
        protected Builder() {
            this(BASE_URI);
        }

        /**
         * Constructor
         */
        public Builder(Uri uri) {
            super(uri);
        }

        /**
         * Set the subreddit field
         * @param subreddit    subreddit url/name
         * @return  Builder to facilitate chaining
         * @throws IllegalStateException    if subreddit has been already set
         * @throws IllegalArgumentException if no subreddit provided
         */
        public Builder subreddit(String subreddit) {
            if (!isValid()) {
                String path = subredditRPath(subreddit);
                appendPath(trimUrlPathStart(
                        joinUrlPaths(path, SUBREDDIT_ABOUT_URL_END)
                        ));
                setValid(true);
            } else {
                throw new IllegalStateException("Subreddit already set");
            }
            return this;
        }

        /**
         * {@inheritDoc}
         * @throws IllegalStateException    if subreddit has not been set
         */
        @Override
        public SubredditAboutRequest build() throws IllegalStateException {
            if (!isValid()) {
                throw new IllegalStateException("Subreddit not set");
            }
            return new SubredditAboutRequest(builder.build(), SubredditAboutResponse.class);
        }

    }

    /**
     * Get a builder instance
     * @return  New builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

}
