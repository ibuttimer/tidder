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

import java.util.ArrayList;


public class QueryValueEventListener extends AbstractValueEventListener<Cursor> {

    private final IFbCursorable mFbCursorable;

    private MatrixCursor mCursor;

    public QueryValueEventListener(IFbCursorable fbCursorable) {
        super();
        mFbCursorable = fbCursorable;
        mCursor = mFbCursorable.getCursor(0);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        mCursor = mFbCursorable.getCursor(
                Long.valueOf(dataSnapshot.getChildrenCount()).intValue());

        ArrayList<DataSnapshot> filtered = mFbQuery.filterChildren(dataSnapshot);
        for (DataSnapshot objSnapshot : filtered) {
            if (objSnapshot != null) {
                IFbCursorable obj = objSnapshot.getValue(mFbCursorable.getClass());
                if (obj != null) {
                    obj.setId(objSnapshot.getKey());
                    obj.addToCursor(mCursor);
                }
            }
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
