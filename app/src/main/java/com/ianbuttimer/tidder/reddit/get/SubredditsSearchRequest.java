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

import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.ListingRequest;

import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDITS_SEARCH_URL;

/**
 * A GET /subreddits/search request<br>
 * Search subreddits by title and description.
 * @see <a href="https://www.reddit.com/dev/api#GET_subreddits_search">/subreddits/search</a>
 */

public class SubredditsSearchRequest extends ListingRequest {

    private static final String QUERY_QUERY = "q";    // a search query
    private static final String QUERY_SORT = "sort";
    private static final String QUERY_DETAIL = "sr_detail"; // (optional) expand subreddits

    private static final String QUERY_RELEVANCE = "relevance";
    private static final String QUERY_ACTIVITY = "activity";

    private static final Uri BASE_URI = Uri.parse(SUBREDDITS_SEARCH_URL);


    public SubredditsSearchRequest(Uri uri) {
        this(uri, null);
    }

    public SubredditsSearchRequest(Uri uri, @Nullable Class<? extends Response> responseClass) {
        super(uri, responseClass);
    }


    public static class Builder extends ListingRequest.Builder {

        /**
         * Constructor
         */
        public Builder() {
            this(BASE_URI);
        }

        /**
         * Constructor
         */
        public Builder(Uri uri) {
            super(uri);
        }

        /**
         * Set the query field
         * @param query    a search query
         * @return  Builder to facilitate chaining
         */
        public Builder query(String query) {
            appendQueryParameter(QUERY_QUERY, query);
            return this;
        }

        /**
         * Set the sort field
         * @param sort    a search query
         * @return  Builder to facilitate chaining
         */
        public Builder sort(String sort) {
            appendQueryParameter(QUERY_SORT, sort);
            return this;
        }

        /**
         * Set the sort field to relevance
         * @return  Builder to facilitate chaining
         */
        public Builder sortRelevance() {
            return sort(QUERY_RELEVANCE);
        }

        /**
         * Set the sort field to activity
         * @return  Builder to facilitate chaining
         */
        public Builder sortActivity() {
            return sort(QUERY_ACTIVITY);
        }

//        sr_detail
//                (optional) expand subreddits

        @Override
        public SubredditsSearchRequest build() {
            return new SubredditsSearchRequest(
                    builder.build(),
                    SubredditsSearchResponse.class);
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
