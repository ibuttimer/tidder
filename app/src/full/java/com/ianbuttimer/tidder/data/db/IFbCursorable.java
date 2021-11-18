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

package com.ianbuttimer.tidderish.data.db;

import android.database.MatrixCursor;

/**
 * Interface for object which may be transformed to Cursors
 */
public interface IFbCursorable {

    /**
     * Get a new Cursor with the specified initial capacity
     * @param initialCapacity   Initial capacity of cursor
     * @return  Cursor
     */
    MatrixCursor getCursor(int initialCapacity);

    /**
     * Add a new row to the specified cursor
     * @param cursor    Cursor to add to
     */
    void addToCursor(MatrixCursor cursor);

    /**
     * Get the object id
     * @return  Id
     */
    String getId();

    /**
     * Set the object id
     * @param id    Id
     */
    void setId(String id);

}
