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

import com.ianbuttimer.tidderish.data.IEmpty;
import com.ianbuttimer.tidderish.event.EventType;

/**
 * Base class for reddit responses
 * @param <T> class of reddit object
 */

public abstract class Response<T> extends BaseObject<T> implements IEmpty {

    protected boolean mEmpty;
    @EventType protected int mEventType;

    public Response() {
        setEmpty(true);
    }

    public Response(@EventType int eventType) {
        setEmpty(true);
        mEventType = eventType;
    }

    @Override
    public boolean isEmpty() {
        return mEmpty;
    }

    @Override
    public void setEmpty(boolean empty) {
        mEmpty = empty;
    }

    @EventType
    public int getEventType() {
        return mEventType;
    }

}
