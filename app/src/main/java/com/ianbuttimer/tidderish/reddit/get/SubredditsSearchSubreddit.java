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

import com.ianbuttimer.tidderish.reddit.Subreddit;

import org.parceler.Parcel;

import java.io.IOException;

/**
 * Class for a subreddit object specific to the GET /subreddits/search API endpoint
 */

@Parcel
public class SubredditsSearchSubreddit extends Subreddit {

    // GET /subreddits/search specific fields
    protected static final String TITLE = "title";
    protected static final String SUBSCRIBERS = "subscribers";
    protected static final String DESCRIPTION = "public_description";
    protected static final String DESCRIPTION_HTML = "public_description_html";
    protected static final String DISPLAY_NAME = "display_name";
    protected static final String OVER_18 = "over18";
    protected static final String DISPLAY_NAME_PREFIXED = "display_name_prefixed";
    protected static final String URL = "url";
    protected static final String HEADER_IMG = "header_img";
    protected static final String BANNER_IMG = "banner_img";


    /**
     * Default constructor
     */
    public SubredditsSearchSubreddit() {
        super();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public SubredditsSearchSubreddit(String json) {
        super(json);
    }

    @Override
    protected SubredditsSearchSubreddit getInstance() {
        return new SubredditsSearchSubreddit();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, Subreddit obj)
                                    throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        SubredditsSearchSubreddit object = ((SubredditsSearchSubreddit) obj);
        boolean consumed = super.parseToken(jsonReader, name, object);
        if (!consumed) {
            consumed = true;
            if (DISPLAY_NAME.equals(name)) {
                object.setDisplayName(nextString(jsonReader, ""));
            } else if (DISPLAY_NAME_PREFIXED.equals(name)) {
                object.setDisplayNamePrefixed(nextString(jsonReader, ""));
            } else if (TITLE.equals(name)) {
                object.setTitle(nextString(jsonReader, ""));
            } else if (DESCRIPTION.equals(name)) {
                object.setDescription(nextString(jsonReader, ""));
            } else if (DESCRIPTION_HTML.equals(name)) {
                object.setDescriptionHtml(nextStringFromHtml(jsonReader, ""));
            } else if (SUBSCRIBERS.equals(name)) {
                object.setSuscribers(nextInt(jsonReader, 0));
            } else if (OVER_18.equals(name)) {
                object.setOver18(nextBoolean(jsonReader, false));
            } else if (HEADER_IMG.equals(name)) {
                object.setHeader(nextUri(jsonReader));
            } else if (BANNER_IMG.equals(name)) {
                object.setBanner(nextUri(jsonReader));
            } else if (URL.equals(name)) {
                object.setUrl(nextString(jsonReader, ""));
            } else {
                consumed = false;
            }
        }
        return consumed;
    }
}
