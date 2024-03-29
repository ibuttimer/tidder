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

import android.util.JsonReader;

import com.ianbuttimer.tidderish.TidderApplication;
import com.ianbuttimer.tidderish.event.EventType;
import com.ianbuttimer.tidderish.reddit.ListingList;
import com.ianbuttimer.tidderish.reddit.ListingResponse;
import com.ianbuttimer.tidderish.reddit.Subreddit;
import com.ianbuttimer.tidderish.utils.PreferenceControl;

import java.io.IOException;


/**
 * Class representing the response from a GET /subreddits/search
 * @see <a href="https://www.reddit.com/dev/api#GET_subreddits_search">/subreddits/search</a>
 */

public class SubredditsSearchResponse extends ListingResponse<SubredditsSearchResponse, Subreddit>
                                    implements ListingList<Subreddit> {

    /**
     * Default constructor
     */
    public SubredditsSearchResponse() {
        super(EventType.SEARCH_INTEREST_RESULT);
    }

    /**
     * Constructor
     * @param eventType     Event type for Event message
     */
    public SubredditsSearchResponse(@EventType int eventType) {
        super(eventType);
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public SubredditsSearchResponse(String json) {
        this();
        parseJson(json);
    }

    @Override
    protected SubredditsSearchResponse getInstance() {
        return new SubredditsSearchResponse();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, SubredditsSearchResponse obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        return super.parseToken(jsonReader, name, obj);
    }


    @Override
    protected void parseChildToken(JsonReader jsonReader, String type) throws IOException, IllegalArgumentException {
        SubredditsSearchSubreddit child = new SubredditsSearchSubreddit();
        child.parseJson(jsonReader);

        if (child.isOver18()) {
            if (PreferenceControl.getSafeForWorkPreference(
                    TidderApplication.getWeakApplicationContext().get())) {
                child = null;   // sfw enabled, don't add over18
            }
        }
        if (child != null) {
            mList.add(child);
        }
    }

    @Override
    protected boolean isExpectedChildListingType(String type) {
        return TYPE_SUBREDDIT.equals(type);
    }

}
