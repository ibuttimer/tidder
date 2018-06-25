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
 * Class representing a Reddit comment
 */
@Parcel
public class CommentProxy extends AbstractProxy<Comment, CommentProxy> {

    public CommentProxy() {
        super();
    }

    @Override
    protected CommentCache getCache() {
        return getCacheInstance();
    }

    @Override
    protected CommentProxy getInstance() {
        return new CommentProxy();
    }

    public String getRedditType() {
        return BaseObject.TYPE_COMMENT;
    }

    public static CommentProxy getProxy(Comment object) {
        CommentProxy proxy = new CommentProxy();
        proxy.setAsProxyFor(object);
        return proxy;
    }

    public static ArrayList<CommentProxy> addToCache(ArrayList<Comment> list) {
        ArrayList<CommentProxy> proxies = new ArrayList<>();
        for (Comment item : list) {
            CommentProxy proxy = item.addToCache();
            if (proxy != null) {
                proxies.add(proxy);
            }
        }
        return proxies;
    }

    public static ArrayList<Comment> getFromCache(ArrayList<CommentProxy> proxies) {
        ArrayList<Comment> list = new ArrayList<>();
        for (CommentProxy proxy : proxies) {
            Comment item = proxy.getFromCache();
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    protected static CommentCache getCacheInstance() {
        return CommentCache.getInstance();
    }

}
