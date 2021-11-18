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

package com.ianbuttimer.tidderish.reddit.post;

import android.util.JsonReader;

import com.ianbuttimer.tidderish.reddit.Subreddit;

import org.parceler.Parcel;

import java.io.IOException;

/**
 * Class for a subreddit object specific to the POST /api/search_subreddits API endpoint
 */

@Parcel
public class ApiSearchSubredditsSubreddit extends Subreddit {

    // POST /api/search_subreddits response specific fields
    protected static final String NAME = "name";
    protected static final String SUBSCRIBER_CNT = "subscriber_count";

    /**
     * Default constructor
     */
    public ApiSearchSubredditsSubreddit() {
        super();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public ApiSearchSubredditsSubreddit(String json) {
        super(json);
    }

    @Override
    protected ApiSearchSubredditsSubreddit getInstance() {
        return new ApiSearchSubredditsSubreddit();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, Subreddit obj)
                                        throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

//    {
//        "active_user_count": 23732,
//        "icon_img": "https://a.thumbs.redditmedia.com/E0Bkwgwe5TkVLflBA7WMe9fMSC7DV2UOeff-UpNJeb0.png",
//        "key_color": "#24a0ed",
//        "name": "news",
//        "subscriber_count": 15570473,
//        "allow_images": false
//    }

        ApiSearchSubredditsSubreddit object = ((ApiSearchSubredditsSubreddit) obj);
        boolean consumed = super.parseToken(jsonReader, name, object);
        if (consumed) {
            // special case 'name' is standard field for fullname but here is used for display name
            if (NAME.equals(name)) {
                object.setDisplayName(object.getName());
                object.setName("");
            }
        } else {
            consumed = true;
            if (SUBSCRIBER_CNT.equals(name)) {
                object.mSuscribers = nextInt(jsonReader, 0);
            } else {
                consumed = false;
            }
        }
        return consumed;
    }
}
