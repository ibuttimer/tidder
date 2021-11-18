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

package com.ianbuttimer.tidderish.data;

import android.database.Cursor;

import com.ianbuttimer.tidderish.data.db.PinnedColumns;

import org.parceler.Parcel;

import java.util.Objects;

/**
 * Class representing the result of a database pinned table query
 */
@Parcel
public class Pinned extends AbstractDbRow {

    protected String mUuid;
    protected String mFullname;

    /**
     * Default constructor
     */
    public Pinned() {
        super();
    }

    /**
     * Constructor
     * @param cursor    Cursor from whose current row to create object
     */
    public Pinned(Cursor cursor) {
        super(cursor);

        mUuid = getString(cursor, PinnedColumns.UUID, "");
        mFullname = getString(cursor, PinnedColumns.FULLNAME, "");
    }

    @Override
    protected void init() {
        super.init();
        mUuid = "";
        mFullname = "";
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }

    public String getFullname() {
        return mFullname;
    }

    public void setFullname(String fullname) {
        this.mFullname = fullname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pinned pinned = (Pinned) o;
        return Objects.equals(mUuid, pinned.mUuid) &&
                Objects.equals(mFullname, pinned.mFullname);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), mUuid, mFullname);
    }
}
