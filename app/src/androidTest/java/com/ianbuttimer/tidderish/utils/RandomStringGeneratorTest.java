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

import org.junit.Test;

import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.ALPHA;
import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.ALPHA_NUMERIC;
import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.ALL_CATEGORIES;
import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.LOWERCASE;
import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.NUMERIC;
import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.PUNCTUATION;
import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.UPPERCASE;
import static com.ianbuttimer.tidderish.utils.RandomStringGenerator.WHITESPACE;
import static org.junit.Assert.*;

/**
 * Random string generator unit test
 */
public class RandomStringGeneratorTest {

    private int NUM_LENGTH = 10 * 2;
    private int ALPHA_LENGTH = 26 * 2;
    private int ALPHA_2_LENGTH = ALPHA_LENGTH * 2;
    private int ALPHANUM_LENGTH = NUM_LENGTH + ALPHA_2_LENGTH;
    private int ALPHANUM_WHITE_LENGTH = ALPHANUM_LENGTH + NUM_LENGTH;
    private int ALL_LENGTH = (0x7e - 0x20 + 1) * 3;

    @Test
    public void generate() throws Exception {
        String string = RandomStringGenerator.generate(NUM_LENGTH, NUMERIC);
        int length = string.length();
        assertEquals("Incorrect length", length, NUM_LENGTH);
        for (int i = 0; i < length; i++) {
            assertTrue("Not a digit", Character.isDigit(string.charAt(i)));
        }

        string = RandomStringGenerator.generate(ALPHA_LENGTH, LOWERCASE);
        length = string.length();
        assertEquals("Incorrect length", length, ALPHA_LENGTH);
        for (int i = 0; i < length; i++) {
            assertTrue("Not lowercase", Character.isLowerCase(string.charAt(i)));
        }

        string = RandomStringGenerator.generate(ALPHA_LENGTH, UPPERCASE);
        length = string.length();
        assertEquals("Incorrect length", length, ALPHA_LENGTH);
        for (int i = 0; i < length; i++) {
            assertTrue("Not uppercase", Character.isUpperCase(string.charAt(i)));
        }

        string = RandomStringGenerator.generate(NUM_LENGTH, WHITESPACE);
        length = string.length();
        assertEquals("Incorrect length", length, NUM_LENGTH);
        for (int i = 0; i < length; i++) {
            assertTrue("Not whitespace", Character.isWhitespace(string.charAt(i)));
        }

        string = RandomStringGenerator.generate(ALPHA_LENGTH, PUNCTUATION);
        length = string.length();
        assertEquals("Incorrect length", length, ALPHA_LENGTH);
        for (int i = 0; i < length; i++) {
            assertTrue("Not punctuation", isPunctuation(string.charAt(i)));
        }

        string = RandomStringGenerator.generate(ALPHA_2_LENGTH, ALPHA);
        length = string.length();
        assertEquals("Incorrect length", length, ALPHA_2_LENGTH);
        for (int i = 0; i < length; i++) {
            assertTrue("Not alpha", Character.isLetter(string.charAt(i)));
        }

        string = RandomStringGenerator.generate(ALPHANUM_LENGTH, ALPHA_NUMERIC);
        length = string.length();
        assertEquals("Incorrect length", length, ALPHANUM_LENGTH);
        for (int i = 0; i < length; i++) {
            assertTrue("Not alphanumeric", Character.isLetterOrDigit(string.charAt(i)));
        }

        string = RandomStringGenerator.generate(ALPHANUM_WHITE_LENGTH, ALPHA_NUMERIC|WHITESPACE);
        length = string.length();
        assertEquals("Incorrect length", length, ALPHANUM_WHITE_LENGTH);
        for (int i = 0; i < length; i++) {
            char chr = string.charAt(i);
            assertTrue("Not alphanumeric/whitespace",
                    (Character.isLetterOrDigit(chr) || Character.isWhitespace(chr)));
        }

        string = RandomStringGenerator.generate(ALL_LENGTH, ALL_CATEGORIES);
        length = string.length();
        assertEquals("Incorrect length", length, ALL_LENGTH);
        for (int i = 0; i < length; i++) {
            char chr = string.charAt(i);
            assertTrue("Not alphanumeric/whitespace",
                    (Character.isLetterOrDigit(chr) || Character.isWhitespace(chr) || isPunctuation(chr)));
        }


    }


    private boolean isPunctuation(char chr) {
        boolean punctuation;
        switch (Character.getType(chr)) {
            case Character.CONNECTOR_PUNCTUATION:
            case Character.DASH_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.CURRENCY_SYMBOL:
            case Character.MODIFIER_SYMBOL:
            case Character.MATH_SYMBOL:
                punctuation = true;
                break;
            default:
                punctuation = false;
                break;
        }
        return punctuation;
    }

}