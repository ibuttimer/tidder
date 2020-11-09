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

import android.text.TextUtils;
import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.ianbuttimer.tidder.data.ITester;
import com.ianbuttimer.tidder.utils.ArrayTester;
import com.ianbuttimer.tidder.utils.Quartet;
import com.ianbuttimer.tidder.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import timber.log.Timber;

import static com.ianbuttimer.tidder.data.db.DbUtils.DB_DELETE_ALL;
import static com.ianbuttimer.tidder.data.provider.BaseProvider.isColumnEqSelection;
import static com.ianbuttimer.tidder.data.provider.BaseProvider.isColumnInSelection;

/**
 * A Firebase query class handling basic 'equals to' and 'in' queries.
 */
public class FbQuery {

    private enum Type { NONE, EQUAL, IN, ALL };

    private DatabaseReference mReference;
    /**
     * List of where conditions;<br>
     * <ul>
     *     <li>Condition type</li>
     *     <li>Condition field</li>
     *     <li>Condition arguments</li>
     *     <li>Number of arguments required by condition</li>
     * </ul>
     */
    private final ArrayList<Quartet<Type, String, String[], Integer>> mWhere;

    private boolean mMade;
    private Query mQuery;

    public FbQuery(DatabaseReference reference) {
        this(reference, null, null);
    }

    public FbQuery(DatabaseReference reference, String selection, String[] selectionArgs) {
        this.mReference = reference;
        this.mQuery = null;
        this.mMade = false;
        this.mWhere = new ArrayList<>();
        where(selection, selectionArgs);
    }

    public FbQuery where(String selection, String[] selectionArgs) {
        if (!TextUtils.isEmpty(selection)) {
            Type type = Type.NONE;
            String field = null;
            int argCount = 0;

            if (!TextUtils.isEmpty(selection)) {
                if (DB_DELETE_ALL.equals(selection)) {
                    type = Type.ALL;
                }
                if (Type.NONE.equals(type)) {
                    field = isColumnEqSelection(selection);
                    if (field != null) {
                        if (Utils.arrayHasSize(selectionArgs)) {
                            argCount = 1;
                            type = Type.EQUAL;
                        } else {
                            throw new IllegalArgumentException("No arguments supplied for 'is equal' select");
                        }
                    }
                }
                if (Type.NONE.equals(type)) {
                    Pair<String, Integer> pair = isColumnInSelection(selection);
                    if (pair != null) {
                        field = pair.first;
                        if (Utils.arrayHasSize(selectionArgs)) {
                            if (selectionArgs.length < pair.second) {
                                throw new IllegalArgumentException("Incorrect number of arguments supplied for 'is in' select");
                            } else {
                                argCount = pair.second;
                                type = Type.IN;
                            }
                        } else {
                            throw new IllegalArgumentException("No arguments supplied for 'is in' select");
                        }
                    }
                }
            }
            mWhere.add(new Quartet<>(type, field, selectionArgs, argCount));
        }
        return this;
    }


    public void makeQuery() {
        Query query = null;

        // can only have 1 order by in firebase, so generate query from 1st 'where'
        if (mWhere.size() >= 1) {
            Quartet<Type, String, String[], Integer> where = mWhere.get(0);
            switch (where.first) {
                case IN:
                case EQUAL:
                    query = mReference.orderByChild(where.second);
                    if (where.first == Type.IN) {
                        break;  // for an 'in' filtering is done in-app
                    }
                    // fall through
                    query = query.equalTo(where.third[0]);
                    break;
                case ALL:
                    // use default query, i.e. all
                    break;
                default:
                    Timber.i("Ignored condition; %s, %s, %s, %d",
                            where.first, where.second, Arrays.toString(where.third), where.fourth);
                    break;
            }
            mQuery = query;
        }
        if (query == null) {
            // default, get all
            query = mReference.orderByKey();
        }
        mMade = true;
        mQuery = query;
    }

    public FbQuery clear() {
        this.mReference = null;
        this.mQuery = null;
        this.mWhere.clear();
        this.mMade = false;
        return this;
    }

    public DatabaseReference getReference() {
        return mReference;
    }

    public FbQuery setReference(DatabaseReference reference) {
        this.mReference = reference;
        this.mMade = false;
        return this;
    }

    public Query getQuery() {
        if (!mMade) {
            makeQuery();
        }
        return mQuery;
    }

    public boolean hasSelection() {
        return (mWhere.size() > 0);
    }

    public ArrayList<DataSnapshot> filterChildren(DataSnapshot dataSnapshot) {
        ArrayList<DataSnapshot> filtered = new ArrayList<>();
        for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
            boolean add = true;
            if (hasSelection()) {
                add = isInSelectionArgs(objSnapshot);
            }
            if (add) {
                filtered.add(objSnapshot);
            }
        }
        return filtered;
    }

    protected boolean isInSelectionArgs(DataSnapshot data) {
        boolean inSelection = true; // default, is in selection for basic query

        for (int i = 0; (i < mWhere.size()) && inSelection; ++i) {
            Quartet<Type, String, String[], Integer> where = mWhere.get(i);

            switch (where.first) {
                case EQUAL:
                    if (i > 0) {
                        inSelection = where.second.equals(where.third[0]);
                    }   // else 1st equal condition handled by original firebase query
                    break;
                case IN:
                    inSelection = isInSelectionArgs(data, where.second, where.third);
                    break;
                case ALL:
                    // all included
                    break;
                default:
                    // ignore unknown condition
                    break;
            }
        }
        return inSelection;
    }

    protected boolean isInSelectionArgs(DataSnapshot data, String selection, String[] selectionArgs) {
        boolean inSelection = false;
        ArrayTester<String> arrayTester = new ArrayTester<>(selectionArgs);
        Object obj = data.getValue();

        if (obj instanceof Map) {
            Map<?, ?> map = ((Map<?, ?>) obj);
            Object[] keys = map.keySet().toArray();
            for (Object key : keys) {
                if (selection.equals(key)) {
                    final Object value = map.get(key);
                    if ((value instanceof String)) {
                        int index = arrayTester.findItemIndex(new ITester<String>() {
                            @Override
                            public boolean test(String obj) {
                                return value.equals(obj);
                            }
                        });
                        inSelection = (index >= 0);
                    }
                    break;
                }
            }
        }
        return inSelection;
    }

}
