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

import android.util.Pair;

import com.ianbuttimer.tidder.data.ITester;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayTesterTest {

    String[] array = new String[] {
            "str0", "str1", "str2", "str3", "str4", "str5", "str6"
    };
    String notInArray = "xyz";

    Pair<String, Integer> notFound = Pair.create(null, -1);

    class StringTester implements ITester<String> {
        String str;

        public StringTester() {
            this.str = "";
        }

        public void setStr(String str) {
            this.str = str;
        }

        @Override
        public boolean test(String obj) {
            return str.equals(obj);
        }
    }

    StringTester tester = new StringTester();
    ArrayTester<String> arrayTester = new ArrayTester<>(array);

    @Test
    public void findItemAndIndex() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        Pair<String, Integer> result = arrayTester.findItemAndIndex(tester, 0, array.length);
        assertResult(result, index);

        result = arrayTester.findItemAndIndex(tester, index + 1, array.length);
        assertNotFound(result);

        tester.setStr(notInArray);
        result = arrayTester.findItemAndIndex(tester, 0, array.length);
        assertNotFound(result);
    }

    @Test
    public void findItem() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        String result = arrayTester.findItem(tester, 0, array.length);
        assertEquals("Incorrect item", result, array[index]);

        result = arrayTester.findItem(tester, index + 1, array.length);
        assertNull("Incorrect item", result);

        tester.setStr(notInArray);
        result = arrayTester.findItem(tester, 0, array.length);
        assertNull("Incorrect item", result);
    }

    @Test
    public void findItem1() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        String result = arrayTester.findItem(tester, 0);
        assertEquals("Incorrect item", result, array[index]);

        result = arrayTester.findItem(tester, index + 1);
        assertNull("Incorrect item", result);

        tester.setStr(notInArray);
        result = arrayTester.findItem(tester, 0);
        assertNull("Incorrect item", result);
    }

    @Test
    public void findItem2() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        String result = arrayTester.findItem(tester);
        assertEquals("Incorrect item", result, array[index]);

        tester.setStr(notInArray);
        result = arrayTester.findItem(tester);
        assertNull("Incorrect item", result);
    }

    @Test
    public void findItemIndex() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        int result = arrayTester.findItemIndex(tester, 0, array.length);
        assertEquals("Incorrect index", result, index);

        result = arrayTester.findItemIndex(tester, index + 1, array.length);
        assertEquals("Incorrect index", result, (int)notFound.second);

        tester.setStr(notInArray);
        result = arrayTester.findItemIndex(tester, 0, array.length);
        assertEquals("Incorrect index", result, (int)notFound.second);
    }

    @Test
    public void findItemIndex1() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        int result = arrayTester.findItemIndex(tester, 0);
        assertEquals("Incorrect index", result, index);

        result = arrayTester.findItemIndex(tester, index + 1);
        assertEquals("Incorrect index", result, (int)notFound.second);

        tester.setStr(notInArray);
        result = arrayTester.findItemIndex(tester, 0);
        assertEquals("Incorrect index", result, (int)notFound.second);
    }

    @Test
    public void findItemIndex2() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        int result = arrayTester.findItemIndex(tester);
        assertEquals("Incorrect index", result, index);

        tester.setStr(notInArray);
        result = arrayTester.findItemIndex(tester);
        assertEquals("Incorrect index", result, (int)notFound.second);
    }

    @Test
    public void findItemAndIndexReverse() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        Pair<String, Integer> result = arrayTester.findItemAndIndexReverse(tester, array.length, 0);
        assertResult(result, index);

        result = arrayTester.findItemAndIndexReverse(tester, array.length, index + 1);
        assertNotFound(result);

        tester.setStr(notInArray);
        result = arrayTester.findItemAndIndexReverse(tester, array.length, 0);
        assertNotFound(result);
    }

    @Test
    public void findItemReverse() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        String result = arrayTester.findItemReverse(tester, array.length, 0);
        assertEquals("Incorrect item", result, array[index]);

        result = arrayTester.findItemReverse(tester, array.length, index + 1);
        assertNull("Incorrect item", result);

        tester.setStr(notInArray);
        result = arrayTester.findItemReverse(tester, array.length, 0);
        assertNull("Incorrect item", result);
    }

    @Test
    public void findItemReverse1() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        String result = arrayTester.findItemReverse(tester);
        assertEquals("Incorrect item", result, array[index]);

        tester.setStr(notInArray);
        result = arrayTester.findItemReverse(tester);
        assertNull("Incorrect item", result);
    }

    @Test
    public void findItemReverse2() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        String result = arrayTester.findItemReverse(tester, array.length);
        assertEquals("Incorrect item", result, array[index]);

        tester.setStr(notInArray);
        result = arrayTester.findItemReverse(tester, array.length);
        assertNull("Incorrect item", result);
    }

    @Test
    public void findItemIndexReverse() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        int result = arrayTester.findItemIndexReverse(tester, array.length, 0);
        assertEquals("Incorrect index", result, index);

        result = arrayTester.findItemIndexReverse(tester, array.length, index + 1);
        assertEquals("Incorrect index", result, (int)notFound.second);

        tester.setStr(notInArray);
        result = arrayTester.findItemIndexReverse(tester, array.length, 0);
        assertEquals("Incorrect index", result, (int)notFound.second);
    }

    @Test
    public void findItemIndexReverse1() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        int result = arrayTester.findItemIndexReverse(tester, array.length);
        assertEquals("Incorrect index", result, index);

        tester.setStr(notInArray);
        result = arrayTester.findItemIndexReverse(tester, array.length);
        assertEquals("Incorrect index", result, (int)notFound.second);
    }

    @Test
    public void findItemIndexReverse2() throws Exception {
        int index = 2;
        tester.setStr(array[index]);
        int result = arrayTester.findItemIndexReverse(tester);
        assertEquals("Incorrect index", result, index);

        tester.setStr(notInArray);
        result = arrayTester.findItemIndexReverse(tester);
        assertEquals("Incorrect index", result, (int)notFound.second);
    }

    public void assertResult(Pair<String, Integer> result, int index) {
        assertResult(result, array[index], index);
    }

    public void assertResult(Pair<String, Integer> result, String item, int index) {
        assertEquals("Incorrect item", result.first, item);
        assertEquals("Incorrect index", (long)result.second, index);
    }

    public void assertNotFound(Pair<String, Integer> result) {
        assertResult(result, notFound.first, notFound.second);
    }

}