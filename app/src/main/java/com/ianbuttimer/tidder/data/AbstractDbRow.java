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

package com.ianbuttimer.tidder.data;

import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Objects;

/**
 * Base Class representing a database row
 */

public abstract class AbstractDbRow {

    protected int mId;

    public AbstractDbRow() {
        init();
    }


    public AbstractDbRow(Cursor cursor) {
        init();

        mId = getInt(cursor, BaseColumns._ID, -1);
    }

    protected void init() {
        mId = -1;
    }

    /**
     * Get an integer value from the current row
     * @param cursor    Cursor to read
     * @param colName   Name of column to read
     * @param dfltValue Default value
     * @return  Column value
     */
    protected int getInt(Cursor cursor, String colName, int dfltValue) {
        int result = dfltValue;
        int index = cursor.getColumnIndex(colName);
        if (index >= 0) {
            result = cursor.getInt(index);
        }
        return result;
    }

    /**
     * Get a string value from the current row
     * @param cursor    Cursor to read
     * @param colName   Name of column to read
     * @param dfltValue Default value
     * @return  Column value
     */
    protected String getString(Cursor cursor, String colName, String dfltValue) {
        String result = dfltValue;
        int index = cursor.getColumnIndex(colName);
        if (index >= 0) {
            result = cursor.getString(index);
        }
        return result;
    }

    /**
     * Get a boolean value from the current row
     * @param cursor    Cursor to read
     * @param colName   Name of column to read
     * @param dfltValue Default value
     * @return  Column value
     */
    protected boolean getBoolean(Cursor cursor, String colName, boolean dfltValue) {
        return Boolean.parseBoolean(getString(cursor, colName, Boolean.toString(dfltValue)));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDbRow that = (AbstractDbRow) o;
        return mId == that.mId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(mId);
    }
}
