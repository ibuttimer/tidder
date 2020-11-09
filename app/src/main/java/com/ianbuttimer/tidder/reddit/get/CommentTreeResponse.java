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

import androidx.annotation.Nullable;
import android.util.JsonReader;

import com.ianbuttimer.tidder.event.EventType;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.ListingList;
import com.ianbuttimer.tidder.reddit.ListingResponse;

import java.io.IOException;


/**
 * Class representing the response from a [/r/subreddit]/comments/article request
 * @see <a href="https://www.reddit.com/dev/api#GET_comments_{article}">[/r/subreddit]/comments/article</a>
 */

public class CommentTreeResponse extends ListingResponse<CommentTreeResponse, Comment>
                                    implements ListingList<Comment> {

    private Link mLink = null;

    /**
     * Default constructor
     */
    public CommentTreeResponse() {
        super(EventType.GET_COMMENT_TREE_RESULT);
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public CommentTreeResponse(String json) {
        this();
        parseJson(json);
    }

    @Override
    protected CommentTreeResponse getInstance() {
        return new CommentTreeResponse();
    }

    @Override
    public void parseJson(JsonReader jsonReader) throws IOException {
        // there are 2 entries in an array; a link response & a comments response
        int index = 0;
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            switch (index) {
                case 0:
                    SubredditLinkResponse linkResponse = new SubredditLinkResponse();
                    linkResponse.parseJson(jsonReader);
                    mLink = linkResponse.getItem(0);    // should be only one item
                    break;
                case 1:
                    CommentResponse commentResponse = new CommentResponse();
                    commentResponse.parseJson(jsonReader);
                    mList = commentResponse.getList();
                    break;
                default:
                    jsonReader.skipValue();
            }
            ++index;
        }
        jsonReader.endArray();
    }


    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, CommentTreeResponse obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        return super.parseToken(jsonReader, name, obj);
    }


    @Override
    protected void parseChildToken(JsonReader jsonReader, String type) throws IOException, IllegalArgumentException {
        // no op
    }

    @Override
    protected boolean isExpectedChildListingType(String type) {
        return TYPE_COMMENT.equals(type);
    }

    @Nullable public Link getLink() {
        return mLink;
    }

}
