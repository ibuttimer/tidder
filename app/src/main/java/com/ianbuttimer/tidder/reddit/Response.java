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

import com.ianbuttimer.tidder.data.IEmpty;

/**
 * Base class for reddit responses
 */

public abstract class Response<E extends Enum> extends BaseObject implements IEmpty {

    protected boolean mEmpty;
    protected E mEventType;

    public Response() {
        setEmpty(true);
    }

    public Response(E eventType) {
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

    public E getEventType() {
        return mEventType;
    }

}
