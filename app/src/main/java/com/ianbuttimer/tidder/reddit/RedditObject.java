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

import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.Date;


/**
 * Base class for a subreddit object.
 * See sub classes for class specific details.
 */
public abstract class RedditObject<T extends BaseObject, P extends AbstractProxy> extends BaseObject<T>
                            implements RedditCache.ICacheable {

    protected static final String ID = "id";
    protected static final String FULLNAME = "name";
    protected static final String CREATED = "created_utc";

    protected String mName;                 // fullname of object, e.g. "t5_2qh3l"
    protected String mId;                   // unique id, e.g. "2qh3l"
    protected Date mCreated;

    protected Tag mTag;

    /**
     * Default constructor
     */
    public RedditObject() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public RedditObject(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        mName = "";
        mId = "";
        mCreated = null;

        mTag = null;
    }

    public boolean copy(RedditObject object) {
        return Utils.copyFields(object, this);
    }

    /**
     * Get the reddit type
     * @return Type string
     */
    public abstract String getRedditType();

    /**
     * Get a proxy for this object
     * @return  Proxy object
     */
    public abstract P getProxy();

    /**
     * Add this object to the appropriate cache
     * @return  Proxy object
     */
    public abstract P addToCache();


    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, T obj)
                                            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        RedditObject object = ((RedditObject) obj);
        boolean consumed = true;
        if (ID.equals(name)) {
            object.mId = nextString(jsonReader, "");
        } else if (FULLNAME.equals(name)) {
            object.mName = nextString(jsonReader, "");
        } else if (CREATED.equals(name)) {
            // created date is in epoch seconds
            object.mCreated = nextDate(jsonReader);
        } else {
            consumed = false;
        }
        return consumed;
    }

    /**
     * Get the fullname of object, e.g. "t5_2qh3l"
     * @return  name
     */
    public String getName() {
        return mName;
    }

    @Override
    public String getCacheKey() {
        return getName();
    }

    /**
     * Get the fullname of object, e.g. "t5_2qh3l"
     * @param name  Fullname
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Get the id of the object, e.g. "2qh3l"
     * @return
     */
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Construct the fullname of the object from its id & type, e.g. "t5_2qh3l"
     * @return  object fullname
     */
    public String getFullname() {
        return makeFullname(getRedditType(), mId);
    }

    @Nullable
    public Date getCreated() {
        return mCreated;
    }

    public void setCreated(Date created) {
        this.mCreated = created;
    }

    @Nullable
    public Tag getTag() {
        return mTag;
    }

    public void setTag(Tag tag) {
        this.mTag = tag;
    }

    public boolean hasTag() {
        return (this.mTag != null);
    }

    /**
     * Add a tag if not already tagged
     * @return  Tag
     */
    public Tag tagIfNotTagged() {
        if (!hasTag()) {
            setTag(new Tag());
        }
        return getTag();
    }

    public static boolean isMarked(Tag status) {
        return ((status != null) && (status.isMarked()));
    }

    public static void setMarked(Tag status, boolean marked) {
        if (status != null) {
            status.setMarked(marked);
        }
    }

    public static boolean isFlag(Tag status, int flag) {
        return ((status != null) && (status.isFlagged(flag)));
    }

    public static void setFlag(Tag status, int flag) {
        if (status != null) {
            status.setFlag(flag);
        }
    }

    public static void clearFlag(Tag status, int flag) {
        if (status != null) {
            status.clearFlag(flag);
        }
    }

    /** Object was instantiated by a cache */
    protected static final int CACHE_INSTANTIATED = 0x01;
    /** Object is currently being requested */
    protected static final int REQUEST_IN_PROGRESS = 0x02;
    /** First flag for concrete objects, they should not used falgs below this */
    protected static final int FIRST_OBJECT_SPECIFIC_FLAG = 0x010000;

    public boolean isCacheInstantiated() {
        return isFlag(getTag(), CACHE_INSTANTIATED);
    }

    public void setCacheInstantiated() {
        setFlag(getTag(), CACHE_INSTANTIATED);
    }

    public void clearCacheInstantiated() {
        clearFlag(getTag(), CACHE_INSTANTIATED);
    }

    public boolean isRequestInProgress() {
        return isFlag(getTag(), REQUEST_IN_PROGRESS);
    }

    public void setRequestInProgress() {
        setFlag(getTag(), REQUEST_IN_PROGRESS);
    }

    public void clearRequestInProgress() {
        clearFlag(getTag(), REQUEST_IN_PROGRESS);
    }

    @Parcel
    public static class Tag {
        boolean mMarked;
        int mFlags;

        public Tag() {
            mMarked = false;
            mFlags = 0;
        }

        public boolean isMarked() {
            return mMarked;
        }

        public void setMarked(boolean marked) {
            this.mMarked = marked;
        }

        public boolean isFlagged(int flag) {
            return ((mFlags & flag) != 0);
        }

        public void setFlag(int flag) {
            this.mFlags |= flag;
        }

        public void clearFlag(int flag) {
            this.mFlags &= ~flag;
        }
    }
}
