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

package com.ianbuttimer.tidder.reddit;

import androidx.annotation.Nullable;
import android.util.JsonReader;

import com.ianbuttimer.tidder.reddit.get.SubredditsSearchSubreddit;

import java.io.IOException;

/**
 * Base class for a reddit kind/data object reader
 */

public class SubredditReader extends KindDataReader {

    private Subreddit mSubreddit = null;

    public SubredditReader() {
        super(BaseObject.TYPE_SUBREDDIT);
    }

    /**
     * Parse the data field of the json string.<br>
     * <b>Default implementation is to parse a single child object.</b>
     * @param jsonReader    Reader to use
     * @return  <code>true</code> indicating taken has been consumed
     * @throws IOException
     * @throws IllegalArgumentException
     */
    protected boolean parseDataToken(JsonReader jsonReader) throws IOException, IllegalArgumentException {
        // default implementation is to parse a single child object
        parseChildToken(jsonReader);
        return true;
    }

    /**
     * Parse a child object from the json string
     * @param jsonReader    Reader to use
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Override
    protected void parseChildToken(JsonReader jsonReader) throws IOException, IllegalArgumentException {
        SubredditsSearchSubreddit subreddit = new SubredditsSearchSubreddit();
        subreddit.parseJson(jsonReader);

        mSubreddit = subreddit;
    }

    @Nullable
    public Subreddit getSubreddit() {
        return mSubreddit;
    }

}

//{
//    "kind": "XXX",
//    "data": {
//          .....
//    }
//}
