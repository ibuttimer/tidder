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

package com.ianbuttimer.tidderish.event;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ianbuttimer.tidderish.data.ICallback;
import com.ianbuttimer.tidderish.data.QueryCallback;
import com.ianbuttimer.tidderish.reddit.BaseObject;
import com.ianbuttimer.tidderish.reddit.Response;

interface IStandardEventProcessor {
    @Nullable
    ICallback<Response<? extends BaseObject<?>>> getApiResponseHandler();

    @Nullable
    QueryCallback<StandardEvent> getCpStdEventHandler();

    int getLoaderId(StandardEvent event);

    FragmentActivity getActivity();
}
