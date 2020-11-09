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

import androidx.annotation.Nullable;

import com.ianbuttimer.tidder.reddit.Subreddit;

/**
 * ITester implementation to find Subreddit objects based on display name
 */
public class SubredditFindByName extends AbstractFindByName<Subreddit> {

    public SubredditFindByName(@Nullable String subredditName) {
        super(subredditName);
    }

    @Override
    protected String getName(Subreddit obj) {
        return obj.getDisplayName();
    }
}
