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

import android.support.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class representing the result of a 'is following' query
 */

public abstract class QueryResponse<T extends AbstractDbRow, E extends Enum> extends ContentProviderResponse<E> {

    private ArrayList<T> mList;
    private Class<? extends AbstractDbRow> mClazz;

    public QueryResponse(ArrayList<T> list, Class<? extends AbstractDbRow> clazz, E eventType) {
        super(eventType);
        init(list, clazz);
    }

    public QueryResponse(T[] array, Class<? extends AbstractDbRow> clazz, E eventType) {
        super(eventType);
        ArrayList<T> list;
        if (array != null) {
            list = new ArrayList<>(Arrays.asList(array));
        } else {
            list = new ArrayList<>();
        }
        init(list, clazz);
    }

    protected void init(ArrayList<T> list, Class<? extends AbstractDbRow> clazz) {
        this.mList = list;
        this.mClazz = clazz;
    }

    public int getCount() {
        int count = 0;
        if (mList != null) {
            count = mList.size();
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    @Nullable public T[] getArray() {
        T[] array = null;
        int count = getCount();
        if (count >= 0) {
            array = (T[]) Array.newInstance(mClazz, count);
        }
        if (count > 0) {
            array = mList.toArray(array);
        }
        return array;
    }

    public ArrayList<T> getList() {
        return mList;
    }

}
