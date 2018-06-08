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

package com.ianbuttimer.tidder.data.provider;

import android.content.ContentValues;

import static com.ianbuttimer.tidder.data.db.FollowColumns.ID;
import static com.ianbuttimer.tidder.data.db.FollowColumns.SUBREDDIT;
import static com.ianbuttimer.tidder.data.db.FollowColumns.UUID;
import static com.ianbuttimer.tidder.data.db.FollowColumns.KEY_COLOUR;
import static com.ianbuttimer.tidder.data.db.FollowColumns.ICON_IMG;

/**
 * Builder class for Follow database table values
 */

public class FollowBuilder {

    private ContentValues mContentValues;

    /**
     * Constructor
     */
    public FollowBuilder() {
        mContentValues = new ContentValues();
    }

    public FollowBuilder id(String id) {
        mContentValues.put(ID, id);
        return this;
    }

    public FollowBuilder uuid(String uuid) {
        mContentValues.put(UUID, uuid);
        return this;
    }

    public FollowBuilder subreddit(String subreddit) {
        mContentValues.put(SUBREDDIT, subreddit);
        return this;
    }

    public FollowBuilder keyColour(int keyColour) {
        mContentValues.put(KEY_COLOUR, keyColour);
        return this;
    }

    public FollowBuilder iconImg(String iconImg) {
        mContentValues.put(ICON_IMG, iconImg);
        return this;
    }

    /**
     * Clear the builder contents
     * @return  Builder to facilitate chaining
     */
    public FollowBuilder clear() {
        mContentValues.clear();
        return this;
    }

    /**
     * Build a ContentValue object
     * @return ContentValue object
     */
    public ContentValues build() {
        return new ContentValues(mContentValues);
    }

    /**
     * Get a builder instance
     * @return  New builder instance
     */
    public static FollowBuilder builder() {
        return new FollowBuilder();
    }

}
