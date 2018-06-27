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
 * No-op class representing the result of a 'config' query which isn't required for free variant
 */

public class ConfigQueryResponse extends QueryResponse<Config> {

    public ConfigQueryResponse(ArrayList<Config> list) {
        super(list, Config.class, EventType.SETTINGS_RESULT);
    }

    public ConfigQueryResponse(Config[] array) {
        super(array, Config.class, EventType.SETTINGS_RESULT);
    }

    public ConfigQueryResponse(Cursor cursor) {
        this((Config[])null);
    }
}
