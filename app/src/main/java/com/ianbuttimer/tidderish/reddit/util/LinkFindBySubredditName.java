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

package com.ianbuttimer.tidderish.reddit.util;

import androidx.annotation.Nullable;

import com.ianbuttimer.tidderish.reddit.Link;

/**
 * ITester implementation to find Link objects based on subreddit name
 */
public class LinkFindBySubredditName extends AbstractFindByName<Link> {

    public LinkFindBySubredditName(@Nullable String subredditName) {
        super(subredditName);
    }

    @Override
    protected String getName(Link obj) {
        return obj.getSubreddit();
    }
}
