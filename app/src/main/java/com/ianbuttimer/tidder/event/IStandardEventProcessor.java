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

package com.ianbuttimer.tidder.event;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.ianbuttimer.tidder.data.ICallback;
import com.ianbuttimer.tidder.data.QueryCallback;
import com.ianbuttimer.tidder.reddit.Response;

interface IStandardEventProcessor {
    @Nullable
    ICallback<Response> getApiResponseHandler();

    @Nullable
    QueryCallback<StandardEvent> getCpStdEventHandler();

    int getLoaderId(StandardEvent event);

    FragmentActivity getActivity();
}
