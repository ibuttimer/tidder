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

package com.ianbuttimer.tidder.reddit.post;

import android.net.Uri;

import com.ianbuttimer.tidder.reddit.Response;

import static com.ianbuttimer.tidder.net.RedditUriBuilder.SEARCH_SUBREDDITS_URL;
import static com.ianbuttimer.tidder.reddit.Api.EXACT_DATA;
import static com.ianbuttimer.tidder.reddit.Api.OVER_18_DATA;
import static com.ianbuttimer.tidder.reddit.Api.QUERY_DATA;
import static com.ianbuttimer.tidder.reddit.Api.UNADVERT_DATA;


/**
 * Class representing a POST /api/search_subreddits request<br>
 * List subreddits that begin with a query string.
 * @see <a href="https://www.reddit.com/dev/api#POST_api_search_subreddits">/api/search_subreddits</a>
 */

public class ApiSearchSubredditsRequest extends PostRequest {

    private static final Uri BASE_URI = Uri.parse(SEARCH_SUBREDDITS_URL);

    public ApiSearchSubredditsRequest(Uri uri) {
        super(uri);
    }

    public ApiSearchSubredditsRequest(Uri uri, Class<? extends Response> responseClass) {
        super(uri, responseClass);
    }

    public static class Builder extends PostRequest.Builder {

        /**
         * Constructor
         */
        protected Builder() {
            super(BASE_URI);
        }

        /**
         * If <code>true</code>, only an exact match will be returned.
         * Exact matches are inclusive of over_18 subreddits
         * @param value     Filter value
         * @return  Builder object for chaining purposes
         */
        public Builder exact(boolean value) {
            post(EXACT_DATA, value);
            return this;
        }

        /**
         * If <code>false</code>, subreddits with over-18 content restrictions will be
         * filtered from the results.
         * @param value     Filter value
         * @return  Builder object for chaining purposes
         */
        public Builder over18data(boolean value) {
            post(OVER_18_DATA, value);
            return this;
        }

        /**
         * If <code>false</code>, subreddits that have hide_ads set to <code>true</code>
         * or are on the anti_ads_subreddits list will be filtered.
         * @param value     Filter value
         * @return  Builder object for chaining purposes
         */
        public Builder unAdvert(boolean value) {
            post(UNADVERT_DATA, value);
            return this;
        }

        /**
         * Search query
         * @param value     a string up to 50 characters long, consisting of printable characters
         * @return  Builder object for chaining purposes
         */
        public Builder query(String value) {
            post(QUERY_DATA, value);
            return this;
        }

        @Override
        public ApiSearchSubredditsRequest build() {
            ApiSearchSubredditsRequest request = new ApiSearchSubredditsRequest(
                    builder.build(),
                    ApiSearchSubredditsResponse.class);
            request.mDataMap = mDataMap;
            return request;
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
