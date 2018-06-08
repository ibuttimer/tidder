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


/**
 * Basic object 'equals' ITester class
 */

public class EqualsITester<T> implements ITester<T> {

    private T mToFind;

    public EqualsITester(T toFind) {
        this.mToFind = toFind;
    }

    @Override
    public boolean test(T obj) {
        boolean match;
        if (mToFind != null) {
            match = mToFind.equals(obj);
        } else {
            match = (obj == null);
        }
        return match;
    }

    public T getToFind() {
        return mToFind;
    }

    public void setToFind(T toFind) {
        this.mToFind = toFind;
    }
}
