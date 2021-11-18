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

import org.parceler.Parcel;

/**
 * No-op class representing the result of a database config table query which isn't required for variant's using local db
 */
@Parcel
public class Config extends AbstractDbRow implements Cloneable {

    /**
     * Default constructor
     */
    public Config() {
        super();
    }

    /**
     * Constructor
     * @param cursor    Cursor from whose current row to create object
     */
    public Config(Cursor cursor) {
        super(cursor);
    }

}
