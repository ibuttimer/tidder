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

import static org.junit.Assert.*;

public class UtilsTest {


    @Test
    public void spliceTest() {

        String[] original = new String[] {
                "str0", "str1", "str2", "str3", "str4", "str5", "str6"
        };

        int start = 0;
        int deleteCount = 2;
        String[] result = Utils.splice(original, start, deleteCount, String[].class);
        assertArrays(original, result, start, deleteCount);

        start = 3;
        deleteCount = 3;
        result = Utils.splice(original, start, deleteCount, String[].class);
        assertArrays(original, result, start, deleteCount);

        start = 3;
        deleteCount = 6;
        result = Utils.splice(original, start, deleteCount, String[].class);
        assertArrays(original, result, start, deleteCount);

        start = 0;
        deleteCount = 0;
        result = Utils.splice(original, start, deleteCount, String[].class);
        assertArrays(original, result, start, deleteCount);

        start = 1;
        deleteCount = 0;
        result = Utils.splice(original, start, deleteCount, String[].class);
        assertArrays(original, result, start, deleteCount);


    }

    public void assertArrays(String[] original, String[] result, int start, int deleteCount) {
        int expectedLength = original.length - deleteCount;
        assertEquals("Unexpected length: " + result.length + " not " + expectedLength, result.length, expectedLength);
        for (int i = 0, j = start + deleteCount; i < expectedLength; i++) {
            if (i < start) {
                assertEquals("Unexpected entry at index " + i, result[i], original[i]);
            } else {
                assertEquals("Unexpected entry at index " + i, result[i], original[j]);
                ++j;
            }
        }
    }

}