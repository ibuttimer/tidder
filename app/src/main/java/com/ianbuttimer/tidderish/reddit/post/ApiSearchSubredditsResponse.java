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

import androidx.annotation.Nullable;
import android.util.JsonReader;

import com.ianbuttimer.tidderish.event.EventType;
import com.ianbuttimer.tidderish.reddit.ListingList;
import com.ianbuttimer.tidderish.reddit.Response;
import com.ianbuttimer.tidderish.reddit.Subreddit;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Class representing the response from a POST /api/search_subreddits
 * @see <a href="https://www.reddit.com/dev/api#POST_api_search_subreddits">/api/search_subreddits</a>
 */

public class ApiSearchSubredditsResponse extends Response<ApiSearchSubredditsResponse>
        implements ListingList<Subreddit> {

    private static final String SUBREDDITS = "subreddits";


    private ArrayList<Subreddit> mList;

    /**
     * Default constructor
     */
    public ApiSearchSubredditsResponse() {
        super(EventType.SEARCH_NAME_RESULT);
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create object
     */
    public ApiSearchSubredditsResponse(String json) {
        this();
        super.parseJson(json);
    }

    @Override
    protected void init() {
        mList = new ArrayList<>();
    }

    @Override
    protected ApiSearchSubredditsResponse getInstance() {
        return new ApiSearchSubredditsResponse();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, ApiSearchSubredditsResponse obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = true;
        if (SUBREDDITS.equals(name)) {
            new ApiSearchSubredditsSubreddit().parseJsonArray(jsonReader, mList);
        } else {
            consumed = false;
        }
        return consumed;
    }

    @Override
    public ArrayList<Subreddit> getList() {
        return mList;
    }

    @Override
    @Nullable
    public Subreddit getItem(int index) {
        Subreddit item = null;
        if ((mList != null) && !mList.isEmpty()) {
            if ((index >= 0) && (index < mList.size())) {
                item = mList.get(index);
            }
        }
        return item;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mList != null) {
            count = mList.size();
        }
        return count;
    }

    @Override
    public String getAfter() {
        return "";
    }

    @Override
    public String getBefore() {
        return "";
    }
}
//{
//    "subreddits": [
//        {
//        "active_user_count": 11067,
//        "icon_img": "https://a.thumbs.redditmedia.com/E0Bkwgwe5TkVLflBA7WMe9fMSC7DV2UOeff-UpNJeb0.png",
//        "key_color": "#24a0ed",
//        "name": "news",
//        "subscriber_count": 15582958,
//        "allow_images": false
//        },
//        ............
//    ]
//}