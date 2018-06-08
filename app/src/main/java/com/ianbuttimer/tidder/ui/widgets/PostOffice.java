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
import android.content.SharedPreferences;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.utils.PreferenceControl;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class PostOffice {

    private static final PostOffice ourInstance = new PostOffice();

    private static boolean mLogPost;

    private static final int LOG_DELIVERY = 0x01;
    private static final int LOG_IGNORE = 0x02;
    private static int mLogDelivery;

    private static final int LOG_HANDLED = 0x01;
    private static final int LOG_NOT_HANDLED = 0x02;
    private static int mLogHandled;

    public static PostOffice getInstance() {
        return ourInstance;
    }

    private PostOffice() {
        Context context = TidderApplication.getWeakApplicationContext().get();

        mLogPost = PreferenceControl.getLogEventPostPreference(context);
        setLogDelivery(context);
        setLogHandled(context);

        SharedPreferences.OnSharedPreferenceChangeListener sPrefListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        Context context = TidderApplication.getWeakApplicationContext().get();
                        mLogPost = PreferenceControl.getLogEventPostPreference(context);
                        setLogDelivery(context);
                        setLogHandled(context);
                    }
                };
        PreferenceControl.registerOnSharedPreferenceChangeListener(context, sPrefListener);
    }


    private void setLogDelivery(Context context) {
        String setting = PreferenceControl.getLogEventDeliveryPreference(context);

        mLogDelivery = 0;
        if (setting.equals(context.getString(R.string.pref_event_delivery_both_value))) {
            mLogDelivery = LOG_DELIVERY | LOG_IGNORE;
        } else if (setting.equals(context.getString(R.string.pref_event_delivery_deliver_value))) {
            mLogDelivery = LOG_DELIVERY;
        } else if (setting.equals(context.getString(R.string.pref_event_delivery_ignore_value))) {
            mLogDelivery = LOG_IGNORE;
        }
    }

    private void setLogHandled(Context context) {
        String setting = PreferenceControl.getLogEventHandledPreference(context);

        mLogHandled = 0;
        if (setting.equals(context.getString(R.string.pref_event_handled_both_value))) {
            mLogHandled = LOG_HANDLED | LOG_NOT_HANDLED;
        } else if (setting.equals(context.getString(R.string.pref_event_handled_handle_value))) {
            mLogHandled = LOG_HANDLED;
        } else if (setting.equals(context.getString(R.string.pref_event_handled_ignore_value))) {
            mLogHandled = LOG_NOT_HANDLED;
        }
    }

    public static <T extends AbstractEvent> void postEvent(T event, String... tags) {
        logPost(event.addAddress(tags));
        EventBus.getDefault().post(event);
    }

    public static boolean deliverEvent(AbstractEvent event, String destination) {
        return deliverEvent(event, destination, destination);
    }

    public static boolean deliverEvent(AbstractEvent event, String destination, String tag) {
        boolean deliver = event.isForTag(destination);
        logDeliver(event, tag, deliver);
        return deliver;
    }

    public static boolean deliverEventOrBroadcast(AbstractEvent event, String destination) {
        return deliverEventOrBroadcast(event, destination, destination);
    }

    public static boolean deliverEventOrBroadcast(AbstractEvent event, String destination, String tag) {
        boolean deliver = event.isBroadcast();
        if (!deliver) {
            deliver = deliverEvent(event, destination, tag);
        } else {
            logDeliver(event, tag, deliver);
        }
        return deliver;
    }


    public static <T extends AbstractEvent> void logPost(T event) {
        if (mLogPost) {
            Timber.i("postEvent: %s", event);
        }
    }

    public static <T extends AbstractEvent> void logDeliver(T event, String tag, boolean deliver) {
        String msg = null;
        if (deliver && isSet(mLogDelivery, LOG_DELIVERY)) {
            msg = "";
        } else if (isSet(mLogDelivery, LOG_IGNORE)) {
            msg = "IGNORE ";
        }
        if (msg != null) {
            Timber.d("deliverEvent[%s]: %s%s", tag, msg, event);
        }
    }

    public static <T extends AbstractEvent> void logHandled(T event, String tag, boolean handled) {
        String msg = null;
        if (handled && isSet(mLogHandled, LOG_HANDLED)) {
            msg = "";
        } else if (isSet(mLogHandled, LOG_NOT_HANDLED)) {
            msg = "NOT ";
        }
        if (msg != null) {
            Timber.d("handleEvent[%s]: %sHANDLED %s", tag, msg, event);
        }
    }

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
        Timber.d("PostOffice: register %s", subscriber.getClass().getSimpleName());
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
        Timber.d("PostOffice: unregister %s", subscriber.getClass().getSimpleName());
    }

    public static boolean isRegistered(Object subscriber) {
        boolean registered = EventBus.getDefault().isRegistered(subscriber);
        Timber.d("PostOffice: isRegistered %s %s", subscriber.getClass().getSimpleName(), registered);
        return registered;
    }


    private static boolean isSet(int value, int bit) {
        return ((value & bit) != 0);
    }


    public interface IAddressable {

        String getAddress();
    }
}
