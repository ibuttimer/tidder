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

package com.ianbuttimer.tidder.ui;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.event.RedditClientEvent;


/**
 * Base class for Posts activity tab fragments
 */

public class PostsPinnedTabFragment extends AbstractPostsPinnedTabFragment {

    public PostsPinnedTabFragment() {
        super(R.layout.fragment_posts);
    }

    @Override
    public void onStart() {
        super.onStart();

        // local db variants don't need valid user info to access db
        requestPinned();
    }

    @Override
    protected boolean onClientEvent(RedditClientEvent event) {
        // no op
        return false;
    }

}
