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

import timber.log.Timber;

import static com.ianbuttimer.tidder.net.NetworkUtils.joinUrlPaths;
import static com.ianbuttimer.tidder.net.NetworkUtils.trimUrlPathStart;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_COMMENT_MORE_BASE_URL;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_COMMENT_TREE_BASE_URL;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_COMMENT_TREE_URL_MID;

/**
 * A GET /api/morechildren request<br>
 Retrieve additional comments omitted from a base comment tree.
 * @see <a href="https://www.reddit.com/dev/api#GET_api_morechildren">GET /api/morechildren</a>
 */

public class CommentMoreRequest extends CommentTreeRequest {

    private static final String QUERY_API_TYPE = "api_type";    // the string json
    private static final String QUERY_CHILDREN = "children";    // a comma-delimited list of comment ID36s
    private static final String QUERY_ID = "id";                // (optional) id of the associated MoreChildren object
    private static final String QUERY_LIMIT = "limit_children"; // If limit_children is True, only return the children requested
    private static final String QUERY_LINK_ID = "link_id";      // fullname of a link

    private static final Uri BASE_URI = Uri.parse(SUBREDDIT_COMMENT_MORE_BASE_URL);


    public CommentMoreRequest(Uri uri) {
        this(uri, null);
    }

    public CommentMoreRequest(Uri uri, @Nullable Class<? extends Response> responseClass) {
        super(uri, responseClass);
    }


    public static class Builder extends CommentTreeRequest.Builder {

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
            appendQueryParameter(QUERY_API_TYPE, "json");
        }

        @Override
        public Builder subreddit(String subreddit) {
            // not relevant ro this builder
            return this;
        }

        @Override
        public Builder article(String article) {
            // not relevant ro this builder
            return this;
        }

        @Override
        public Builder permalink(String permalink) {
            // not relevant ro this builder
            return this;
        }

        @Override
        public Builder query(String comment) {
            // not relevant ro this builder
            return this;
        }

        /**
         * Set the children field
         * @param children  comma-delimited list of comment ID36s
         * @return  Builder to facilitate chaining
         */
        public Builder children(String children) {
            appendQueryParameter(QUERY_CHILDREN, children);
            return this;
        }

        /**
         * Set the children field
         * @param children  list of comment ID36s
         * @return  Builder to facilitate chaining
         */
        public Builder children(String... children) {
            return children(TextUtils.join(",", children));
        }

        /**
         * Set the limit children field
         * @param limit only return the children requested flag
         * @return  Builder to facilitate chaining
         */
        public Builder limitChildren(boolean limit) {
            appendQueryParameter(QUERY_LIMIT, limit);
            return this;
        }

        /**
         * Set the id field
         * @param id    id of the associated MoreChildren object
         * @return  Builder to facilitate chaining
         */
        public Builder id(String id) {
            appendQueryParameter(QUERY_ID, id);
            return this;
        }

        /**
         * Set the link id field
         * @param id    fullname of a link
         * @return  Builder to facilitate chaining
         */
        public Builder linkId(String id) {
            appendQueryParameter(QUERY_LINK_ID, id);
            return this;
        }

//        sr_detail
//                (optional) expand subreddits

        @Override
        public CommentMoreRequest build() {
            return new CommentMoreRequest(
                    builder.build(),
                    CommentMoreResponse.class);
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
