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
import androidx.annotation.Nullable;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.utils.PreferenceControl;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

/**
 * Event post office
 */
public class PostOffice {

    private static final PostOffice ourInstance = new PostOffice();

    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;   // need a strong ref to avoid possible garbage collection

    private static boolean mLogPost;

    private static final int LOG_DELIVERY = 0x01;
    private static final int LOG_IGNORE = 0x02;
    private static int mLogDelivery;

    private static final int LOG_HANDLED = 0x01;
    private static final int LOG_NOT_HANDLED = 0x02;
    private static int mLogHandled;

    /**
     * Get the PostOffice
     * @return  PostOffice object
     */
    public static PostOffice getInstance() {
        return ourInstance;
    }

    /**
     * Constructor
     */
    private PostOffice() {
        Context context = TidderApplication.getWeakApplicationContext().get();

        mLogPost = PreferenceControl.getLogEventPostPreference(context);
        setLogDelivery(context);
        setLogHandled(context);

        mPrefListener = (sharedPreferences, key) -> {
            Context context1 = TidderApplication.getWeakApplicationContext().get();
            mLogPost = PreferenceControl.getLogEventPostPreference(context1);
            setLogDelivery(context1);
            setLogHandled(context1);
        };
        PreferenceControl.registerOnSharedPreferenceChangeListener(context, mPrefListener);
    }

    /**
     * Set delivery logging
     * @param context   Current context
     */
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

    /**
     * Set handled logging
     * @param context   Current context
     */
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

    /**
     * Post an event
     * @param event Event to post
     * @param tags  Address(s) to post too
     * @param <T>   Event class
     */
    public static <T extends AbstractEvent<?>> void postEvent(T event, String... tags) {
        logPost(event.addAddress(tags));
        EventBus.getDefault().post(event);
    }

    /**
     * Post a sticky event
     * @param event Event to post
     * @param tags  Address(s) to post too
     * @param <T>   Event class
     */
    public static <T extends AbstractEvent<?>> void postSticky(T event, String... tags) {
        logPostSticky(event.addAddress(tags));
        EventBus.getDefault().postSticky(event);
    }

    /**
     * Get the most recent sticky event
     * @param tClass    Class of event
     * @param <T>       Class of event
     * @return  Event or <code>null</code> if no events
     */
    @Nullable
    public static <T extends AbstractEvent<?>> T getStickyEvent(Class<T> tClass) {
        return EventBus.getDefault().getStickyEvent(tClass);
    }

    /**
     * Remove the most recent sticky event
     * @param tClass    Class of event
     * @param <T>       Class of event
     * @return  Event or <code>null</code> if no events
     */
    @Nullable
    public static <T extends AbstractEvent<?>> T removeStickyEvent(Class<T> tClass) {
        return EventBus.getDefault().removeStickyEvent(tClass);
    }

    /**
     * Check if an addressed event should be delivered to a destination
     * @param event         Event
     * @param destination   Destination address
     * @return  <code>true</code> if event should be delivered
     */
    public static boolean deliverEvent(AbstractEvent<?> event, String destination) {
        return deliverEvent(event, destination, destination);
    }

    /**
     * Check if an addressed event should be delivered to a destination
     * @param event         Event
     * @param destination   Destination address
     * @param tag           Tag to display in log
     * @return  <code>true</code> if event should be delivered
     */
    public static boolean deliverEvent(AbstractEvent<?> event, String destination, String tag) {
        boolean deliver = event.isForTag(destination);
        logDeliver(event, tag, deliver);
        return deliver;
    }

    /**
     * Check if an addressed or broadcast event should be delivered to a destination
     * @param event         Event
     * @param destination   Destination address
     * @return  <code>true</code> if event should be delivered
     */
    public static boolean deliverEventOrBroadcast(AbstractEvent<?> event, String destination) {
        return deliverEventOrBroadcast(event, destination, destination);
    }

    /**
     * Check if an addressed or broadcast event should be delivered to a destination
     * @param event         Event
     * @param destination   Destination address
     * @param tag       Tag to display in log
     * @return  <code>true</code> if event should be delivered
     */
    public static boolean deliverEventOrBroadcast(AbstractEvent<?> event, String destination, String tag) {
        boolean deliver = event.isBroadcast();
        if (!deliver) {
            deliver = deliverEvent(event, destination, tag);
        } else {
            logDeliver(event, tag, deliver);
        }
        return deliver;
    }

    /**
     * Log the posting of an event
     * @param event     Event
     * @param <T>       Event class
     */
    public static <T extends AbstractEvent<?>> void logPost(T event) {
        logPost(event, false);
    }

    /**
     * Log the posting of a sticky event
     * @param event     Event
     * @param <T>       Event class
     */
    public static <T extends AbstractEvent<?>> void logPostSticky(T event) {
        logPost(event, true);
    }

    /**
     * Log the posting of an event
     * @param event     Event
     * @param sticky    Sticky flag
     * @param <T>       Event class
     */
    private static <T extends AbstractEvent<?>> void logPost(T event, boolean sticky) {
        if (mLogPost) {
            Timber.i("postEvent%s: %s", (sticky ? "[STICKY]" : ""), event);
        }
    }

    /**
     * Log an event delivery
     * @param event     Event
     * @param tag       Tag to display in log
     * @param deliver   Delivered flag
     * @param <T>       Event class
     */
    public static <T extends AbstractEvent<?>> void logDeliver(T event, String tag, boolean deliver) {
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

    /**
     * Log an event handled status
     * @param event     Event
     * @param tag       Tag to display in log
     * @param handled   Handled flag
     * @param <T>       Event class
     */
    public static <T extends AbstractEvent<?>> void logHandled(T event, String tag, boolean handled) {
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

    /**
     * Register a subscriber
     * @param subscriber    Subscriber
     */
    public static void register(Object subscriber) {
        register(subscriber, subscriber.getClass().getSimpleName());
    }

    /**
     * Register a subscriber
     * @param subscriber    Subscriber
     * @param tag           Tag to display in log
     */
    public static void register(Object subscriber, String tag) {
        EventBus.getDefault().register(subscriber);
        Timber.d("PostOffice: register %s", tag);
    }

    /**
     * Unregister a subscriber
     * @param subscriber    Subscriber
     */
    public static void unregister(Object subscriber) {
        unregister(subscriber, subscriber.getClass().getSimpleName());
    }

    /**
     * Unregister a subscriber
     * @param subscriber    Subscriber
     * @param tag           Tag to display in log
     */
    public static void unregister(Object subscriber, String tag) {
        EventBus.getDefault().unregister(subscriber);
        Timber.d("PostOffice: unregister %s", tag);
    }

    /**
     * Check if a subscriber is registered
     * @param subscriber    Subscriber
     * @return <code>true</code> if registered
     */
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
