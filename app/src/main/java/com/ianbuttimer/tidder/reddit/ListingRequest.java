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

package com.ianbuttimer.tidder.reddit;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.ianbuttimer.tidder.event.AbstractEvent;

/**
 * Base class for listing requests
 * @see <a href="https://www.reddit.com/dev/api#listings">listings</a>
 */

public abstract class ListingRequest extends Request {

    // after / before - only one should be specified. these indicate the fullname of an item in the listing to use as the anchor point of the slice.
    static final String QUERY_AFTER = "after";      // anchor point of the slice, fullname of a thing
    static final String QUERY_BEFORE = "before";    // anchor point of the slice, fullname of a thing

    static final String QUERY_LIMIT = "limit";    // the maximum number of items to return in this slice of the listing.
    static final String QUERY_COUNT = "count";    // the number of items already seen in this listing. on the html site, the builder uses this to determine when to give values for before and after in the response.
    static final String QUERY_SHOW = "show";    // optional parameter; if all is passed, filters such as "hide links that I have voted on" will be disabled.

    public static final int QUERY_LIMIT_DEFAULT = 25;
    public static final int QUERY_LIMIT_MAX = 100;
    public static final int QUERY_COUNT_DEFAULT = 0;
    public static final String QUERY_SHOW_ALL = "all";


    public ListingRequest(Uri uri) {
        this(uri, null);
    }

    public ListingRequest(Uri uri, @Nullable Class<? extends Response> responseClass) {
        super(uri, responseClass);
    }

    public abstract static class Builder extends Request.Builder {

        /**
         * Constructor
         */
        public Builder() {
            super();
        }

        /**
         * Constructor
         */
        public Builder(String urlString) {
            super(urlString);
        }

        /**
         * Constructor
         */
        public Builder(Uri uri) {
            super(uri);
        }

        /**
         * Set the after field
         * @param after    fullname of a thing
         * @return  Builder to facilitate chaining
         */
        public Builder after(String after) {
            appendQueryParameter(QUERY_AFTER, after);
            return this;
        }

        /**
         * Set the before field
         * @param before    fullname of a thing
         * @return  Builder to facilitate chaining
         */
        public Builder before(String before) {
            appendQueryParameter(QUERY_BEFORE, before);
            return this;
        }

        /**
         * Set the show field
         * @param show    show value
         * @return  Builder to facilitate chaining
         */
        public Builder show(String show) {
            appendQueryParameter(QUERY_SHOW, show);
            return this;
        }

        /**
         * Set the show field to show all
         * @return  Builder to facilitate chaining
         */
        public Builder showAll() {
            appendQueryParameter(QUERY_SHOW, QUERY_SHOW_ALL);
            return this;
        }

        /**
         * Set the count field
         * @param count    a positive integer (default: 0)
         * @return  Builder to facilitate chaining
         */
        public Builder count(int count) {
            if (count < 0) {
                count = 0;
            }
            appendQueryParameter(QUERY_COUNT, count);
            return this;
        }

        /**
         * Set the limit field
         * @param limit    the maximum number of items desired (default: 25, maximum: 100)
         * @return  Builder to facilitate chaining
         */
        public Builder limit(int limit) {
            if (limit <= 0) {
                limit = QUERY_LIMIT_DEFAULT;
            } else if (limit > QUERY_LIMIT_MAX) {
                limit = QUERY_LIMIT_MAX;
            }
            appendQueryParameter(QUERY_LIMIT, limit);
            return this;
        }

        /**
         * Set the listing fields
         * @param event     The request event
         * @return  Builder to facilitate chaining
         */
        public Builder listing(AbstractEvent event) {
            if (event != null) {
                before(event.getBefore());
                after(event.getAfter());
                count(event.getCount());
                limit(event.getLimit());
            }
            return this;
        }

    }
}
