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

import android.support.annotation.Nullable;
import android.util.JsonReader;

import com.ianbuttimer.tidder.event.EventType;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.KindDataResponse;
import com.ianbuttimer.tidder.reddit.Subreddit;

import java.io.IOException;


/**
 * Class representing the response from a GET  /r/<i>subreddit</i>/about request
 * @see <a href="https://www.reddit.com/dev/api#GET_r_{subreddit}_about">/r/<i>subreddit</i>/about</a>
 */

public class SubredditAboutResponse extends KindDataResponse {

    private Subreddit mSubreddit;

    /**
     * Default constructor
     */
    public SubredditAboutResponse() {
        super(EventType.SUBREDDIT_INFO_RESULT);
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public SubredditAboutResponse(String json) {
        this();
        parseJson(json);
    }

    @Override
    protected void init() {
        super.init();
        mSubreddit = null;
    }

    @Override
    protected BaseObject getInstance() {
        return new SubredditAboutResponse();
    }

    @Override
    protected boolean isExpectedListingType(String type) {
        return TYPE_SUBREDDIT.equals(type);
    }

    @Override
    protected boolean isExpectedChildListingType(String type) {
        return TYPE_SUBREDDIT.equals(type);
    }

    protected void parseChildToken(JsonReader jsonReader, String type) throws IOException, IllegalArgumentException {
        SubredditsSearchSubreddit child = new SubredditsSearchSubreddit();
        child.parseJson(jsonReader);
        mSubreddit = child;
    }

    @Nullable public Subreddit getSubreddit() {
        return mSubreddit;
    }

    public void setSubreddit(Subreddit subreddit) {
        this.mSubreddit = subreddit;
    }
}

//{
//    "kind": "t5",
//    "data": {
//        ....
//    }
//}
