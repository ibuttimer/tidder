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


import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Class representing a Reddit link
 */
@Parcel
public class LinkProxy extends AbstractProxy<Link, LinkProxy> {

    public LinkProxy() {
        super();
    }

    @Override
    protected LinkCache getCache() {
        return getCacheInstance();
    }

    @Override
    protected LinkProxy getInstance() {
        return new LinkProxy();
    }

    public String getRedditType() {
        return BaseObject.TYPE_LINK;
    }

    public static LinkProxy getProxy(Link object) {
        LinkProxy proxy = new LinkProxy();
        proxy.setAsProxyFor(object);
        return proxy;
    }

    public static ArrayList<LinkProxy> addToCache(ArrayList<Link> list) {
        ArrayList<LinkProxy> proxies = new ArrayList<>();
        for (Link item : list) {
            LinkProxy proxy = item.addToCache();
            if (proxy != null) {
                proxies.add(proxy);
            }
        }
        return proxies;
    }

    public static ArrayList<Link> getFromCache(ArrayList<LinkProxy> proxies) {
        ArrayList<Link> list = new ArrayList<>();
        for (LinkProxy proxy : proxies) {
            Link item = proxy.getFromCache();
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    protected static LinkCache getCacheInstance() {
        return LinkCache.getInstance();
    }

}
