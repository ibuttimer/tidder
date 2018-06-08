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

package com.ianbuttimer.tidder.data.db;

import android.content.ContentValues;
import android.database.MatrixCursor;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

@IgnoreExtraProperties
public abstract class AbstractFbRow implements IFbCursorable {

    @Exclude
    public String id;

    public AbstractFbRow() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        id = "";
    }

    @Exclude
    public abstract Map<String, Object> toMap();

    @Exclude
    public ContentValues getContentValues() {
        Map<String, Object> map = toMap();
        ContentValues values = new ContentValues();
        for (String key : map.keySet()) {
            Object val = map.get(key);
            if (val instanceof String) {
                values.put(key, (String)val);
            } else if (val instanceof Integer) {
                values.put(key, (Integer) val);
            } else if (val instanceof Boolean) {
                values.put(key, (Boolean) val);
            } else if (val instanceof Long) {
                values.put(key, (Long) val);
            } else if (val instanceof Float) {
                values.put(key, (Float) val);
            } else if (val instanceof Double) {
                values.put(key, (Double) val);
            }
        }

        return values;
    }

    @Override
    @Exclude
    public MatrixCursor getCursor(int initialCapacity) {
        return new MatrixCursor(getCursorColumns(), initialCapacity);
    }

    @Exclude
    protected abstract String[] getCursorColumns();

    @Override
    @Exclude
    public void addToCursor(MatrixCursor cursor) {
        cursor.addRow(getCursorColumnValues());
    }

    @Exclude
    protected abstract Object[] getCursorColumnValues();

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public abstract void update(ContentValues contentValues);

}
