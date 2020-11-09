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

import androidx.core.util.Pair;

import java.util.Random;

/**
 * A class to generate simple random strings
 */
@SuppressWarnings("unused")
public class RandomStringGenerator {

    public static final int NUMERIC = 0x01;     // ascii 0x30~0x39
    public static final int LOWERCASE = 0x02;   // ascii 0x61~0x7a
    public static final int UPPERCASE = 0x04;   // ascii 0x41~0x5a
    public static final int WHITESPACE = 0x08;  // ascii 0x20
    public static final int PUNCTUATION = 0x10; // ascii 0x21~0x7e excluding ranges covered by DIGIT, LOWERCASE & UPPERCASE
    public static final int ALPHA = LOWERCASE | UPPERCASE;
    public static final int ALPHA_NUMERIC = ALPHA | NUMERIC;
    public static final int ALL_CATEGORIES = 0x1f;

    public static final int NUMERIC_START = 0x30;
    public static final int NUMERIC_END = 0x39;
    public static final int LOWERCASE_START = 0x61;
    public static final int LOWERCASE_END = 0x7a;
    public static final int UPPERCASE_START = 0x41;
    public static final int UPPERCASE_END = 0x5a;
    public static final int WHITESPACE_START = 0x20;
    public static final int WHITESPACE_END = 0x20;
    public static final int PUNCTUATION_START = 0x21;
    public static final int PUNCTUATION_END = 0x7e;

    private static Picker mNumericPicker;
    private static Picker mLowercasePicker;
    private static Picker mUppercasePicker;
    private static Picker mWhiteSpacePicker;
    private static Picker mRunctationPicker;

    private int mLength;            // length of string to generate
    private int mCharCategories;    // character categories to use


    /**
     * Constructor
     * @param length            length of string to generate
     * @param charCategories    character categories to use
     */
    public RandomStringGenerator(int length, int charCategories) {
        this.mLength = length;
        this.mCharCategories = charCategories;
    }


    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        this.mLength = length;
    }

    public int getCharCategories() {
        return mCharCategories;
    }

    public void setCharCategories(int charCategories) {
        this.mCharCategories = charCategories;
    }

    /**
     * Generate a random string
     * @return  String of specified length
     */
    public String generate() {
        return generate(mLength, mCharCategories);
    }

    /**
     * Generate a random string
     * @param length            length of string to generate
     * @param charCategories    character categories to use
     * @return  String of specified length, or an empty string if invalid arguments were provided
     */
    public static String generate(int length, int charCategories) {
        if ((length <= 0) || ((charCategories & ALL_CATEGORIES) == 0)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Random rndCategory = new Random();
        Random rndIndex = new Random();
        Picker[] pickerArray = getPickerArray(charCategories);

        for (int i = 0; i < length; i++) {
            int category = rndCategory.nextInt(pickerArray.length);
            char[] cc = Character.toChars(pickerArray[category].get(rndIndex));
            sb.append(cc);
        }
        return sb.toString();
    }

    /**
     * Get the array of Pickers to use to generate a string
     * @param charCategories    Character categories
     * @return  array of Pickers
     */
    private static Picker[] getPickerArray(int charCategories) {
        int bits = 0;
        for (int mask = NUMERIC; (mask & ALL_CATEGORIES) != 0; mask <<= 1) {
            if ((mask & charCategories) != 0) {
                ++bits;
            }
        }
        Picker[] array = new Picker[bits];
        for (int mask = NUMERIC, i = 0; (mask & ALL_CATEGORIES) != 0; mask <<= 1) {
            Picker picker;
            switch (mask & charCategories) {
                case NUMERIC:
                    if (mNumericPicker == null) {
                        mNumericPicker = new RangePicker(NUMERIC_START, NUMERIC_END);
                    }
                    picker = mNumericPicker;
                    break;
                case LOWERCASE:
                    if (mLowercasePicker == null) {
                        mLowercasePicker = new RangePicker(LOWERCASE_START, LOWERCASE_END);
                    }
                    picker = mLowercasePicker;
                    break;
                case UPPERCASE:
                    if (mUppercasePicker == null) {
                        mUppercasePicker = new RangePicker(UPPERCASE_START, UPPERCASE_END);
                    }
                    picker = mUppercasePicker;
                    break;
                case WHITESPACE:
                    if (mWhiteSpacePicker == null) {
                        mWhiteSpacePicker = new RangePicker(WHITESPACE_START, WHITESPACE_END);
                    }
                    picker = mWhiteSpacePicker;
                    break;
                case PUNCTUATION:
                    if (mRunctationPicker == null) {
                        Pair<Integer, Integer>[] exclusions = new Pair[] {
                                new Pair<>(NUMERIC_START, NUMERIC_END),
                                new Pair<>(LOWERCASE_START, LOWERCASE_END),
                                new Pair<>(UPPERCASE_START, UPPERCASE_END),
                        };
                        mRunctationPicker = new ExcludeRangePicker(
                                                    PUNCTUATION_START, PUNCTUATION_END, exclusions);
                    }
                    picker = mRunctationPicker;
                    break;
                default:
                    picker = null;
                    break;
            }
            if (picker != null) {
                array[i++] = picker;
            }
        }
        return array;
    }

    /**
     * Base character picker class
     */
    static abstract class Picker {
        int start;  // start of range
        int end;    // end of range
        int bound;  // num of chars in range
        Pair<Integer, Integer> exclusions[];    // start/end excluded ranges

        Picker(int start, int end, Pair<Integer, Integer>[] exclusions) {
            this.start = start;
            this.end = end;
            this.exclusions = exclusions;
            this.bound = end - start + 1;
            if (Utils.arrayHasSize(exclusions)) {
                for (Pair<Integer, Integer> exclude: exclusions) {
                    if (exclude != null) {
                        // noinspection ConstantConditions
                        this.bound -= (exclude.second - exclude.first + 1);
                    }
                }
            }
        }

        boolean isExcluded(int num) {
            boolean excluded = false;
            if (Utils.arrayHasSize(exclusions)) {
                for (int i = 0; i < exclusions.length; ++i) {
                    // noinspection ConstantConditions
                    if ((num >= exclusions[i].first) && (num <= exclusions[i].second)) {
                        excluded = true;
                        break;
                    }
                }
            }
            return excluded;
        }

        abstract int get(Random rnd);
    }

    /**
     * Character picker class for range with no exclusions
     */
    static class RangePicker extends Picker {

        RangePicker(int start, int end) {
            super(start, end, null);
        }

        @Override
        int get(Random rnd) {
            int index = rnd.nextInt(bound);
            return start + index;
        }
    }

    /**
     * Character picker class for range with exclusions
     */
    static class ExcludeRangePicker extends Picker {

        int[] list; // list of allowed chars

        ExcludeRangePicker(int start, int end, Pair<Integer, Integer>[] exclusions) {
            super(start, end, exclusions);
            list = new int[bound];

            for (int i = 0, num = start; (i < bound) && (num <= end); ++num) {
                if (!isExcluded(num)) {
                    list[i++] = num;
                }
            }
        }

        @Override
        int get(Random rnd) {
            int index = rnd.nextInt(bound);
            return list[index];
        }
    }
}
