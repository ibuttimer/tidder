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

import android.support.annotation.Nullable;
import android.util.JsonReader;

import com.ianbuttimer.tidder.event.EventType;

import java.io.IOException;
import java.util.ArrayList;

import static com.ianbuttimer.tidder.reddit.ListingRequest.QUERY_AFTER;
import static com.ianbuttimer.tidder.reddit.ListingRequest.QUERY_BEFORE;

/**
 * Base class for a reddit listing response
 */

public abstract class ListingResponse<T extends BaseObject>
                            extends KindDataResponse
                            implements ListingList<T> {

    static final String RESPONSE_DIST = "dist";
    static final String RESPONSE_CHILDREN = "children";
    static final String RESPONSE_AFTER = QUERY_AFTER;
    static final String RESPONSE_BEFORE = QUERY_BEFORE;

    protected String mAfter;
    protected String mBefore;
    protected int mDist;

    protected ArrayList<T> mList;


    public ListingResponse() {
        super();
        init();
    }

    public ListingResponse(@EventType int eventType) {
        super(eventType);
        init();
    }

    @Override
    protected void init() {
        super.init();
        mAfter = "";
        mBefore = "";
        mDist = 0;
        mList = new ArrayList<>();
    }

    /**
     * Parse the data field of the json string
     * @param jsonReader    Reader to use
     * @return  <code>true</code> indicating taken has been consumed
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Override
    protected boolean parseDataToken(JsonReader jsonReader) throws IOException, IllegalArgumentException {
        // parse the listing object
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (RESPONSE_AFTER.equals(name)) {
                mAfter = nextString(jsonReader, "");
            } else if (RESPONSE_BEFORE.equals(name)) {
                mBefore = nextString(jsonReader, "");
            } else if (RESPONSE_DIST.equals(name)) {
                mDist = nextInt(jsonReader, 0);
            } else if (RESPONSE_CHILDREN.equals(name)) {
                parseChildArray(jsonReader);
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return true;
    }

    /**
     * Parse an array of children objects from the json string
     * @param jsonReader    Reader to use
     * @return  <code>true</code> indicating taken has been consumed
     * @throws IOException
     * @throws IllegalArgumentException
     */
    protected boolean parseChildArray(JsonReader jsonReader) throws IOException, IllegalArgumentException {
        String kind = null;
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (RESPONSE_DATA.equals(name)) {
                    parseChildToken(jsonReader, kind);
                    kind = null;
                } else if (RESPONSE_KIND.equals(name)) {
                    kind = nextString(jsonReader, "");
                    if (!isExpectedChildListingType(kind)) {
                        throw new IllegalStateException("Incorrect listing type: got " + kind);
                    }
                } else {
                    // skip unknown
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        }
        jsonReader.endArray();
        return true;
    }

    @Override
    protected boolean isExpectedListingType(String type) {
        return KIND_LISTING.equals(type);
    }

    protected abstract void parseChildToken(JsonReader jsonReader, String type)
            throws IOException, IllegalArgumentException;

    @Override
    public String getAfter() {
        return mAfter;
    }

    @Override
    public String getBefore() {
        return mBefore;
    }

    public int getDist() {
        return mDist;
    }

    @Override
    public ArrayList<T> getList() {
        return mList;
    }

    @Override
    @Nullable
    public T getItem(int index) {
        T item = null;
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

}

//{
//    "kind": "Listing",
//    "data": {
//        "modhash": null,
//        "whitelist_status": "all_ads",
//        "children": [
//          {
//              "kind": "t5",
//              "data": {
//                      ..........
//              }
//          }
//        ],
//        "after": "t5_2qkju",
//        "before": null
//    }
//}
