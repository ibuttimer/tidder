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

package com.ianbuttimer.tidder.reddit.get;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ianbuttimer.tidder.reddit.ListingRequest;
import com.ianbuttimer.tidder.reddit.Response;

import static com.ianbuttimer.tidder.net.NetworkUtils.joinUrlPaths;
import static com.ianbuttimer.tidder.net.NetworkUtils.trimUrlPathStart;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_POST_BASE_URL;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_POST_URL_END;


/**
 * A GET /r/<i>subreddit</i>/about request
 * @see <a href="https://www.reddit.com/dev/api#GET_r_{subreddit}_about">/r/<i>subreddit</i>/about</a>
 */

public class SubredditLinkRequest extends ListingRequest {

    private static final Uri BASE_URI = Uri.parse(SUBREDDIT_POST_BASE_URL);

    public SubredditLinkRequest(Uri uri) {
        this(uri, null);
    }

    public SubredditLinkRequest(Uri uri, @Nullable Class<? extends Response> responseClass) {
        super(uri, responseClass);
    }

    public static class Builder extends ListingRequest.Builder {

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
         * @param subreddit     subreddit url
         * @param listing       Listing category
         * @return  Builder to facilitate chaining
         * @throws IllegalStateException    if subreddit has been already set
         * @throws IllegalArgumentException if no subreddit or listing provided
         */
        public Builder subreddit(String subreddit, String listing) throws IllegalStateException {
            if (!isValid()) {
                String path = subredditRPath(subreddit);
                String list;

                if (TextUtils.isEmpty(listing)) {
                    throw new IllegalArgumentException("Listing name required");
                }

                if (listing.endsWith(SUBREDDIT_POST_URL_END)) {
                    list = listing;
                } else {
                    list = listing + SUBREDDIT_POST_URL_END;
                }

                appendPath(trimUrlPathStart(
                        joinUrlPaths(path, list)
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
        public SubredditLinkRequest build() throws IllegalStateException {
            if (!isValid()) {
                throw new IllegalStateException("Subreddit not set");
            }
            return new SubredditLinkRequest(builder.build(), SubredditLinkResponse.class);
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
