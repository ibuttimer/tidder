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

package com.ianbuttimer.tidderish.data;

import com.ianbuttimer.tidderish.event.EventType;

/**
 * Class representing the result of a content provider request
 */

public abstract class ContentProviderResponse {

    @EventType
    protected int mEventType;

    public ContentProviderResponse(@EventType int eventType) {
        this.mEventType = eventType;
    }

    @EventType
    public int getEventType() {
        return mEventType;
    }

}
