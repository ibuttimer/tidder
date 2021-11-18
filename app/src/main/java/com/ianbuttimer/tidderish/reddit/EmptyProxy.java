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

package com.ianbuttimer.tidderish.reddit;


import org.parceler.Parcel;

/**
 * Class representing a object which doesn't have a proxy
 */
@Parcel
public class EmptyProxy<T extends RedditObject> extends AbstractProxy<T, EmptyProxy<T>> {

    public EmptyProxy() {
        super();
    }

    @Override
    protected EmptyCache getCache() {
        return getCacheInstance();
    }

    @Override
    protected EmptyProxy<T> getInstance() {
        return new EmptyProxy<>();
    }

    public String getRedditType() {
        return "";
    }

    protected static EmptyCache getCacheInstance() {
        return EmptyCache.getInstance();
    }

}
