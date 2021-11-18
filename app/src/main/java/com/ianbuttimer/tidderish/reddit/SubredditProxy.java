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

package com.ianbuttimer.tidderish.reddit;


import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Class representing a Reddit subreddit
 */
@Parcel
public class SubredditProxy extends AbstractProxy<Subreddit, SubredditProxy> {

    public SubredditProxy() {
        super();
    }

    @Override
    protected SubredditCache getCache() {
        return getCacheInstance();
    }

    @Override
    protected SubredditProxy getInstance() {
        return new SubredditProxy();
    }

    public String getRedditType() {
        return BaseObject.TYPE_SUBREDDIT;
    }

    public static SubredditProxy getProxy(Subreddit object) {
        SubredditProxy proxy = new SubredditProxy();
        proxy.setAsProxyFor(object);
        return proxy;
    }

    public static ArrayList<SubredditProxy> addToCache(ArrayList<Subreddit> list) {
        ArrayList<SubredditProxy> proxies = new ArrayList<>();
        for (Subreddit item : list) {
            SubredditProxy proxy = item.addToCache();
            if (proxy != null) {
                proxies.add(proxy);
            }
        }
        return proxies;
    }

    public static ArrayList<Subreddit> getFromCache(ArrayList<SubredditProxy> proxies) {
        ArrayList<Subreddit> list = new ArrayList<>();
        for (SubredditProxy proxy : proxies) {
            Subreddit item = proxy.getFromCache();
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    protected static SubredditCache getCacheInstance() {
        return SubredditCache.getInstance();
    }

}
