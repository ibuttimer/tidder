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

package com.ianbuttimer.tidder.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class TypedGestureDetector extends GestureDetector {

    public enum Gestures { SINGLE_TAP, DOUBLE_TAP, LONG_PRESS;

        public static final int SINGLE_TAP_BIT = SINGLE_TAP.toGestureBit();
        public static final int DOUBLE_TAP_BIT = DOUBLE_TAP.toGestureBit();
        public static final int LONG_PRESS_BIT = LONG_PRESS.toGestureBit();

        public static int toGestureBit(Gestures gesture) {
            int bit = 0;
            if (gesture != null) {
                bit = 1 << gesture.ordinal();
            }
            return bit;
        }

        public int toGestureBit() {
            return toGestureBit(this);
        }

        public static int toGestureMap(Gestures... gestures) {
            int map = 0;
            for (Gestures gesture : gestures) {
                map |= toGestureBit(gesture);
            }
            return map;
        }
    }

    private SimpleTypedOnGestureListener mListener;

    public TypedGestureDetector(Context context, SimpleTypedOnGestureListener listener) {
        super(context, listener);
        mListener = listener;
    }


    public Pair<Boolean, Gestures> onTypedTouchEvent(MotionEvent ev) {
        Boolean intercept = onTouchEvent(ev);
        Gestures gesture = null;
        if (intercept) {
            gesture = mListener.getLastGesture();
        }
        return new Pair<>(intercept, gesture);
    }

    public static class SimpleTypedOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private int mGestureMap;
        private Gestures mLastGesture;

        public SimpleTypedOnGestureListener(Gestures... gestures) {
            int map = Gestures.toGestureMap(gestures);
            if (map != 0) {
                mGestureMap = map;
            } else {
                mGestureMap = Gestures.SINGLE_TAP_BIT;
            }
            mLastGesture = null;
        }

//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            boolean consumed = ((Gestures.SINGLE_TAP_BIT & mGestureMap) != 0);
//            if (consumed) {
//                mLastGesture = Gestures.SINGLE_TAP;
//            }
//            return consumed;
//        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            boolean consumed = ((Gestures.SINGLE_TAP_BIT & mGestureMap) != 0);
            if (consumed) {
                mLastGesture = Gestures.SINGLE_TAP;
            }
            return consumed;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if ((Gestures.LONG_PRESS_BIT & mGestureMap) != 0) {
                // no op
            }
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            boolean consumed = false;
            if ((Gestures.DOUBLE_TAP_BIT & mGestureMap) != 0) {
                consumed = (e.getAction() == MotionEvent.ACTION_UP);
                if (consumed) {
                    mLastGesture = Gestures.DOUBLE_TAP;
                }
            }
            return consumed;
        }

        @Nullable
        public Gestures getLastGesture() {
            Gestures gesture = mLastGesture;
            mLastGesture = null;
            return gesture;
        }
    }

}
