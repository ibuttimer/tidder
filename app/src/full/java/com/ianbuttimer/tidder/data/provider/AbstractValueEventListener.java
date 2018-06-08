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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ianbuttimer.tidder.data.ITester;
import com.ianbuttimer.tidder.utils.ArrayTester;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import timber.log.Timber;

public abstract class AbstractValueEventListener<R> implements ValueEventListener {

    private CountDownLatch mLatch;

    protected Query mQuery;
    protected FbQuery mFbQuery;

    public AbstractValueEventListener() {
        this(1);
    }

    public AbstractValueEventListener(int count) {
        this.mLatch = new CountDownLatch(count);
    }

    public void addToQueryAsSingleValueEvent(Query query) {
        if (query != null) {
            mQuery = query;

            query.addListenerForSingleValueEvent(this);
            try {
                mLatch.await();
            } catch (InterruptedException e) {
                Timber.e(e);
            }
        }
    }

    public void addToQueryAsSingleValueEvent(FbQuery query) {
        if (query != null) {
            mFbQuery = query;

            addToQueryAsSingleValueEvent(query.getQuery());
        }
    }

    public void addToQueryAsValueEvent(Query query) {
        if (query != null) {
            mQuery = query;

            query.addValueEventListener(this);
            try {
                mLatch.await();
            } catch (InterruptedException e) {
                Timber.e(e);
            }
        }
    }

    public void addToQueryAsValueEvent(FbQuery query) {
        if (query != null) {
            mFbQuery = query;

            addToQueryAsValueEvent(query.getQuery());
        }
    }

    public void removeFromQuery() {
        if (mQuery != null) {
            mQuery.removeEventListener(this);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mLatch.countDown();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        mLatch.countDown();
    }

    public abstract R getResult();
}
