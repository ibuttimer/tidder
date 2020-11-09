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

import android.util.JsonReader;

import com.ianbuttimer.tidder.event.EventType;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.ListingList;

import java.io.IOException;


/**
 * Class representing the response from a [/r/subreddit]/comments/article request
 * @see <a href="https://www.reddit.com/dev/api#GET_comments_{article}">[/r/subreddit]/comments/article</a>
 */

public class CommentMoreResponse extends CommentResponse
                                    implements ListingList<Comment> {

    protected static final String RESPONSE_JSON = "json";
    protected static final String RESPONSE_ERRORS = "errors";
    protected static final String RESPONSE_DATA = "data";
    protected static final String RESPONSE_THINGS = "things";

    /**
     * Default constructor
     */
    public CommentMoreResponse() {
        super(EventType.GET_COMMENT_MORE_RESULT);
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public CommentMoreResponse(String json) {
        this();
        parseJson(json);
    }

    @Override
    protected CommentMoreResponse getInstance() {
        return new CommentMoreResponse();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, CommentResponse obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            if (RESPONSE_JSON.equals(name)) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String key = jsonReader.nextName();
                    if (RESPONSE_ERRORS.equals(key)) {
                        jsonReader.skipValue();
                    } else if (RESPONSE_DATA.equals(key)) {
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            key = jsonReader.nextName();
                            if (RESPONSE_THINGS.equals(key)) {
                                parseChildArray(jsonReader);
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                    }
                }
                jsonReader.endObject();
                consumed = true;
            }
        }


        return consumed;
    }


    @Override
    protected boolean isExpectedChildListingType(String type) {
        return (TYPE_COMMENT.equals(type) || TYPE_MORE.equals(type));
    }
}
