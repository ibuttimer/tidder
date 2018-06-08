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
 * Class to process an 'is following' db query response
 */
@SuppressWarnings("unused")
public class FollowCursorProcessor extends AbstractCursorProcessor<Follow> {

    public FollowCursorProcessor(Cursor cursor) {
        super(cursor, Follow.class);
    }

    /**
     * Read an object from the current position of the cursor
     * @param cursor        Cursor to read from
     * @return  new object
     */
    @Override
    @Nullable protected Follow processOne(Cursor cursor) {
        Follow result = null;
        if (isValidPosition(cursor)) {
            result = new Follow(cursor);
        }
        return result;
    }

    @Nullable public static Follow[] readArray(Cursor cursor) {
        Follow[] array = new FollowCursorProcessor(cursor).processArray();
        if (cursor != null) {
            cursor.close();
        }
        return array;
    }

}
