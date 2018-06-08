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

import static com.ianbuttimer.tidder.net.RedditUriBuilder.ALL_SUBREDDITS_URL;

/**
 * A GET /subreddits.json request<br>
 * List all subreddits
 * @see <a href="https://www.reddit.com/dev/api#GET_subreddits_{where}">GET /subreddits/where</a>
 */

public class AllSubredditsRequest extends SubredditsSearchRequest {


    private static final Uri BASE_URI = Uri.parse(ALL_SUBREDDITS_URL);


    public AllSubredditsRequest(Uri uri) {
        this(uri, null);
    }

    public AllSubredditsRequest(Uri uri, @Nullable Class<? extends Response> responseClass) {
        super(uri, responseClass);
    }


    public static class Builder extends SubredditsSearchRequest.Builder {

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


        @Override
        public AllSubredditsRequest build() {
            return new AllSubredditsRequest(
                    builder.build(),
                    AllSubredditsResponse.class);
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
