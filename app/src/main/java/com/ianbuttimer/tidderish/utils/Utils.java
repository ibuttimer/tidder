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
package com.ianbuttimer.tidderish.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.core.app.NavUtils;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.TextView;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.TidderApplication;
import com.ianbuttimer.tidderish.reddit.RedditClient;
import com.ianbuttimer.tidderish.ui.AboutActivity;
import com.ianbuttimer.tidderish.ui.FollowActivity;
import com.ianbuttimer.tidderish.ui.HelpActivity;
import com.ianbuttimer.tidderish.ui.SettingsActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static android.text.format.DateUtils.HOUR_IN_MILLIS;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;


/**
 * This class contains miscellaneous utility functions
 */
@SuppressWarnings("unused")
public class Utils {

    public static final long MSEC_PER_DAY = DateUtils.DAY_IN_MILLIS;
    public static final long MSEC_PER_WEEK = MSEC_PER_DAY * 7;
    public static final long MSEC_PER_FORTNIGHT = MSEC_PER_WEEK * 2;
    public static final long MSEC_PER_MONTH = MSEC_PER_DAY * 31;

    public static final int ONE_K = 1000;
    public static final int ONE_M = 1000000;

    /**
     * Private constructor
     */
    private Utils() {
        // can't instantiate class
    }

    /**
     * Return a formatted version string for the app
     * @param context   Context to use
     * @return  Version string
     */
    public static String getVersionString(Context context) {
        return MessageFormat.format(context.getResources().getString(R.string.app_version), getVersion(context));
    }

    /**
     * Return the version string for the app
     * @param context   Context to use
     * @return  Version string
     */
    public static String getVersion(Context context) {
        String ver = "";
        try {
            ver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Unable to retrieve version");
        }
        return ver;
    }

    /**
     * Return a formatted user-agent string for the app
     * @param context   Context to use
     * @return  Version string
     */
    public static String getUserAgentString(Context context) {
        return MessageFormat.format(context.getResources().getString(R.string.user_agent),
                context.getString(R.string.app_name), getVersion(context));
    }

    /**
     * Retrieve meta-data bundle from the manifest
     * @param context   Context to use
     * @return  meta-data string
     */
    public static Bundle getManifestMetaDataBundle(Context context) {
        Bundle bundle = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            bundle = ai.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        return bundle;
    }

    /**
     * Retrieve the value of a meta-data entry from the manifest
     * @param context   Context to use
     * @param key       Meta-data name
     * @return  meta-data string
     */
    public static String getManifestMetaDataString(Context context, String key) {
        String metaData = null;
        Bundle bundle = getManifestMetaDataBundle(context);
        if (bundle != null) {
            metaData = bundle.getString(key);
        }
        return metaData;
    }

    /**
     * Retrieve the value of a meta-data entry from the manifest
     * @param context   Context to use
     * @param key       String resource for meta-data name
     * @return  meta-data string
     */
    public static String getManifestMetaDataString(Context context, @StringRes int key) {
        String name = context.getString(key);
        String metaData = null;
        if (!TextUtils.isEmpty(name)) {
            metaData = getManifestMetaDataString(context, name);
        }
        return metaData;
    }

    /**
     * Retrieve the value of a meta-data entry from the manifest
     * @param context   Context to use
     * @param key       Meta-data name
     * @param dfltValue Default value
     * @return  meta-data string
     */
    public static boolean getManifestMetaDataBoolean(Context context, String key, boolean dfltValue) {
        boolean metaData = dfltValue;
        Bundle bundle = getManifestMetaDataBundle(context);
        if (bundle != null) {
            metaData = bundle.getBoolean(key, dfltValue);
        }
        return metaData;
    }

    /**
     * Check if the string has some content other than spaces or empty
     * @param str   String to check
     * @return true if the string is has content.
     */
    public static boolean stringHasContent(String str) {
        return (!TextUtils.isEmpty(str) && (TextUtils.getTrimmedLength(str) > 0));
    }

    /**
     * Start an activity
     * @param context   The current context
     * @param intent    Intent to start activity
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivity(Context context, Intent intent) {
        return startActivity(context, new Intent[] { intent });
    }

    /**
     * Start an activity
     * @param activity      Parent activity
     * @param intent        Intent to start activity
     * @param requestCode   Reply request code
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivityForResult(Activity activity, Intent intent, int requestCode) {
        return startActivityForResult(activity, new Intent[] { intent }, requestCode);
    }

    /**
     * Start an activity with fallback options. All intents will be attempted in ascending order until
     * one is successfully resolved, and that one is used.
     * @param context   The current context
     * @param intents       Intents to start activity
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivity(Context context, Intent[] intents) {
        boolean resolved = false;
        PackageManager manager = context.getPackageManager();
        for (int i = 0, ll = intents.length; (i < ll) && !resolved; i++) {
            resolved = (intents[i].resolveActivity(manager) != null);
            if (resolved) {
                context.startActivity(intents[i]);
            }
        }
        return resolved;
    }

    /**
     * Start an activity with fallback options. All intents will be attempted in ascending order until
     * one is successfully resolved, and that one is used.
     * @param activity      Parent activity
     * @param intents       Intents to start activity
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivityForResult(Activity activity, Intent[] intents, int requestCode) {
        boolean resolved = false;
        PackageManager manager = activity.getPackageManager();
        for (int i = 0, ll = intents.length; (i < ll) && !resolved; i++) {
            resolved = (intents[i].resolveActivity(manager) != null);
            if (resolved) {
                activity.startActivityForResult(intents[i], requestCode);
            }
        }
        return resolved;
    }

    /**
     * Handle action bar item clicks
     * @param activity  The current activity
     * @param item      Menu item clicked
     * @return  <code>true</code> if handled
     */
    public static boolean onOptionsItemSelected(Activity activity, MenuItem item) {
        int id = item.getItemId();
        boolean handled = true;
        Intent intent = null;

        // don't use menu action ids in switch statement as Resource IDs will be non-final in Android Gradle Plugin version 5.0
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(activity);
        } else if (id == R.id.action_settings) {
            intent = new Intent(activity, SettingsActivity.class);
        } else if (id == R.id.action_help) {
            intent = new Intent(activity, HelpActivity.class);
        } else if (id == R.id.action_about) {
            intent = new Intent(activity, AboutActivity.class);
        } else if (id == R.id.action_logout) {
            RedditClient.getClient().logout(activity);
        } else if (id == R.id.action_follow) {
            intent = new Intent(activity, FollowActivity.class);
        } else {
            handled = false;
        }

        if (intent != null) {
            startActivity(activity, intent);
        }
        return handled;
    }

    /**
     * Returns a ascending numerical order sorted copy of an array
     * @param unsorted  Array to copy
     * @return  new sorted array, or empty array if <code>null</code> was passed
     */
    public static int[] getSortedArray(int[] unsorted) {
        int[] sorted;
        if (unsorted == null) {
            sorted = new int[] {};
        } else {
            sorted = Arrays.copyOf(unsorted, unsorted.length);
            Arrays.sort(sorted);
        }
        return sorted;
    }

    /**
     * Return an array representing the specified column of a multi-dimension array
     * @param array         Array to get column from
     * @param columnIndex   Index of column to get
     * @return  Column array
     */
    public static int[] getArrayColumn(int[][] array, int columnIndex) {
        int length = array.length;
        if (columnIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("Invalid column index");
        }
        int[] column = new int[length];
        for (int i = 0; i < length; i++) {
            if (columnIndex >= array[i].length) {
                throw new ArrayIndexOutOfBoundsException("Invalid column index on row " + i);
            }
            column[i] = array[i][columnIndex];
        }
        return column;
    }

    /**
     * Return the index of a row from a multi-dimension array where the value at a particular column matches a value<br>
     * <b>NOTE:</b> The search column must be sorted in ascending order.
     * @param array         Array to get column from
     * @param columnIndex   Index of column to check
     * @param value         Value to find
     * @return  Index of row or <code>-1</code> if not found
     */
    public static int binarySearch(int[][] array, int columnIndex, int value) {
        int row = -1;
        if (Utils.arrayHasSize(array)) {
            int lo = 0;
            int hi = array.length - 1;
            while (lo <= hi) {
                int mid = (lo + hi) / 2;
                if (value < array[mid][columnIndex]) {
                    hi = mid - 1;
                } else if (value > array[mid][columnIndex]) {
                    lo = mid + 1;
                } else {
                    row = mid;
                    break;
                }
            }
        }
        return row;
    }

    /**
     * Write an Integer object array to a Parcel
     * @param parcel    Parcel to write to
     * @param array     Array to write
     */
    public static void writeIntegerArrayToParcel(Parcel parcel, Integer[] array) {
        int len = array.length;
        int[] intArray = new int[len];
        for (int index = 0; index < len; index++) {
            intArray[index] = array[index];
        }
        parcel.writeInt(len);
        if (len > 0) {
            parcel.writeIntArray(intArray);
        }
    }

    /**
     * Read an int array from a Parcel
     * @param in    Parcel to read from
     * @return  Integer object array
     */
    public static int[] readIntArrayFromParcel(Parcel in) {
        int len = in.readInt();
        int[] intArray = new int[len];
        if (len > 0) {
            in.readIntArray(intArray);
        }
        return intArray;
    }

    /**
     * Read an Integer object array from a Parcel
     * @param in    Parcel to read from
     * @return  Integer object array
     */
    public static Integer[] readIntegerArrayFromParcel(Parcel in) {
        int[] intArray = readIntArrayFromParcel(in);
        int len = intArray.length;
        Integer[] array = new Integer[len];
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                array[i] = intArray[i];
            }
        }
        return array;
    }

    /**
     * Read an array from a Parcel
     * @param in            Parcel to read from
     * @param loader        Class loader to create array elements
     * @param arrayClass    Class of the copy to be returned
     * @return Object array
     */
    public static Object[] readArrayFromParcel(Parcel in, ClassLoader loader, Class<? extends Object[]> arrayClass) {
        Object[] objArray = in.readArray(loader);
        return Arrays.copyOf(objArray, objArray.length, arrayClass);
    }

    /**
     * Write the representation of a boolean to a parcel
     * @param parcel    Parcel to write to
     * @param bool      Valur to write
     */
    public static void writeBooleanToParcel(Parcel parcel, Boolean bool) {
        parcel.writeInt(bool ? 1 : 0);
    }

    /**
     * Read a boolean from a Parcel
     * @param in            Parcel to read from
     * @return Boolean value
     */
    public static boolean readBooleanFromParcel(Parcel in) {
        return (in.readInt() == 1);
    }

    /**
     * Read an array from a Bundle
     * @param in            Bundle to read from
     * @param key           Key for array value
     * @param arrayClass    Class of the copy to be returned
     * @return Object array
     */
    public static Object[] getParcelableArrayFromBundle(Bundle in, String key, Class<? extends Object[]> arrayClass) {
        Parcelable[] objArray = in.getParcelableArray(key);
        Object[] result = objArray;
        if (objArray != null) {
            result = Arrays.copyOf(objArray, objArray.length, arrayClass);
        }
        return result;
    }

    /**
     * Returns a string describing 'time' relative to 'now'
     * @param time  Time to describe in msec
     * @param now   The current time
     * @return  relative time string
     */
    public static String getRelativeTimeSpanString (long time, long now) {
        String timeSpan;
        long diff = Math.abs(now - time);
        boolean ago = (now > time);
        long resolution;
        boolean useLocal = false;
        if (diff < HOUR_IN_MILLIS) {
            resolution = MINUTE_IN_MILLIS;
        } else if (diff < DateUtils.DAY_IN_MILLIS) {
            resolution = HOUR_IN_MILLIS;
        } else if (diff < MSEC_PER_WEEK) {
            resolution = DateUtils.DAY_IN_MILLIS;
        } else if (diff < (MSEC_PER_WEEK + MSEC_PER_DAY)) {
            resolution = DateUtils.WEEK_IN_MILLIS;
        } else if (diff < MSEC_PER_FORTNIGHT) {
            // DateUtils.getRelativeTimeSpanString returns the date
            resolution = DateUtils.DAY_IN_MILLIS;
            useLocal = true;
        } else if (diff < MSEC_PER_MONTH) {
            resolution = DateUtils.WEEK_IN_MILLIS;
        } else if (diff < DateUtils.YEAR_IN_MILLIS) {
            resolution = MSEC_PER_MONTH;
            useLocal = true;
        } else {
            resolution = DateUtils.YEAR_IN_MILLIS;
            useLocal = true;
        }

        if (!useLocal) {
            timeSpan = DateUtils.getRelativeTimeSpanString(time, now, resolution).toString();
        } else {
            long span;
            @PluralsRes int redId;
            if (resolution == DateUtils.YEAR_IN_MILLIS) {
                span = (diff / DateUtils.YEAR_IN_MILLIS);
                if (ago) {
                    redId = R.plurals.time_span_years_ago;
                } else {
                    redId = R.plurals.time_span_in_years;
                }
            } else {
                GregorianCalendar calendar = new GregorianCalendar();
                long future;
                int field;
                if (resolution == MSEC_PER_MONTH) {
                    field = Calendar.MONTH;
                    redId = (ago ? R.plurals.time_span_months_ago : R.plurals.time_span_in_months);
                } else {
                    field = Calendar.DAY_OF_YEAR;
                    redId = (ago ? R.plurals.time_span_days_ago : R.plurals.time_span_in_days);
                }
                if (ago) {
                    calendar.setTimeInMillis(time);
                    future = now;
                } else {
                    calendar.setTimeInMillis(now);
                    future = time;
                }
                span = -1;
                while (calendar.getTimeInMillis() < future) {
                    calendar.add(field, 1);
                    ++span;
                }
            }
            timeSpan = TidderApplication.getWeakApplicationContext().get()
                            .getResources().getQuantityString(
                                    redId, Long.valueOf(span).intValue(), span);
        }

        return timeSpan;
    }

    /**
     * Get a count indication string of the form<br>
     * <ul>
     *     <li>0 items</li>
     *     <li>0.0k items</li>
     *     <li>0.0m items</li>
     * </ul>
     * @param count     Count to get indication of
     * @param item      Singular item
     * @param items     Multiple items
     * @return  Pair with first item being the indication string, and second content description
     */
    public static Pair<String, String> getCountIndication(int count, String item, String items) {
        double indicationCount = count;
        String format = "###,##0.0";
        String itemName = items;
        @StringRes int resId;
        @StringRes int resIdItem;
        @StringRes int resCdItem;
        if (indicationCount < ONE_K) {
            format = "#0";
            resId = R.string.count_indication_1;
            resIdItem = R.string.count_indication_1_item;
            resCdItem = R.string.count_indication_content_desc_1;
            if (count == 1) {
                itemName = item;
            }
        } else if (indicationCount < ONE_M) {
            resId = R.string.count_indication_k;
            resIdItem = R.string.count_indication_k_item;
            resCdItem = R.string.count_indication_content_desc_k;
            indicationCount /= ONE_K;
        } else {
            resId = R.string.count_indication_m;
            resIdItem = R.string.count_indication_m_item;
            resCdItem = R.string.count_indication_content_desc_m;
            indicationCount /= ONE_M;
        }
        DecimalFormat df = new DecimalFormat(format);
        Context context = TidderApplication.getWeakApplicationContext().get();
        String cntIndication = df.format(indicationCount);
        String indication;
        String contentDesc;
        if (TextUtils.isEmpty(itemName)) {
            indication = MessageFormat.format(context.getString(resId), cntIndication);
            contentDesc = MessageFormat.format(context.getString(resCdItem), cntIndication);
        } else {
            indication = MessageFormat.format(context.getString(resIdItem), cntIndication, itemName);
            contentDesc = MessageFormat.format(context.getString(resCdItem), cntIndication, itemName);
        }
        return new Pair<>(indication, contentDesc);
    }

    /**
     * Get a count indication string of the form<br>
     * <ul>
     *     <li>0 items</li>
     *     <li>0.0k items</li>
     *     <li>0.0m items</li>
     * </ul>
     * @param count     Count to get indication of
     * @param item      Singular item
     * @param items     Multiple items
     * @return  Pair with first item being the indication string, and second content description
     */
    public static Pair<String, String> getCountIndication(int count, @StringRes int item, @StringRes int items) {
        Context context = TidderApplication.getWeakApplicationContext().get();
        String itemStr = null;
        String itemsStr = null;
        if (item != 0) {
            itemStr = context.getString(item);
        }
        if (items != 0) {
            itemsStr = context.getString(items);
        }
        return getCountIndication(count, itemStr, itemsStr);
    }

    /**
     * Get a count indication string of the form<br>
     * <ul>
     *     <li>0 items</li>
     *     <li>0.0k items</li>
     *     <li>0.0m items</li>
     * </ul>
     * @param textView  TextView to apply to
     * @param count     Count to get indication of
     * @param item      Singular item
     * @param items     Multiple items
     */
    public static void setCountIndication(TextView textView, int count, @StringRes int item, @StringRes int items) {
        if (textView != null) {
            Pair<String, String> cntIndication = getCountIndication(count, item, items);
            textView.setText(cntIndication.first);
            textView.setContentDescription(cntIndication.second);
        }
    }

    /**
     * Copy fields from <code>src</code> to <code>dest</code>, excluding final, static & volatile fields.
     * @param src   Source object
     * @param dest  Destination object
     * @return  <code>true</code> if copied successfully
     */
    public static boolean copyFields(Object src, Object dest) {
        boolean copied = false;
        if ((src != null) && (dest != null)) {
            Class<?> srcClass = src.getClass();
            Class<?> destClass = dest.getClass();
            List<Field> fields = getFields(srcClass, destClass);
            if (fields == null) {
                fields = getFields(Objects.requireNonNull(srcClass.getSuperclass()), destClass.getSuperclass());
            }
            if (fields != null) {
                copied = true;
                for (Field field : fields) {
                    Class fieldClass = field.getType();
                    int modifiers = field.getModifiers();
                    if (Modifier.isFinal(modifiers)
                            || Modifier.isStatic(modifiers)
                            || Modifier.isVolatile(modifiers)) {
                        // don't copy final, static or volatile fields
                        continue;
                    }
                    if (!Modifier.isPublic(modifiers) ||
                            !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
                        field.setAccessible(true);
                    }
                    try {
                        if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
                            field.setLong(dest, field.getLong(src));
                        } else if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)) {
                            field.setInt(dest, field.getInt(src));
                        } else if (Short.class.equals(fieldClass) || short.class.equals(fieldClass)) {
                            field.setShort(dest, field.getShort(src));
                        } else if (Byte.class.equals(fieldClass) || byte.class.equals(fieldClass)) {
                            field.setByte(dest, field.getByte(src));
                        } else if (Character.class.equals(fieldClass) || char.class.equals(fieldClass)) {
                            field.setChar(dest, field.getChar(src));
                        } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)) {
                            field.setFloat(dest, field.getFloat(src));
                        } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)) {
                            field.setDouble(dest, field.getDouble(src));
                        } else if (Boolean.class.equals(fieldClass) || boolean.class.equals(fieldClass)) {
                            field.setBoolean(dest, field.getBoolean(src));
                        } else {
                            field.set(dest, field.get(src));
                        }
                    } catch (IllegalAccessException e) {
                        Timber.e(e);
                        copied = false;
                    }
                }
            }
        }
        return copied;
    }

    /**
     * Get the common fields of the argument classes
     * @param classA    First class
     * @param classB    Second class
     * @return  Field list or <code>null</code> if no common fields
     */
    @Nullable
    private static List<Field> getFields(Class<?> classA, Class<?> classB) {
        List<Field> fields = null;
        Class<?> fieldClass = null;
        if (classA.isAssignableFrom(classB)) {
            // classA may be assigned from destination, so use classA fields
            fieldClass = classA;
        } else if (classB.isAssignableFrom(classA)) {
            // classB may be assigned from classA, so use classB fields
            fieldClass = classB;
        }
        return getFields(fieldClass);
    }

    /**
     * Get the fields of the argument class
     * @param clazz    Class to list fields
     * @return  Field list or <code>null</code> if no class argument
     */
    @Nullable
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = null;
        if (clazz != null) {
            fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
            // get super class fields
            for (Class<?> superCls = clazz.getSuperclass();
                    (superCls != Object.class) && (superCls != null);
                        superCls = superCls.getSuperclass()) {
                fields.addAll(Arrays.asList(superCls.getDeclaredFields()));
            }
        }
        return fields;
    }

    /**
     * Check if a string is numeric
     * @param inputData String to check
     * @return  <code>true</code> if argument is numeric
     * @see <a href="https://rosettacode.org/wiki/Determine_if_a_string_is_numeric#Java">Determine if a string is numeric</a>
     */
    public static boolean isNumeric(String inputData) {
        return isNumeric(NumberFormat.getInstance(), inputData);
    }

    /**
     * Check if a string is numeric
     * @param inputData String to check
     * @return  <code>true</code> if argument is numeric
     */
    private static boolean isNumeric(NumberFormat formatter, String inputData) {
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(inputData, pos);
        return inputData.length() == pos.getIndex();
    }

    /**
     * Check if a string represents an integer
     * @param inputData String to check
     * @return  <code>true</code> if argument is numeric
     */
    public static boolean isInteger(String inputData) {
        DecimalFormat formatter = new DecimalFormat();
        formatter.setParseIntegerOnly(true);
        return isNumeric(formatter, inputData);
    }

    /**
     * Make a copy of an array with some elements removed
     * @param original      Original array
     * @param start         Splice start index (inclusive)
     * @param deleteCount   Number of elements to remove
     * @param newType       Array type of new array
     * @param <T>           Type of original array element
     * @param <U>           Type of new array element
     * @return  New array
     * @throws NullPointerException	        if original is null
     * @throws IllegalArgumentException	    if deleteCount < 0
     * @throws NegativeArraySizeException   if size of new array would be < 0
     */
    public static <T, U> T[] splice(U[] original, int start, int deleteCount, Class<? extends T[]> newType) {
        if (original == null) {
            throw new NullPointerException("No array argument");
        }
        if (deleteCount < 0) {
            throw new IllegalArgumentException("Negative delete count");
        }
        int newLength = original.length - deleteCount;
        if (newLength < 0) {
            throw new NegativeArraySizeException("Negative new length");
        }

        int from = 0;
        if (start == 0) {
            from = deleteCount;
        }
        int to = from + newLength;

        T[] result = Arrays.copyOfRange(original, from, to, newType);

        if (start > 0) {
            from = start + deleteCount;
            int toCopy = newLength - start;
            if (toCopy > 0) {
                System.arraycopy(original, from, result, start, toCopy);
            }
        }
        return result;
    }

    /**
     * Check if an array has a size greater than zero
     * @param array Array to check
     * @param <U>   the type of the array
     * @return  <code>true</code> if array has size > 0
     */
    public static <U> boolean arrayHasSize(U[] array) {
        return arrayHasSize(array, 1);
    }

    /**
     * Check if an array has a size greater than or equal to the specified size
     * @param array Array to check
     * @param size  The min size to check for
     * @param <U>   the type of the array
     * @return  <code>true</code> if array has size > 0
     */
    public static <U> boolean arrayHasSize(U[] array, int size) {
        return (array != null) && (array.length >= size);
    }

    /**
     * Check if the item at the index of an array is null
     * @param array Array to check
     * @param index Index if item to check
     * @param <U>   the type of the array
     * @return  <code>true</code> if item is null or index out of range
     */
    public static <U> boolean arrayItemIsNull(U[] array, int index) {
        boolean isNull = true;
        if (arrayHasSize(array)) {
            if ((index >= 0) && (index < array.length)) {
                isNull = (array[index] == null);
            }
        }
        return isNull;
    }

    /**
     * Determine the size of a bundle
     * @param bundle    To to determine size of
     * @return  Bundle size in bytes
     */
    public static int getBundleSizeInBytes(Bundle bundle) {
        Parcel parcel = Parcel.obtain();
        parcel.writeValue(bundle);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes.length;
    }

}
