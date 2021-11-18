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
import com.ianbuttimer.tidderish.reddit.Comment;
import com.ianbuttimer.tidderish.reddit.Link;
import com.ianbuttimer.tidderish.reddit.ListingList;
import com.ianbuttimer.tidderish.reddit.ListingResponse;
import com.ianbuttimer.tidderish.reddit.RedditObject;
import com.ianbuttimer.tidderish.reddit.Subreddit;
import com.ianbuttimer.tidderish.utils.PreferenceControl;

import java.io.IOException;


/**
 * Class representing the response from a GET /api/info request
 * @see <a href="https://www.reddit.com/dev/api#GET_api_info">GET [/r/<i>subreddit</i>]/api/info</a>
 */

public class ThingAboutResponse extends ListingResponse<ThingAboutResponse, RedditObject>
                                    implements ListingList<RedditObject> {

    /**
     * Default constructor
     */
    public ThingAboutResponse() {
        super(EventType.THING_ABOUT_RESULT);
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public ThingAboutResponse(String json) {
        this();
        parseJson(json);
    }

    @Override
    protected ThingAboutResponse getInstance() {
        return new ThingAboutResponse();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, ThingAboutResponse obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        return super.parseToken(jsonReader, name, obj);
    }

    @Override
    protected void parseChildToken(JsonReader jsonReader, String type) throws IOException, IllegalArgumentException {
        RedditObject child = null;
        if (TYPE_LINK.equals(type)) {
            Link link = new Link();
            link.parseJson(jsonReader);

            child = link;
            if (link.isOver18()) {
                if (PreferenceControl.getSafeForWorkPreference(
                        TidderApplication.getWeakApplicationContext().get())) {
                    child = null;   // sfw enabled, don't add over18
                }
            }
        } else if (TYPE_COMMENT.equals(type)) {
            Comment comment = new Comment();
            comment.parseJson(jsonReader);
            child = comment;
        } else if (TYPE_SUBREDDIT.equals(type)) {
            Subreddit subreddit = new Subreddit();
            subreddit.parseJson(jsonReader);
            child = subreddit;
        }

        if (child != null) {
            mList.add(child);
        }
    }

    @Override
    protected boolean isExpectedChildListingType(String type) {
        return TYPE_LINK.equals(type) || TYPE_COMMENT.equals(type) || TYPE_SUBREDDIT.equals(type);
    }

}
