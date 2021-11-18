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
import android.text.TextUtils;

import com.ianbuttimer.tidderish.reddit.BaseObject;
import com.ianbuttimer.tidderish.reddit.Request;
import com.ianbuttimer.tidderish.reddit.Response;

import static com.ianbuttimer.tidderish.net.RedditUriBuilder.THING_ABOUT_URL;


/**
 * A GET /api/info request
 * @see <a href="https://www.reddit.com/dev/api#GET_api_info">GET [/r/<i>subreddit</i>]/api/info</a>
 */

public class ThingAboutRequest extends Request {

    private static final Uri BASE_URI = Uri.parse(THING_ABOUT_URL);

    public ThingAboutRequest(Uri uri) {
        super(uri);
    }

    public ThingAboutRequest(Uri uri, Class<? extends Response<? extends BaseObject<?>>> responseClass) {
        super(uri, responseClass);
    }

    public static class Builder extends Request.Builder {

        static final String QUERY_ID = "id";      // A comma-separated list of thing fullnames

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
         * Set id field
         * @param id    fullname of a thing
         * @return  Builder to facilitate chaining
         * @throws IllegalStateException    if id has been already set
         */
        public Builder id(String id) throws IllegalStateException {
            if (!isValid()) {
                appendQueryParameter(QUERY_ID, id);
                setValid(true);
            } else {
                throw new IllegalStateException("Id(s) already set");
            }
            return this;
        }

        /**
         * Set id field
         * @param ids   List of fullname of a things
         * @return  Builder to facilitate chaining
         * @throws IllegalStateException    if id has been already set
         */
        public Builder id(String... ids) throws IllegalStateException {
            return id(TextUtils.join(",", ids));
        }

        /**
         * {@inheritDoc}
         * @throws IllegalStateException    if id has not been set
         */
        @Override
        public ThingAboutRequest build() {
            if (!isValid()) {
                throw new IllegalStateException("Id not set");
            }
            return new ThingAboutRequest(builder.build(), ThingAboutResponse.class);
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
