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

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ianbuttimer.tidder.data.db.AbstractFbRow;

import java.util.ArrayList;


public class UpdateValueEventListener extends AbstractValueEventListener<Integer> {

    private Class<? extends AbstractFbRow> mClass;
    private ContentValues mValues;
    private int mCount;

    public UpdateValueEventListener() {
        super();
        mCount = 0;
    }

    public UpdateValueEventListener(Class<? extends AbstractFbRow> objClass, ContentValues values) {
        this();
        setClass(objClass);
        setValues(values);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mCount = 0;

        ArrayList<DataSnapshot> filtered = mFbQuery.filterChildren(dataSnapshot);
        for (DataSnapshot objSnapshot : filtered) {
            if (objSnapshot != null) {
                Object dbObj = objSnapshot.getValue(mClass);
                mClass.cast(dbObj).update(mValues);
                ++mCount;
            }
        }

        super.onDataChange(dataSnapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

        super.onCancelled(databaseError);
    }

    @Override
    public Integer getResult() {
        return mCount;
    }


    public void setClass(Class<? extends AbstractFbRow> objClass) {
        this.mClass = objClass;
    }

    public void setValues(ContentValues values) {
        this.mValues = values;
    }
}
