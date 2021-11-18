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

/**
 * A Least-Recently-Used Cache for Reddit subreddits
 */
public class SubredditCache extends RedditCache<Subreddit> {

    public static final int CACHE_SIZE = 100;

    private static SubredditCache mInstance;

    private SubredditCache() {
        super(CACHE_SIZE);
    }


    @Override
    protected Subreddit getObject() {
        return new Subreddit();
    }

    public static SubredditCache getInstance() {
        if (mInstance == null) {
            mInstance = new SubredditCache();
        }
        return mInstance;
    }
}
