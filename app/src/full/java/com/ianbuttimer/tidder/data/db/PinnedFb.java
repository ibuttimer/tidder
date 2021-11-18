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

import android.content.ContentValues;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a Firebase Realtime database Pinned table row
 */
@IgnoreExtraProperties
public class PinnedFb extends AbstractFbRow implements IFbCursorable {

    @Exclude
    public static final String[] CURSOR_COLUMNS = new String[] {
            PinnedColumns.FULLNAME
    };

    @Exclude
    private static final PinnedFb sFactory = new PinnedFb();

    public String fullname;

    public PinnedFb() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this("");
    }

    public PinnedFb(String fullname) {
        super();
        this.fullname = fullname;
    }

    @Override
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(PinnedColumns.FULLNAME, fullname);

        return result;
    }

    @Exclude
    public static PinnedFb generate(ContentValues contentValues) {
        return update(new PinnedFb(), contentValues);
    }

    @Exclude
    public static PinnedFb update(PinnedFb obj, ContentValues contentValues) {
        if (contentValues.containsKey(PinnedColumns.FULLNAME)) {
            obj.fullname = contentValues.getAsString(PinnedColumns.FULLNAME);
        }
        return obj;
    }

    @Override
    @Exclude
    public void update(ContentValues contentValues) {
        update(this, contentValues);
    }

    @Exclude
    public static IFbCursorable getFactory() {
        return sFactory;
    }

    @Override
    @Exclude
    protected String[] getCursorColumns() {
        return CURSOR_COLUMNS;
    }

    @Override
    @Exclude
    protected Object[] getCursorColumnValues() {
        return new String[] {
                fullname
        };
    }

}
