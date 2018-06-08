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

import static com.ianbuttimer.tidder.data.db.PinnedColumns.ID;
import static com.ianbuttimer.tidder.data.db.PinnedColumns.FULLNAME;
import static com.ianbuttimer.tidder.data.db.PinnedColumns.UUID;

/**
 * Builder class for Pinned database table values
 */

public class PinnedBuilder {

    private ContentValues mContentValues;

    /**
     * Constructor
     */
    public PinnedBuilder() {
        mContentValues = new ContentValues();
    }

    public PinnedBuilder id(String id) {
        mContentValues.put(ID, id);
        return this;
    }

    public PinnedBuilder uuid(String uuid) {
        mContentValues.put(UUID, uuid);
        return this;
    }

    public PinnedBuilder permalink(String permalink) {
        mContentValues.put(FULLNAME, permalink);
        return this;
    }

    /**
     * Clear the builder contents
     * @return  Builder to facilitate chaining
     */
    public PinnedBuilder clear() {
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
    public static PinnedBuilder builder() {
        return new PinnedBuilder();
    }

}
