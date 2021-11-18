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
import androidx.annotation.Nullable;

import java.lang.reflect.Array;


/**
 * Class to process cursors
 */
@SuppressWarnings("unused")
public abstract class AbstractCursorProcessor<T extends AbstractDbRow> {

    private Cursor mCursor;
    private final Class<? extends AbstractDbRow> mClazz;

    public AbstractCursorProcessor(Cursor cursor, Class<? extends AbstractDbRow> clazz) {
        this.mCursor = cursor;
        this.mClazz = clazz;
    }

    /**
     * Read an object from a mCursor
     * @param cursor    Cursor to read from
     * @param position  Zero-based mCursor row to read
     * @return  new object or <code>null</code>
     */
    @Nullable public T processSingle(Cursor cursor, int position) {
        T object = null;
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                object = processOne(cursor);
            }
        }
        return object;
    }

    /**
     * Read an object from a mCursor
     * @param cursor    Cursor to read from
     * @return  new object or <code>null</code>
     */
    @Nullable public T processSingle(Cursor cursor) {
        return processSingle(cursor, 0);
    }

    /**
     * Read an object
     * @param position  Zero-based mCursor row to read
     * @return  new object or <code>null</code>
     */
    @Nullable public T processSingle(int position) {
        return processSingle(mCursor, position);
    }

    /**
     * Read an object
     * @return  new object or <code>null</code>
     */
    @Nullable public T processSingle() {
        return processSingle(mCursor);
    }

    /**
     * Read an object array from a mCursor
     * @param cursor    Cursor to read from
     * @return  new object array or <code>null</code>
     */
    @SuppressWarnings("unchecked")
    @Nullable public T[] processArray(Cursor cursor) {
        T[] array = null;
        if (cursor != null) {
            int length = cursor.getCount();
            array = (T[]) Array.newInstance(mClazz, length);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < length; ++i) {
                    array[i] = processOne(cursor);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return array;
    }

    /**
     * Read an object array
     * @return  new object array or <code>null</code>
     */
    @Nullable public T[] processArray() {
        return processArray(mCursor);
    }

    /**
     * Read an object from the current position of the mCursor
     * @param cursor        Cursor to read from
     * @return  new object
     */
    @Nullable protected abstract T processOne(Cursor cursor);

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
    }

    boolean isValidPosition(Cursor cursor) {
        boolean valid = false;
        if (cursor != null) {
            valid = (!cursor.isClosed() && !cursor.isBeforeFirst() && !cursor.isAfterLast());
        }
        return valid;
    }
}
