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

package com.ianbuttimer.tidder.reddit.util;

import android.support.annotation.Nullable;

import com.ianbuttimer.tidder.data.ITester;

/**
 * ITester base implementation to find objects based on name
 */
public abstract class AbstractFindByName<T> implements ITester<T> {
    @Nullable
    protected String mName;

    public AbstractFindByName(@Nullable String name) {
        setName(name);
    }

    public void setName(@Nullable String name) {
        this.mName = name;
    }

    @Override
    public boolean test(T obj) {
        boolean match = false;
        if (mName != null) {
            match = mName.equals(getName(obj));
        }
        return match;
    }

    protected abstract String getName(T obj);
}
