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

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import timber.log.Timber;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_MULTIPLE;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_UNKNOWN;
import static com.ianbuttimer.tidder.data.adapter.AdapterSelectController.logKeyEvent;

/**
 * Key event interceptor
 */
public class KeyInterceptor {

    private static final int sInterceptKeyDelay;      // length of time to wait checking for key interception
    private static final int sInterceptKeyExtendDelay;   // length of time to extend wait checking for key interception

    static {
        Context context = TidderApplication.getWeakApplicationContext().get();
        Resources resources = context.getResources();
        sInterceptKeyDelay = resources.getInteger(R.integer.intercept_key_delay_msec);
        sInterceptKeyExtendDelay = resources.getInteger(R.integer.intercept_key_extend_delay_msec);
    }
    private DelayedKeyAccumulator mSender;

    private IKeyInterceptor mInterceptor;
    private int[] mKeys;
    private int mThreshold; // key count to trigger execution

    public KeyInterceptor(View view, IKeyInterceptor interceptor, int[] keys, int threshold) {
        mInterceptor = interceptor;
        mThreshold = threshold;

        mKeys = Arrays.copyOf(keys, keys.length);
        Arrays.sort(mKeys);

        mSender = new DelayedKeyAccumulator(view, threshold);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean consumed = false;

        if (!event.isCanceled()) {
            if (Arrays.binarySearch(mKeys, event.getKeyCode()) >= 0) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                    case ACTION_UP:
                        consumed = true;    // consume event by default

                        if (event instanceof DelayedKeyEvent) {
                            if (((DelayedKeyEvent)event).getEventSource() == InputDevice.SOURCE_UNKNOWN) {
                                // its a resend so ignore
                                consumed = false;
                            }
                        }
                        if (consumed) {
                            // add event to delayed sender
                            boolean post = mSender.isTerminated();
                            mSender.addEvent(new DelayedKeyEvent(event));
                            if (post) {
                                Handler handler = new Handler();
                                mSender.setHandler(handler);
                                handler.postDelayed(mSender, sInterceptKeyDelay);
                            }
                        }
                        break;
                    case ACTION_MULTIPLE:
                        if (event.getRepeatCount() == mThreshold) {

                            Timber.i("Delayed key intercept recv");
                            mInterceptor.onIntercept();

                            consumed = true;
                        }
                        break;
                }
            }
        }
        logKeyEvent(event, "dispatchKeyEvent[" + consumed + "]");

        return consumed;
    }

    private static int DOWN_EVT = 0;
    private static int UP_EVT = 1;

    private static int MASK_2_DU_EVT = DOWN_EVT | (UP_EVT << 1);    // single key press pattern

    /**
     * Class to track key events
     */
    class DelayedKeyAccumulator implements Runnable {

        private ArrayList<KeyEvent> mEvents;
        private ReentrantLock mLock;
        private Handler mHandler;
        private View mView;

        private int mEventThreshold; // event count to trigger execution
        private int mKeyThreshold;  // key count to trigger execution
        private int mPattern;   // pattern of events in list; bit value 0 - down, 1 = up
        private int mKeyCode;   // keycode of current key event
        private boolean mPostponed;

        /**
         * Constructor
         * @param view
         * @param threshold Number of key presses to trigger intercept
         */
        public DelayedKeyAccumulator(View view, int threshold) {
            mView = view;
            mKeyThreshold = threshold;
            mEventThreshold = threshold * 2; // down & up event represent one key press
            mEvents = new ArrayList<>();
            mLock = new ReentrantLock();
            clear();
        }

        @Override
        public void run() {
            mLock.lock();
            try {
                execute();
            } finally {
                if (!mPostponed) {
                    clear();
                }
                mLock.unlock();
            }
        }

        private boolean execute() {
            boolean intercept = false;
            int count = mEvents.size();
            int lastAction = getAction(count - 1);

            if ((lastAction == ACTION_DOWN) && !mPostponed) {
                // postpone to see if the expected up action arrives
                mPostponed = true;
                mHandler.postDelayed(this, sInterceptKeyExtendDelay);
                Timber.i("Delayed key postponed");
            } else {
                // decide what to do
                mPostponed = false;
                switch (count) {
                    case 2:
                        // std down up sequence => dispatch, otherwise anomalous double key press => intercept
                        intercept = (mPattern != MASK_2_DU_EVT);
                        break;
                    default:
                        // anomalous single key press => dispatch
                        // anomalous triple, quadruple key press etc. key press => intercept
                        intercept = (count > 1);
                        break;
                }

                if (intercept) {
                    Timber.i("Delayed key intercept");
                    KeyEvent event = new KeyEvent(0, SystemClock.uptimeMillis(),
                                                    ACTION_MULTIPLE, mKeyCode, mKeyThreshold);
                    clear();    // throw away delayed keys
                    addEventToList(event);
                }
                BaseInputConnection mInputConnection = new BaseInputConnection(mView, true);
                for (KeyEvent event : mEvents) {
                    mInputConnection.sendKeyEvent(event);
                    logKeyEvent(event, "Dispatch event");
                }
            }
            return intercept;
        }

        private void clear() {
            mEvents.clear();
            mPattern = 0;
            mKeyCode = KEYCODE_UNKNOWN;
            mPostponed = false;
        }

        private int addEventToList(KeyEvent event) {
            if (mEvents.size() == 0) {
                mKeyCode = event.getKeyCode();  // set keycode for current inspection
            } else if (mKeyCode != event.getKeyCode()) {
                // different key, so restart
                execute();
                clear();
            }
            int bit = mEvents.size();
            mEvents.add(event);
            int mask = (event.getAction() == ACTION_UP ? UP_EVT : DOWN_EVT);
            mPattern |= (mask << bit);
            return (bit + 1);   // current size
        }

        public void addEvent(KeyEvent event) {
            mLock.lock();
            try {
                if (addEventToList(event) == mEventThreshold) {
                    if (execute()) {
                        clear();
                    }
                }
            } finally {
                mLock.unlock();
            }
        }

        private int getAction(int bit) {
            int action;
            if (bit >= 0) {
                action = ((mPattern & (UP_EVT << bit)) >> bit);
            } else {
                action = -1;
            }
            return action;
        }

        public boolean isTerminated() {
            boolean terminated;
            mLock.lock();
            try {
                terminated = mEvents.isEmpty();
            } finally {
                mLock.unlock();
            }
            return terminated;
        }

        public int cacheSize() {
            int size;
            mLock.lock();
            try {
                size = mEvents.size();
            } finally {
                mLock.unlock();
            }
            return size;
        }

        public void setHandler(Handler handler) {
            this.mHandler = handler;
        }
    }

    public interface IKeyInterceptor {

        void onIntercept();
    }
}
