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


/**
 * Class to process a 'config' db query response
 */
@SuppressWarnings("unused")
public class ConfigCursorProcessor extends AbstractCursorProcessor<Config> {

    public ConfigCursorProcessor(Cursor cursor) {
        super(cursor, Config.class);
    }

    /**
     * Read an object from the current position of the cursor
     * @param cursor        Cursor to read from
     * @return  new object
     */
    @Override
    @Nullable protected Config processOne(Cursor cursor) {
        Config result = null;
        if (isValidPosition(cursor)) {
            result = new Config(cursor);
        }
        return result;
    }

    @Nullable public static Config[] readArray(Cursor cursor) {
        Config[] array = new ConfigCursorProcessor(cursor).processArray();
        if (cursor != null) {
            cursor.close();
        }
        return array;
    }

}
