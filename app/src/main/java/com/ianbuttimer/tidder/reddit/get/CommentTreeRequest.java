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

import com.ianbuttimer.tidder.reddit.ListingRequest;
import com.ianbuttimer.tidder.reddit.Response;

import static com.ianbuttimer.tidder.net.NetworkUtils.joinUrlPaths;
import static com.ianbuttimer.tidder.net.NetworkUtils.trimUrlPathStart;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_COMMENT_TREE_BASE_URL;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_COMMENT_TREE_URL_MID;

/**
 * A GET [/r/subreddit]/comments/article request<br>
 * Get the comment tree for a given Link article.
 * @see <a href="https://www.reddit.com/dev/api#GET_comments_{article}">[/r/subreddit]/comments/article</a>
 */

public class CommentTreeRequest extends ListingRequest {

    private static final String QUERY_COMMENT = "comment";  // (optional) ID36 of a comment
    private static final String QUERY_CONTEXT = "context";  // an integer between 0 and 8
    private static final String QUERY_DEPTH = "depth";      // (optional) an integer, maximum depth of subtrees in the thread
    private static final String QUERY_LIMIT = "limit";      // (optional) an integer, maximum number of comments to return
    private static final String QUERY_SHOWEDITS = "showedits";  // boolean value
    private static final String QUERY_SHOWMORE = "showmore";    // boolean value
    private static final String QUERY_SORT = "sort";            // one of (confidence, top, new, controversial, old, random, qa, live)
    private static final String QUERY_DETAIL = "sr_detail";// (optional) expand subreddits
    private static final String QUERY_THREADED = "threaded";    // boolean value
    private static final String QUERY_TRUNCATE = "truncate";    // an integer between 0 and 50

    private static final String QUERY_CONFIDENCE = "confidence";
    private static final String QUERY_TOP = "top";
    private static final String QUERY_NEW = "new";
    private static final String QUERY_CONTROVERSIAL = "controversial";
    private static final String QUERY_OLD = "old";
    private static final String QUERY_RANDOM = "random";
    private static final String QUERY_QA = "qa";
    private static final String QUERY_LIVE = "live";

    private static final Uri BASE_URI = Uri.parse(SUBREDDIT_COMMENT_TREE_BASE_URL);


    public CommentTreeRequest(Uri uri) {
        this(uri, null);
    }

    public CommentTreeRequest(Uri uri, @Nullable Class<? extends Response> responseClass) {
        super(uri, responseClass);
    }


    public static class Builder extends ListingRequest.Builder {

        protected static final int SUBREDDIT_SET = 0x01;
        protected static final int ARTICLE_SET = 0x02;
        protected static final int SORT_SET = 0x04;
        protected int mPathSet;

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
            mPathSet = 0;
        }

        /**
         * Set the subreddit field
         * @param subreddit    subreddit url/name
         * @return  Builder to facilitate chaining
         * @throws IllegalStateException    if subreddit has been already set
         * @throws IllegalArgumentException if no subreddit provided
         */
        public Builder subreddit(String subreddit) throws IllegalStateException {
            if (!isSet(SUBREDDIT_SET)) {
                String path = subredditRPath(subreddit);
                appendPath(trimUrlPathStart(
                        joinUrlPaths(path, SUBREDDIT_COMMENT_TREE_URL_MID)
                ));
                set(SUBREDDIT_SET);
            } else {
                throw new IllegalStateException("Subreddit already set");
            }
            return this;
        }

        /**
         * Set the subreddit field
         * @param article    ID36 of a link
         * @return  Builder to facilitate chaining
         * @throws IllegalStateException    if subreddit has not been already set or, article has already been set
         */
        public Builder article(String article) throws IllegalStateException {
            if (!isSet(SUBREDDIT_SET)) {
                throw new IllegalStateException("Subreddit not set");
            }
            if (!isSet(ARTICLE_SET)) {
                appendPath(trimUrlPathStart(article));
                set(ARTICLE_SET);
            } else {
                throw new IllegalStateException("Article already set");
            }
            return this;
        }

        /**
         * Set the subreddit and article fields
         * @param permalink    permalink
         * @return  Builder to facilitate chaining
         * @throws IllegalStateException    if subreddit or article have been already set
         */
        public Builder permalink(String permalink) throws IllegalStateException {
            if (!isSet(SUBREDDIT_SET) && !isSet(ARTICLE_SET)) {
                appendPath(trimUrlPathStart(permalink));
                set(SUBREDDIT_SET|ARTICLE_SET);
            } else {
                throw new IllegalStateException("Subreddit and/or article already set");
            }
            return this;
        }

        /**
         * Set the comment field
         * @param comment    ID36 of a comment to become focal point
         * @return  Builder to facilitate chaining
         */
        public Builder query(String comment) {
            appendQueryParameter(QUERY_COMMENT, comment);
            return this;
        }

        /**
         * Set the context field
         * @param context    number of parents shown for focal point comment
         * @return  Builder to facilitate chaining
         */
        public Builder context(int context) {
            appendQueryParameter(QUERY_CONTEXT, context, 0, 8);
            return this;
        }

        /**
         * Set the depth field
         * @param depth    maximum depth of subtrees in the thread
         * @return  Builder to facilitate chaining
         */
        public Builder depth(int depth) {
            appendQueryParameter(QUERY_DEPTH, depth);
            return this;
        }

        /**
         * Set the limit field
         * @param limit    maximum depth of subtrees in the thread
         * @return  Builder to facilitate chaining
         */
        public Builder limit(int limit) {
            appendQueryParameter(QUERY_LIMIT, limit);
            return this;
        }

        /**
         * Set the showedits field
         * @param showedits
         * @return  Builder to facilitate chaining
         */
        public Builder showedits(boolean showedits) {
            appendQueryParameter(QUERY_SHOWEDITS, showedits);
            return this;
        }

        /**
         * Set the showmore field
         * @param showmore
         * @return  Builder to facilitate chaining
         */
        public Builder showmore(boolean showmore) {
            appendQueryParameter(QUERY_SHOWMORE, showmore);
            return this;
        }

        /**
         * Set the sort field
         * @param sort    a search query
         * @return  Builder to facilitate chaining
         */
        public Builder sort(String sort) throws IllegalStateException {
            if (!isSet(SORT_SET)) {
                appendQueryParameter(QUERY_SORT, sort);
                set(SORT_SET);
            } else {
                throw new IllegalStateException("Sort already set");
            }
            return this;
        }

        /**
         * Set the sort field to confidence
         * @return  Builder to facilitate chaining
         */
        public Builder sortConfidence() {
            return sort(QUERY_CONFIDENCE);
        }

        /**
         * Set the sort field to top
         * @return  Builder to facilitate chaining
         */
        public Builder sortTop() {
            return sort(QUERY_TOP);
        }

        /**
         * Set the sort field to new
         * @return  Builder to facilitate chaining
         */
        public Builder sortNew() {
            return sort(QUERY_NEW);
        }

        /**
         * Set the sort field to controversial
         * @return  Builder to facilitate chaining
         */
        public Builder sortControversial() {
            return sort(QUERY_CONTROVERSIAL);
        }

        /**
         * Set the sort field to old
         * @return  Builder to facilitate chaining
         */
        public Builder sortOld() {
            return sort(QUERY_OLD);
        }

        /**
         * Set the sort field to random
         * @return  Builder to facilitate chaining
         */
        public Builder sortRandom() {
            return sort(QUERY_RANDOM);
        }

        /**
         * Set the sort field to qa
         * @return  Builder to facilitate chaining
         */
        public Builder sortQa() {
            return sort(QUERY_QA);
        }

        /**
         * Set the sort field to live
         * @return  Builder to facilitate chaining
         */
        public Builder sortLive() {
            return sort(QUERY_LIVE);
        }

//        sr_detail
//                (optional) expand subreddits

        /**
         * Set the threaded field
         * @param threaded
         * @return  Builder to facilitate chaining
         */
        public Builder threaded(boolean threaded) {
            appendQueryParameter(QUERY_THREADED, threaded);
            return this;
        }

        /**
         * Set the truncate field
         * @param truncate    an integer between 0 and 50
         * @return  Builder to facilitate chaining
         */
        public Builder truncate(int truncate) {
            appendQueryParameter(QUERY_TRUNCATE, truncate, 0, 50);
            return this;
        }

        @Override
        public CommentTreeRequest build() {
            return new CommentTreeRequest(
                    builder.build(),
                    CommentTreeResponse.class);
        }

        protected boolean isSet(int element) {
            return ((mPathSet & element) != 0);
        }

        protected int set(int element) {
            return (mPathSet |= element);
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
