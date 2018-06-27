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

import com.ianbuttimer.tidder.event.EventType;

import java.util.ArrayList;

/**
 * Class representing the result of a 'pinned' query
 */

public class PinnedQueryResponse extends QueryResponse<Pinned> {

    public PinnedQueryResponse(ArrayList<Pinned> list) {
        super(list, Pinned.class, EventType.PINNED_LIST_RESULT);
    }

    public PinnedQueryResponse(Pinned[] array) {
        super(array, Pinned.class, EventType.PINNED_LIST_RESULT);
    }

    public PinnedQueryResponse(Cursor cursor) {
        this(PinnedCursorProcessor.readArray(cursor));
    }
}
