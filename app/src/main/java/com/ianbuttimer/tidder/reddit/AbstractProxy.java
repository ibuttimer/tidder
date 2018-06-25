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

package com.ianbuttimer.tidder.reddit;


import android.support.annotation.Nullable;


/**
 * Class representing a Reddit object
 */
public abstract class AbstractProxy<T extends RedditObject, P extends AbstractProxy>
                            implements RedditCache.ICacheable {

    protected String mName;                 // fullname of object, e.g. "t5_2qh3l"
    protected String mId;                   // unique id, e.g. "2qh3l"


    public AbstractProxy() {
        // no op
    }

    protected abstract P getInstance();

    protected abstract RedditCache<T> getCache();

    protected abstract String getRedditType();

    @Nullable
    public T getFromCache() {
        return getCache().get(getCacheKey());
    }

    @Override
    public String getCacheKey() {
        // caches use fullname as key, needs to be same as implementation in RedditObject
        return getName();
    }

    public void setAsProxyFor(T object) {
        setId(object.getId());
        setName(object.getName());
    }

    @Nullable
    public P addToCache(T object) {
        P proxy = getInstance();
        if (proxy != null) {
            proxy.setAsProxyFor(object);
            getCache().put(getCacheKey(), object);
        }
        return proxy;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }
}
