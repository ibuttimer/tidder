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

import com.ianbuttimer.tidder.event.EventType;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.ListingList;
import com.ianbuttimer.tidder.reddit.Subreddit;


/**
 * A GET /subreddits.json response<br>
 * List all subreddits
 * @see <a href="https://www.reddit.com/dev/api#GET_subreddits_{where}">GET /subreddits/where</a>
 */

public class AllSubredditsResponse extends SubredditsSearchResponse
                                    implements ListingList<Subreddit> {

    /**
     * Default constructor
     */
    public AllSubredditsResponse() {
        super(EventType.ALL_SUBREDDIT_RESULT);
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public AllSubredditsResponse(String json) {
        this();
        super.parseJson(json);
    }

    @Override
    protected BaseObject getInstance() {
        return new AllSubredditsResponse();
    }


}
