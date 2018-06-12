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
import com.ianbuttimer.tidder.data.Config;
import com.ianbuttimer.tidder.data.ConfigQueryResponse;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.ui.util.DatabaseSettings;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.Utils;

/**
 * Base class for Posts activity tab fragments
 */

public class PostsNewTabFragment extends AbstractPostsNewTabFragment {

    public PostsNewTabFragment() {
        super(R.layout.fragment_posts);
    }

    @Override
    protected boolean onClientEvent(RedditClientEvent event) {
        boolean handled = true;
        if (event.isUserValidEvent()) {
            // full version requires valid user info to access db & latest config from db
            PostOffice.postEvent(StandardEvent.newSettingsRequest(),
                                    PostListActivity.getActivityAddress(),
                                    getAddress());
        } else {
            handled = false;
        }
        return handled;
    }

    @Override
    protected boolean onStandardEvent(StandardEvent event) {
        boolean handled = super.onStandardEvent(event);
        if (!handled) {
            handled = true;

            if (event.isSettingsResult()) {
                ConfigQueryResponse response = event.getConfigQueryResponse();
                if (response != null) {
                    //
                    Config[] array = response.getArray();
                    if (Utils.arrayHasSize(array)) {
                        DatabaseSettings.getInstance().applyConfig(array[0]);
                    }
                }

                // have latest config, so request following
                requestFollowing();
            } else {
                handled = false;
            }

            PostOffice.logHandled(event, getAddress(), handled);
        }
        return handled;
    }

}
