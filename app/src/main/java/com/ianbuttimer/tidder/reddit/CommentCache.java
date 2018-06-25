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

/**
 * A Least-Recently-Used Cache for Reddit comments
 */
public class CommentCache extends RedditCache<Comment> {

    public static final int CACHE_SIZE = 150;

    private static CommentCache mInstance;

    private CommentCache() {
        super(CACHE_SIZE);
    }


    @Override
    protected Comment getObject() {
        return new Comment();
    }

    public static CommentCache getInstance() {
        if (mInstance == null) {
            mInstance = new CommentCache();
        }
        return mInstance;
    }
}
