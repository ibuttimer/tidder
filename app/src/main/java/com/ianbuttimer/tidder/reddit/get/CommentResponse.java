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
import com.ianbuttimer.tidder.reddit.CommentMore;
import com.ianbuttimer.tidder.reddit.ListingList;
import com.ianbuttimer.tidder.reddit.ListingResponse;

import java.io.IOException;


/**
 * Class representing the response from a [/r/subreddit]/comments/article request
 * @see <a href="https://www.reddit.com/dev/api#GET_comments_{article}">[/r/subreddit]/comments/article</a>
 */

public class CommentResponse extends ListingResponse<CommentResponse, Comment>
                                    implements ListingList<Comment> {

    /**
     * Default constructor
     */
    public CommentResponse() {
        super(EventType.GET_COMMENT_TREE_RESULT);
    }

    /**
     * Constructor
     * @param eventType     Event type for Event message
     */
    protected CommentResponse(@EventType int eventType) {
        super(eventType);
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public CommentResponse(String json) {
        this();
        parseJson(json);
    }

    @Override
    protected CommentResponse getInstance() {
        return new CommentResponse();
    }

    @Override
    public void parseJson(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (!parseToken(jsonReader, name)) {
                // not consumed so skip
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
    }


    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, CommentResponse obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        return super.parseToken(jsonReader, name, obj);
    }


    @Override
    protected void parseChildToken(JsonReader jsonReader, String type) throws IOException, IllegalArgumentException {
        Comment child;
        if (TYPE_MORE.equals(type)) {
            CommentMore more = new CommentMore();
            more.parseJson(jsonReader);
            child = more;
        } else {
            child = new Comment();
            child.parseJson(jsonReader);
        }
        mList.add(child);
    }

    @Override
    protected boolean isExpectedChildListingType(String type) {
        return (TYPE_COMMENT.equals(type) || TYPE_MORE.equals(type));
    }

}
