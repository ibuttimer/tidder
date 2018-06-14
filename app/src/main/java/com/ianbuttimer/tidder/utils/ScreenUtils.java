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
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.annotation.Dimension;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ianbuttimer.tidder.utils.annotation.LayoutDirection;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * This class contains screen related utility functions
 */
@SuppressWarnings("unused")
public class ScreenUtils {

    /**
     * Private constructor
     */
    private ScreenUtils() {
        // can't instantiate class
    }

    /**
     * Get the screen metrics.
     * @param activity  The current activity
     * @return  screen metrics
     */
    public static DisplayMetrics getScreenMetrics(Activity activity) {
        return getScreenMetrics(activity.getWindowManager());
    }

    /**
     * Get the display metrics.
     * @param manager  The current window manager
     * @return  screen metrics
     */
    private static DisplayMetrics getScreenMetrics(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * Get the available screen size in pixels.
     * @param activity  The current activity
     * @return  screen size
     */
    public static Point getScreenSize(Activity activity) {
        DisplayMetrics metrics = getScreenMetrics(activity);
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * Get the available screen width in pixels.
     * @param activity  The current activity
     * @return  screen width
     */
    public static int getScreenWidth(Activity activity) {
        Point size = getScreenSize(activity);
        return size.x;
    }

    /**
     * Get the available screen height in pixels.
     * @param activity  The current activity
     * @return  screen height
     */
    public static int getScreenHeight(Activity activity) {
        Point size = getScreenSize(activity);
        return size.y;
    }

    /**
     * Get the available screen size in density-independent pixels.
     * @param activity  The current activity
     * @return  screen size
     */
    public static Point getScreenDp(Activity activity) {
        DisplayMetrics metrics = getScreenMetrics(activity);
        float dpWidth = metrics.widthPixels / metrics.density;
        float dpHeight = metrics.heightPixels / metrics.density;
        return new Point(Float.valueOf(dpWidth).intValue(), Float.valueOf(dpHeight).intValue());
    }

    /**
     * Get the available screen width in density-independent pixels.
     * @param activity  The current activity
     * @return  screen width
     */
    public static int getScreenDpWidth(Activity activity) {
        Point size = getScreenDp(activity);
        return size.x;
    }

    /**
     * Get the available screen height in density-independent pixels.
     * @param activity  The current activity
     * @return  screen height
     */
    public static int getScreenDpHeight(Activity activity) {
        Point size = getScreenDp(activity);
        return size.y;
    }

    /**
     * Convert between density-independent pixels & pixels
     * @param context   The current context
     * @param in        Value to convert
     * @param dimen     Value unit
     * @return  pixel size
     */
    private static int convertBetweenDpAndPixels(Context context, int in, @Dimension int dimen) {
        DisplayMetrics metrics = getScreenMetrics((WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        float out;
        switch (dimen) {
            case Dimension.DP:
                out = in * metrics.density;
                break;
            case Dimension.PX:
                out = in / metrics.density;
                break;
            default:
                out = 0;
                break;
        }
        return Float.valueOf(out).intValue();
    }

    /**
     * Convert density-independent pixels to pixels
     * @param context   The current context
     * @param dp        Dp to convert
     * @return  pixel size
     */
    public static int convertDpToPixels(Context context, int dp) {
        return convertBetweenDpAndPixels(context, dp, Dimension.DP);
    }

    /**
     * Convert pixels to density-independent pixels
     * @param context   The current context
     * @param pixels    Pixels to convert
     * @return  pixel size
     */
    public static int convertPixelsToDp(Context context, int pixels) {
        return convertBetweenDpAndPixels(context, pixels, Dimension.PX);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isSize(Context context, int size) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= size;
    }

    /**
     * Determine if the device has an extra-large screen, i.e. at least approximately 720x960 dp units
     * @param context   The current context
     * @return <code>true</code> if device has an extra-large screen, <code>false</code> otherwise
     */
    public static boolean isXLargeScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    /**
     * Determine if the device has a large screen, i.e. at least approximately 480x640 dp units
     * @param context   The current context
     * @return <code>true</code> if device has a large screen, <code>false</code> otherwise
     */
    public static boolean isLargeScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_LARGE);
    }

    /**
     * Determine if the device has a normal screen, i.e. at least approximately 320x470 dp units
     * @param context   The current context
     * @return <code>true</code> if device has a normal screen, <code>false</code> otherwise
     */
    public static boolean isNormalScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_NORMAL);
    }

    /**
     * Determine if the device has a small screen, i.e. at least approximately 320x426 dp units
     * @param context   The current context
     * @return <code>true</code> if device has a small screen, <code>false</code> otherwise
     */
    public static boolean isSmallScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_SMALL);
    }

    /**
     * Determine if the device screen is in portrait orientation
     * @param context   The current context
     * @return <code>true</code> if screen is in portrait orientation, <code>false</code> otherwise
     */
    public static boolean isPotraitScreen(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    /**
     * Determine if the device screen width is at least the specified number of pixels
     * @param activity  The current activity
     * @param width     Width to test in density-independent pixels
     * @return <code>true</code> if screen width at least specified size, <code>false</code> otherwise
     */
    public static boolean isScreenWidth(Activity activity, int width) {
        return (getScreenDpWidth(activity) >= width);
    }

    /**
     * Determine if the device screen height is at least the specified number of pixels
     * @param activity  The current activity
     * @param height    Height to test in density-independent pixels
     * @return <code>true</code> if screen width at least specified size, <code>false</code> otherwise
     */
    public static boolean isScreenHeight(Activity activity, int height) {
        return (getScreenDpHeight(activity) >= height);
    }

    /**
     * Return the layout direction.
     * @param context   The current context
     * @return  Returns either LAYOUT_DIRECTION_LTR or LAYOUT_DIRECTION_RTL.
     */
    @LayoutDirection
    public static int getScreenDirection(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.getLayoutDirection();
    }

    /**
     * Check if the current screen direction is RTL
     * @param context   The current context
     * @return  <code>true</code> if screen direction is RTL
     */
    public static boolean isScreenDirectionRtl(Context context) {
        return (getScreenDirection(context) == View.LAYOUT_DIRECTION_RTL);
    }

    /**
     * Check if the current screen direction is LTR
     * @param context   The current context
     * @return  <code>true</code> if screen direction is LTR
     */
    public static boolean isScreenDirectionLtr(Context context) {
        return (getScreenDirection(context) == View.LAYOUT_DIRECTION_LTR);
    }

    /**
     * Set the size of a Dialog
     * @param dialog    Dialog to size
     * @param width     Required width; WRAP_CONTENT/MATCH_PARENT, 0 < width <= 1.0 implies percent, or width > 1 implies pixel size
     * @param height    Required height; WRAP_CONTENT/MATCH_PARENT, 0 < height <= 1.0 implies percent, or height > 1 implies pixel size
     * @param gravity   Gravity, only relevant if width or height is WRAP_CONTENT
     */
    public static void setDialogSize(android.app.Dialog dialog, float width, float height, int gravity) {
        Window window = dialog.getWindow();
        Point size = ScreenUtils.getScreenSize(dialog.getOwnerActivity());
        if (window != null) {
            int iWidth = getDialogDim(width, size.x);
            int iHeight = getDialogDim(height, size.y);

            window.setLayout(iWidth, iHeight);
            if ((iWidth == WRAP_CONTENT) || (iHeight == WRAP_CONTENT)) {
                // gravity only relevant if wrap content
                window.setGravity(gravity);
            }
        }
    }

    private static int getDialogDim(float dimen, int screen) {
        int iDimen;
        if ((dimen > 0f) && (dimen <= 1.0f)) {
            // percent of screen size
            iDimen = (int) (screen * dimen);
        } else {
            // < 0 implies WRAP_CONTENT/MATCH_PARENT or > 1 implies constant
            iDimen = Float.valueOf(dimen).intValue();
        }
        return iDimen;
    }
}
