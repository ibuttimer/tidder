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
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;

/**
 * Colour-related utility functions
 */

public class ColourUtils {

    /** Value for fully transparent */
    public static final float TRANSPARENT = 0f;
    /** Value for fully opaque */
    public static final float OPAQUE = 1f;

    @ColorInt public static final int[] APP_COLOURS;

    static {
        Context content = TidderApplication.getWeakApplicationContext().get();
        Resources resources = content.getResources();

        @ColorRes int[] resIds = new int[] {
                R.color.colorPrimary, R.color.colorPrimaryLight, R.color.colorPrimaryDark,
                R.color.colorAccent, R.color.colorAccentLight, R.color.colorAccentDark
        };
        int len = resIds.length;
        @ColorInt int[] colours = new int[len];
        for (int i = 0; i < len; i++) {
            colours[i] = resources.getColor(resIds[i]);
        }
        APP_COLOURS = colours;
    }

    /**
     * Determine which colour in the specified array is the furthest euclidean distance from the base colour
     * @param base      Base colour
     * @param array     Array of colours to test
     * @return  Colour from array with greatest euclidean distance
     */
    @ColorInt
    public static int getFurthestColour(@ColorInt int base, @ColorInt int[] array) {
        @ColorInt int result = base;
        if ((array != null) && (array.length > 0)) {
            double distance = 0d;
            double[] baseLab = new double[3];
            double[] testLab = new double[3];

            // convert to CIE Lab representative components
            ColorUtils.RGBToLAB(
                    Color.red(base), Color.green(base), Color.blue(base),
                    baseLab);

            for (int colour : array) {
                ColorUtils.RGBToLAB(
                        Color.red(colour), Color.green(colour), Color.blue(colour),
                        testLab);
                double testDist = ColorUtils.distanceEuclidean(baseLab, testLab);
                if (testDist > distance) {
                    distance = testDist;
                    result = colour;
                }
            }
        }
        return result;
    }

    /**
     * Determine which colour in the specified array is the furthest euclidean distance from the base colour
     * @param base      Base colour
     * @param palette   Palette to select colour from
     * @return  Colour from array with greatest euclidean distance
     */
    @ColorInt public static int getFurthestColour(@ColorInt int base, Palette palette) {
        @ColorInt int[] array = new int[] {
                palette.getMutedColor(base),
                palette.getDarkMutedColor(base),
                palette.getLightMutedColor(base),
                palette.getVibrantColor(base),
                palette.getDarkVibrantColor(base),
                palette.getLightVibrantColor(base)
        };
        return getFurthestColour(base, array);
    }


    /**
     * Convert a colour to the equivalent YIQ value
     * @param colour    Colour to convert
     * @return  YIQ value
     * @see <a href="https://www.w3.org/TR/AERT/#color-contrast">W3C test color attributes</a>
     * @see <a href="https://en.wikipedia.org/wiki/YIQ">YIQ</a>
     */
    public static double colorToYIQ(@ColorInt int colour) {
        return ((299 * (double)Color.red(colour))
                + (587 * (double)Color.green(colour))
                + (114 * Color.blue(colour))) / 1000;
    }

    /**
     * Calculate the best contrasting colour for the specified colour
     * @param colour    Colour to contrast
     * @return  Contrast colour
     * @see <a href="https://stackoverflow.com/a/13030061/4054609">Reverse opposing colors</a>
     */
    @ColorInt public static int getContrastColor(@ColorInt int colour) {
        return (colorToYIQ(colour) >= 128 ? Color.BLACK : Color.WHITE);
    }

}
