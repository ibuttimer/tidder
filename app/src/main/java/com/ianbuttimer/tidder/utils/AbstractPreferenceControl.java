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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.BoolRes;
import androidx.annotation.StringRes;

import com.ianbuttimer.tidder.R;


/**
 * Base utility class providing access to application preferences
 */
@SuppressWarnings("unused")
public abstract class AbstractPreferenceControl {

    public enum PreferenceTypes { BOOLEAN, FLOAT, INTEGER, LONG, STRING }

    public static final int AUTOEXPAND_OFF = 0;
    public static final int AUTOEXPAND_ALL = Integer.MAX_VALUE;

    private static final String STRING_LIST_SEPARATOR = ",";

    /**
     * Convenience method to retrieve Safe for Work enabled setting
     * @param context   The current context
     * @return  <code>true</code> if SFW enabled, <code>false</code> otherwise
     */
    public static boolean getSafeForWorkPreference(Context context) {
        return getSharedBooleanPreference(context,
                R.string.pref_sfw_key, R.bool.pref_sfw_dflt_value);
    }

    /**
     * Convenience method to retrieve Log Http enabled setting
     * @param context   The current context
     * @return  <code>true</code> if SFW enabled, <code>false</code> otherwise
     */
    public static boolean getLogHttpPreference(Context context) {
        return getSharedBooleanPreference(context,
                R.string.pref_log_http_key, R.bool.pref_log_http_dflt_value);
    }

    /**
     * Convenience method to retrieve Refresh on Discard enabled setting
     * @param context   The current context
     * @return  <code>true</code> if Refresh is enabled, <code>false</code> otherwise
     */
    public static boolean getRefreshOnDiscardPreference(Context context) {
        return getSharedBooleanPreference(context,
                R.string.pref_refresh_on_discard_key, R.bool.pref_refresh_on_discard_dflt_value);
    }

    /**
     * Convenience method to retrieve Auto Expand Comment Thread enabled setting
     * @param context   The current context
     * @return  <code>true</code> if Auto expand is enabled, <code>false</code> otherwise
     */
    public static boolean getAutoExpandPreference(Context context) {
        return getSharedBooleanPreference(context,
                R.string.pref_autoexpand_key, R.bool.pref_autoexpand_dflt_value);
    }

    /**
     * Convenience method to retrieve Auto Expand Comment Thread level setting
     * @param context   The current context
     * @return  <code>true</code> if SFW enabled, <code>false</code> otherwise
     */
    public static int getAutoExpandLevelPreference(Context context) {
        int level;
        if (getAutoExpandPreference(context)) {
            String setting = getSharedStringPreference(context,
                    R.string.pref_autoexpand_level_key, R.string.pref_autoexpand_level_dflt_value);
            level = Integer.valueOf(setting);
        } else {
            level = AUTOEXPAND_OFF;
        }
        return level;
    }

    /**
     * Convenience method to retrieve Post Source setting
     * @param context   The current context
     * @return  <code>true</code> if SFW enabled, <code>false</code> otherwise
     */
    public static String getPostSourcePreference(Context context) {
        return getSharedStringPreference(context,
                R.string.pref_post_source_key, R.string.pref_post_source_dflt_value);
    }

    /**
     * Convenience method to retrieve the device id setting
     * @param context   The current context
     * @return  device id
     */
    public static String getDeviceIdPreference(Context context) {
        return getSharedStringPreference(context,
                    R.string.pref_device_key, R.string.pref_device_dflt_value);
    }

    /**
     * Convenience method to set the device id setting
     * @param context   The current context
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setDeviceIdPreference(Context context, String value) {
        return setSharedStringPreference(context, R.string.pref_device_key, value);
    }

    /**
     * Return the application preferences
     * @param context   The current context
     * @return SharedPreference instance
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    /**
     * Return the value of a string preference
     * @param context   The current context
     * @param key       The preference key           
     * @param dfltValue Default value to return if preference not available 
     * @return preference value
     */
    public static String getSharedStringPreference(Context context, String key, String dfltValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(key, dfltValue);
    }

    /**
     * Return the value of a string preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param dfltValue Default value to return if preference not available
     * @return preference value
     */
    public static String getSharedStringPreference(Context context, @StringRes int keyResId, String dfltValue) {
        String keyValue = context.getString(keyResId);
        return getSharedStringPreference(context, keyValue, dfltValue);
    }

    /**
     * Return the value of a string preference
     * @param context   The current context
     * @param key       The preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static String getSharedStringPreference(Context context, String key, @StringRes int dfltResId) {
        String dfltValue = context.getString(dfltResId);
        return getSharedStringPreference(context, key, dfltValue);
    }

    /**
     * Return the value of a string preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static String getSharedStringPreference(Context context, @StringRes int keyResId, @StringRes int dfltResId) {
        String dfltValue = context.getString(dfltResId);
        return getSharedStringPreference(context, keyResId, dfltValue);
    }

    /**
     * Set the value of a string preference
     * @param context   The current context
     * @param key       The preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setSharedStringPreference(Context context, String key, String value) {
        return setStringPreference(getSharedPreferences(context), key, value, false);
    }

    /**
     * Set the value of a string preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setSharedStringPreference(Context context, @StringRes int keyResId, String value) {
        String keyValue = context.getString(keyResId);
        return setSharedStringPreference(context, keyValue, value);
    }

    /**
     * Return the value of a boolean preference
     * @param context   The current context
     * @param key       The preference key
     * @param dfltValue Default value to return if preference not available
     * @return preference value
     */
    public static boolean getSharedBooleanPreference(Context context, String key, boolean dfltValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(key, dfltValue);
    }

    /**
     * Return the value of a boolean preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param dfltValue Default value to return if preference not available
     * @return preference value
     */
    public static boolean getSharedBooleanPreference(Context context, @StringRes int keyResId, boolean dfltValue) {
        String keyValue = context.getString(keyResId);
        return getSharedBooleanPreference(context, keyValue, dfltValue);
    }

    /**
     * Return the value of a boolean preference
     * @param context   The current context
     * @param key       The preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static boolean getSharedBooleanPreference(Context context, String key, @BoolRes int dfltResId) {
        boolean dfltValue = context.getResources().getBoolean(dfltResId);
        return getSharedBooleanPreference(context, key, dfltValue);
    }

    /**
     * Return the value of a boolean preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static boolean getSharedBooleanPreference(Context context, @StringRes int keyResId, @BoolRes int dfltResId) {
        String keyValue = context.getString(keyResId);
        return getSharedBooleanPreference(context, keyValue, dfltResId);
    }

    /**
     * Set the value of a boolean preference
     * @param context   The current context
     * @param key       The preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setSharedBooleanPreference(Context context, String key, boolean value) {
        return setBooleanPreference(getSharedPreferences(context), key, value, false);
    }

    /**
     * Set the value of a boolean preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setSharedBooleanPreference(Context context, @StringRes int keyResId, boolean value) {
        String keyValue = context.getString(keyResId);
        return setSharedBooleanPreference(context, keyValue, value);
    }

    /**
     * Return the value of an integer preference
     * @param context   The current context
     * @param key       The preference key
     * @param dfltValue Default value to return if preference not available
     * @return preference value
     */
    public static int getSharedIntPreference(Context context, String key, int dfltValue) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getInt(key, dfltValue);
    }

    /**
     * Return the value of an integer preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param dfltValue Default value to return if preference not available
     * @return preference value
     */
    public static int getSharedIntPreference(Context context, @StringRes int keyResId, int dfltValue) {
        String keyValue = context.getString(keyResId);
        return getSharedIntPreference(context, keyValue, dfltValue);
    }

    /**
     * Set the value of an integer preference
     * @param context   The current context
     * @param key       The preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setSharedIntPreference(Context context, String key, int value) {
        return setIntPreference(getSharedPreferences(context), key, value, false);
    }

    /**
     * Set the value of an integer preference
     * @param context   The current context
     * @param keyResId  Resource id of preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setSharedIntPreference(Context context, @StringRes int keyResId, int value) {
        String keyValue = context.getString(keyResId);
        return setSharedIntPreference(context, keyValue, value);
    }

    /**
     * Return the value of a preference
     * @param context   The current context
     * @param type      Preference type
     * @param keyResId  Resource id of preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static Object getSharedPreference(Context context, PreferenceTypes type, @StringRes int keyResId, int dfltResId) {
        Object value;
        switch (type) {
            case STRING:
                value = getSharedStringPreference(context, keyResId, dfltResId);
                break;
            case BOOLEAN:
                value = getSharedBooleanPreference(context, keyResId, dfltResId);
                break;
            case INTEGER:
                value = getSharedIntPreference(context, keyResId, dfltResId);
                break;
            default:
                value = null;
                break;
        }
        return value;
    }

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     * @param context   The current context
     * @param listener  Listener to register
     */
    public static void registerOnSharedPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregisters a previous callback.
     * @param context   The current context
     * @param listener  Listener to unregister
     */
    public static void unregisterOnSharedPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Return the activity preferences
     * @param activity  The current activity
     * @return SharedPreferences instance
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#getPreferences(int)">Activity.getPreferences(int)</a>
     */
    public static SharedPreferences getPreferences(Activity activity) {
        return activity.getPreferences(Context.MODE_PRIVATE);
    }

    /**
     * Return the value of a string preference
     * @param activity  The current activity
     * @param key       The preference key
     * @param dfltValue Default value to return if preference not available
     * @return String value
     */
    public static String getStringPreference(Activity activity, String key, String dfltValue) {
        SharedPreferences prefs = getPreferences(activity);
        return prefs.getString(key, dfltValue);
    }

    /**
     * Return the value of a string preference
     * @param activity  The current activity
     * @param keyResId  Resource id of preference key
     * @param dfltValue Default value to return if preference not available
     * @return String value
     */
    public static String getStringPreference(Activity activity, int keyResId, String dfltValue) {
        String keyValue = activity.getString(keyResId);
        return getStringPreference(activity, keyValue, dfltValue);
    }

    /**
     * Return the value of a string preference
     * @param activity  The current activity
     * @param key       The preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static String getStringPreference(Activity activity, String key, int dfltResId) {
        String dfltValue = activity.getString(dfltResId);
        return getStringPreference(activity, key, dfltValue);
    }

    /**
     * Return the value of a string preference
     * @param activity  The current activity
     * @param keyResId  Resource id of preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static String getStringPreference(Activity activity, int keyResId, int dfltResId) {
        String keyValue = activity.getString(keyResId);
        return getStringPreference(activity, keyValue, dfltResId);
    }

    /**
     * Set the value of a string preference
     * @param prefs     SharedPreferences to update
     * @param key       The preference key
     * @param value     The preference value to save
     * @param commit    Commit flag; <code>true</code> changes synchronously, <code>false</code> changes asynchronously
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setStringPreference(SharedPreferences prefs, String key, String value, boolean commit) {
        boolean result = false;     // default is false for commit(), and always for apply()
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        if (commit) {
            result = editor.commit();   // synchronous commit
        } else {
            editor.apply();             // asynchronous commit
        }
        return result;
    }

    /**
     * Set the value of a string preference
     * @param activity  The current activity
     * @param key       The preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setStringPreference(Activity activity, String key, String value) {
        return setStringPreference(getPreferences(activity), key, value, false);
    }

    /**
     * Set the value of a string preference
     * @param activity  The current activity
     * @param keyResId  Resource id of preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setStringPreference(Activity activity, int keyResId, String value) {
        String keyValue = activity.getString(keyResId);
        return setStringPreference(getPreferences(activity), keyValue, value, false);
    }

    /**
     * Return the value of a boolean preferences
     * @param activity  The current activity
     * @param key       The preference key
     * @param dfltValue Default value to return if preference not available
     * @return preference value
     */
    public static boolean getBooleanPreference(Activity activity, String key, boolean dfltValue) {
        SharedPreferences prefs = getPreferences(activity);
        return prefs.getBoolean(key, dfltValue);
    }

    /**
     * Return the value of a boolean preferences
     * @param activity  The current activity
     * @param keyResId  Resource id of preference key
     * @param dfltValue Default value to return if preference not available
     * @return preference value
     */
    public static boolean getBooleanPreference(Activity activity, int keyResId, boolean dfltValue) {
        String keyValue = activity.getString(keyResId);
        return getBooleanPreference(activity, keyValue, dfltValue);
    }

    /**
     * Return the value of a boolean preferences
     * @param activity  The current activity
     * @param key       The preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static boolean getBooleanPreference(Activity activity, String key, int dfltResId) {
        boolean dfltValue = activity.getResources().getBoolean(dfltResId);
        return getBooleanPreference(activity, key, dfltValue);
    }

    /**
     * Return the value of a boolean preferences
     * @param activity  The current activity
     * @param keyResId  Resource id of preference key
     * @param dfltResId Resource id of default value to return if preference not available
     * @return preference value
     */
    public static boolean getBooleanPreference(Activity activity, int keyResId, int dfltResId) {
        String keyValue = activity.getString(keyResId);
        return getBooleanPreference(activity, keyValue, dfltResId);
    }

    /**
     * Set the value of a boolean preference
     * @param prefs     SharedPreferences to update
     * @param key       The preference key
     * @param value     The preference value to save
     * @param commit    Commit flag; <code>true</code> changes synchronously, <code>false</code> changes asynchronously
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setBooleanPreference(SharedPreferences prefs, String key, boolean value, boolean commit) {
        boolean result = false;     // default is false for commit(), and always for apply()
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        if (commit) {
            result = editor.commit();
        } else {
            editor.apply();
        }
        return result;
    }

    /**
     * Set the value of a string preference
     * @param activity  The current activity
     * @param key       The preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setBooleanPreference(Activity activity, String key, boolean value) {
        return setBooleanPreference(getPreferences(activity), key, value, false);
    }

    /**
     * Set the value of a string preference
     * @param activity  The current activity
     * @param keyResId  Resource id of preference key
     * @param value     The preference value to save
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setBooleanPreference(Activity activity, int keyResId, boolean value) {
        String keyValue = activity.getString(keyResId);
        return setBooleanPreference(getPreferences(activity), keyValue, value, false);
    }


    /**
     * Set the value of an int preference
     * @param prefs     SharedPreferences to update
     * @param key       The preference key
     * @param value     The preference value to save
     * @param commit    Commit flag; <code>true</code> changes synchronously, <code>false</code> changes asynchronously
     * @return <code>true</code> changes successfully committed synchronously, otherwise <code>false</code>
     */
    public static boolean setIntPreference(SharedPreferences prefs, String key, int value, boolean commit) {
        boolean result = false;     // default is false for commit(), and always for apply()
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        if (commit) {
            result = editor.commit();
        } else {
            editor.apply();
        }
        return result;
    }

}
