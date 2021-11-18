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

package com.ianbuttimer.tidderish.event;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
public @interface EventType {

    // Common events
    int TYPE_NA = 0;
    int FACTORY_INSTANCE = 1;

    /* >>>>>>> StandardEvent-specific events >>>>>>> */

    int STANDARD_EVENT_TYPE_START = 100;

    /** Query Follow Status of Subreddit List request */
    int QUERY_FOLLOW_STATUS_LIST_REQUEST = STANDARD_EVENT_TYPE_START + 1;

    /** Get Subreddit Following List request */
    int FOLLOWING_LIST_REQUEST = STANDARD_EVENT_TYPE_START + 2;
    /** Get Subreddit Following List response */
    int FOLLOWING_LIST_RESULT = STANDARD_EVENT_TYPE_START + 3;

    /** Get Pinned List request */
    int PINNED_LIST_REQUEST = STANDARD_EVENT_TYPE_START + 4;
    /** Get Pinned List response */
    int PINNED_LIST_RESULT = STANDARD_EVENT_TYPE_START + 5;

    /** Subreddit info request */
    int SUBREDDIT_INFO_REQUEST = STANDARD_EVENT_TYPE_START + 6;
    /** Subreddit info result */
    int SUBREDDIT_INFO_RESULT = STANDARD_EVENT_TYPE_START + 7;

    /** Subreddit/Comment info request */
    int THING_ABOUT_REQUEST = STANDARD_EVENT_TYPE_START + 8;
    /** Subreddit/Comment info result */
    int THING_ABOUT_RESULT = STANDARD_EVENT_TYPE_START + 9;

    // full variant specific events

    /** Settings info request */
    int SETTINGS_REQUEST = STANDARD_EVENT_TYPE_START + 10;
    /** Settings info result */
    int SETTINGS_RESULT = STANDARD_EVENT_TYPE_START + 11;

    /* <<<<<<< StandardEvent-specific events <<<<<<< */

    /* >>>>>>> FollowEvent-specific events >>>>>>> */

    int FOLLOW_EVENT_TYPE_START = 200;

    /** Subreddit search interests request */
    int SEARCH_INTEREST_REQUEST = FOLLOW_EVENT_TYPE_START + 1;
    /** Subreddit search interests result */
    int SEARCH_INTEREST_RESULT = FOLLOW_EVENT_TYPE_START + 2;
    /** Subreddit search names request */
    int SEARCH_NAME_REQUEST = FOLLOW_EVENT_TYPE_START + 3;
    /** Subreddit search names result */
    int SEARCH_NAME_RESULT = FOLLOW_EVENT_TYPE_START + 4;

    /** Subreddit Follow State Change request */
    int FOLLOW_STATE_CHANGE_REQUEST = FOLLOW_EVENT_TYPE_START + 5;

    /** All subreddits request */
    int ALL_SUBREDDIT_REQUEST = FOLLOW_EVENT_TYPE_START + 6;
    /** All subreddits result */
    int ALL_SUBREDDIT_RESULT = FOLLOW_EVENT_TYPE_START + 7;

    /* <<<<<<< FollowEvent-specific events <<<<<<< */

    /* >>>>>>> PostEvent-specific events >>>>>>> */

    int POST_EVENT_TYPE_START = 300;

    /** View Comment Thread request */
    int VIEW_THREAD_REQUEST = POST_EVENT_TYPE_START + 1;

    /** Get Comment Tree request */
    int GET_COMMENT_TREE_REQUEST = POST_EVENT_TYPE_START + 2;
    /** Get Subreddit Post response */
    int GET_COMMENT_TREE_RESULT = POST_EVENT_TYPE_START + 3;

    /** Get Comment More request */
    int GET_COMMENT_MORE_REQUEST = POST_EVENT_TYPE_START + 4;
    /** Get Comment More response */
    int GET_COMMENT_MORE_RESULT = POST_EVENT_TYPE_START + 5;

    /** Pinned Status change occurred */
    int PINNED_STATUS_CHANGE = POST_EVENT_TYPE_START + 6;


    /* <<<<<<< PostEvent-specific events <<<<<<< */

    /* >>>>>>> PostsEvent-specific events >>>>>>> */

    int POSTS_EVENT_TYPE_START = 400;

    /** View Subreddit Post request */
    int VIEW_POST_REQUEST = POSTS_EVENT_TYPE_START + 1;

    /** Get Subreddit Post request */
    int GET_POST_REQUEST = POSTS_EVENT_TYPE_START + 2;
    /** Get Subreddit Post response */
    int GET_POST_RESULT = POSTS_EVENT_TYPE_START + 3;

    /** Refresh posts command */
    int REFRESH_POSTS_CMD = POSTS_EVENT_TYPE_START + 4;
    /** Clear posts command */
    int CLEAR_POSTS_CMD = POSTS_EVENT_TYPE_START + 5;

    /* <<<<<<< PostsEvent-specific events <<<<<<< */

    /* >>>>>>> RedditClientEvent-specific events >>>>>>> */

    int REDDITCLIENT_EVENT_TYPE_START = 500;

    int STATUS_CHANGE = REDDITCLIENT_EVENT_TYPE_START + 1;
    int AUTH_ERROR = REDDITCLIENT_EVENT_TYPE_START + 2;
    int USER_VALID = REDDITCLIENT_EVENT_TYPE_START + 3;
    int COMMS_ERROR = REDDITCLIENT_EVENT_TYPE_START + 4;

    /* <<<<<<< RedditClientEvent-specific events <<<<<<< */


        /** Defines the allowed constants for this element */
    int[] value() default {
        TYPE_NA, FACTORY_INSTANCE,

        // StandardEvent
        QUERY_FOLLOW_STATUS_LIST_REQUEST,
        FOLLOWING_LIST_REQUEST, FOLLOWING_LIST_RESULT,
        PINNED_LIST_REQUEST, PINNED_LIST_RESULT,
        SUBREDDIT_INFO_REQUEST, SUBREDDIT_INFO_RESULT,
        THING_ABOUT_REQUEST, THING_ABOUT_RESULT,
        SETTINGS_REQUEST, SETTINGS_RESULT,

        // FollowEvent
        SEARCH_INTEREST_REQUEST, SEARCH_INTEREST_RESULT,
        SEARCH_NAME_REQUEST, SEARCH_NAME_RESULT,
        FOLLOW_STATE_CHANGE_REQUEST,
        ALL_SUBREDDIT_REQUEST, ALL_SUBREDDIT_RESULT,

        // PostEvent
        VIEW_THREAD_REQUEST,
        GET_COMMENT_TREE_REQUEST, GET_COMMENT_TREE_RESULT,
        GET_COMMENT_MORE_REQUEST, GET_COMMENT_MORE_RESULT,
        PINNED_STATUS_CHANGE,

        // PostsEvent
        VIEW_POST_REQUEST,
        GET_POST_REQUEST, GET_POST_RESULT,
        REFRESH_POSTS_CMD, CLEAR_POSTS_CMD,

        // RedditClientEvent
        STATUS_CHANGE, AUTH_ERROR,
        USER_VALID, COMMS_ERROR,

    };

    /** Defines whether the constants can be used as a flag, or just as an enum (the default) */
    boolean flag() default false;
}

