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
import android.support.annotation.Nullable;


/**
 * Class to process an 'is pinned' db query response
 */
@SuppressWarnings("unused")
public class PinnedCursorProcessor extends AbstractCursorProcessor<Pinned> {

    public PinnedCursorProcessor(Cursor cursor) {
        super(cursor, Pinned.class);
    }

    /**
     * Read an object from the current position of the cursor
     * @param cursor        Cursor to read from
     * @return  new object
     */
    @Override
    @Nullable protected Pinned processOne(Cursor cursor) {
        Pinned result = null;
        if (isValidPosition(cursor)) {
            result = new Pinned(cursor);
        }
        return result;
    }

    @Nullable public static Pinned[] readArray(Cursor cursor) {
        Pinned[] array = new PinnedCursorProcessor(cursor).processArray();
        if (cursor != null) {
            cursor.close();
        }
        return array;
    }

}
