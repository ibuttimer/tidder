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

package com.ianbuttimer.tidder.ui.util;

import android.view.InputDevice;
import android.view.KeyEvent;

/**
 * Extended KeyEvent class
 */
public class DelayedKeyEvent extends KeyEvent {

    private int mSource;

    public DelayedKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags, int source) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags, source);
        mSource = source;
    }

    public DelayedKeyEvent(KeyEvent event) {
        super(event);
        mSource = InputDevice.SOURCE_UNKNOWN;
    }

    public void setEventSource(int source) {
        mSource = getSource();
        setSource(source);
    }

    public int getEventSource() {
        return mSource;
    }
}
