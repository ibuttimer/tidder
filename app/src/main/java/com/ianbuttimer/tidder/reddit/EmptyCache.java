/*
 * Copyright (C) 2020  Ian Buttimer
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

package com.ianbuttimer.tidder.reddit;

/**
 * A Least-Recently-Used Cache placeholder for items that can't be cached
 */
public class EmptyCache<T extends RedditObject> extends RedditCache<T> {

    public static final int CACHE_SIZE = 0;

    private static EmptyCache mInstance;

    private EmptyCache() {
        super(CACHE_SIZE);
    }


    @Override
    protected T getObject() {
        return null;
    }

    public static EmptyCache getInstance() {
        if (mInstance == null) {
            mInstance = new EmptyCache();
        }
        return mInstance;
    }
}
