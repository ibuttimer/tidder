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

package com.ianbuttimer.tidderish.reddit;

import android.util.JsonReader;

import com.ianbuttimer.tidderish.event.EventType;

import java.io.IOException;

/**
 * Base class for a reddit kind/data response which may be part of a listing or object response
 */

public abstract class KindDataResponse<T> extends Response<T> {

    static final String RESPONSE_KIND = "kind";
    static final String RESPONSE_DATA = "data";

    static final String KIND_LISTING = "Listing";

    public KindDataResponse() {
        super();
        init();
    }

    public KindDataResponse(@EventType int eventType) {
        super(eventType);
        init();
    }

    @Override
    protected void init() {
        setEmpty(true);
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, T obj) throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        setEmpty(false);

        boolean consumed = false;
        if (RESPONSE_DATA.equals(name)) {
            consumed = parseDataToken(jsonReader);
        } else if (RESPONSE_KIND.equals(name)) {
            String kind = nextString(jsonReader, "");
            consumed = isExpectedListingType(kind);
            if (!consumed) {
                throw new IllegalStateException("Incorrect listing type: got " + kind);
            }
        }

        return consumed;
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
        parseChildToken(jsonReader, null);
        return true;
    }

    /**
     * Parse a child object from the json string
     * @param jsonReader    Reader to use
     * @param type
     * @throws IOException
     * @throws IllegalArgumentException
     */
    protected abstract void parseChildToken(JsonReader jsonReader, String type)
            throws IOException, IllegalArgumentException;

    /**
     * Check if the listing type is expected by this response
     * @param type  Listing type in server response
     * @return  <code>true</code> if type is valid
     */
    protected abstract boolean isExpectedListingType(String type);

    /**
     * Check if the listing type is an expected child type of this response
     * @param type  Listing type in server response
     * @return  <code>true</code> if type is valid
     */
    protected abstract boolean isExpectedChildListingType(String type);
}

//{
//    "kind": "XXX",
//    "data": {
//          .....
//    }
//}
