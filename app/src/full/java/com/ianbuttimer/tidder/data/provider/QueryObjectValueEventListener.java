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

package com.ianbuttimer.tidder.data.provider;

import android.database.Cursor;
import android.database.MatrixCursor;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ianbuttimer.tidder.data.db.IFbCursorable;

/**
 * Event listener for a Firebase Realtime database query
 */
public class QueryObjectValueEventListener extends AbstractValueEventListener<Cursor> {

    private final IFbCursorable mFbCursorable;

    private MatrixCursor mCursor;

    /**
     * Constructor
     * @param fbCursorable  Cursorable object to hold results
     */
    public QueryObjectValueEventListener(IFbCursorable fbCursorable) {
        super();
        mFbCursorable = fbCursorable;
        mCursor = mFbCursorable.getCursor(0);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        mCursor = mFbCursorable.getCursor(1);

        IFbCursorable obj = dataSnapshot.getValue(mFbCursorable.getClass());
        if (obj != null) {
            obj.addToCursor(mCursor);
        }

        super.onDataChange(dataSnapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

        super.onCancelled(databaseError);
    }

    @Override
    public Cursor getResult() {
        return mCursor;
    }
}
