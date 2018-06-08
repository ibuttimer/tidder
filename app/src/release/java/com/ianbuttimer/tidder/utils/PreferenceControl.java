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
package com.ianbuttimer.tidder.utils;

import android.content.Context;

import com.ianbuttimer.tidder.R;


/**
 * Utility class providing access to application preferences
 */
@SuppressWarnings("unused")
public class PreferenceControl extends AbstractPreferenceControl {

    /**
     * Convenience method to retrieve Log Event Post enabled setting
     * @param context   The current context
     * @return  <code>true</code> if SFW enabled, <code>false</code> otherwise
     */
    public static boolean getLogEventPostPreference(Context context) {
        return false;
    }

    /**
     * Convenience method to retrieve Log Event Ignore enabled setting
     * @param context   The current context
     * @return  <code>true</code> if Refresh is enabled, <code>false</code> otherwise
     */
    public static boolean getLogEventIgnorePreference(Context context) {
        return false;
    }

    /**
     * Convenience method to retrieve Log Event Delivery setting
     * @param context   The current context
     * @return  Log Event Delivery setting
     */
    public static String getLogEventDeliveryPreference(Context context) {
        return context.getString(R.string.pref_event_delivery_neither_value);
    }

    /**
     * Convenience method to retrieve Log Event Handled setting
     * @param context   The current context
     * @return  Log Event Handled setting
     */
    public static String getLogEventHandledPreference(Context context) {
        return context.getString(R.string.pref_event_handled_neither_value);
    }

}
