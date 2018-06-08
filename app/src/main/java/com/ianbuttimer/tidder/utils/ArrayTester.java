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

import android.support.annotation.Nullable;
import android.util.Pair;

import com.ianbuttimer.tidder.data.ITester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to provide array testing functionality
 */

public class ArrayTester<T> {

    private List<T> mList;

    /**
     * Default constructor
     */
    public ArrayTester() {
        this((List<T>) null);
    }

    /**
     * Constructor
     * @param list     List to test
     */
    public ArrayTester(List<T> list) {
        setList(list);
    }

    /**
     * Constructor
     * @param list     Array to test
     */
    public ArrayTester(T... list) {
        setList(list);
    }

    /**
     * Set the list to test
     * @param list
     */
    public void setList(List<T> list) {
        this.mList = list;
    }

    /**
     * Set the list to test
     * @param list     Array to test
     */
    public void setList(T... list) {
        this.mList = Arrays.asList(list);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester        Tester to use to find required data
     * @param fromIndex	    Index of the first element, <b>inclusive</b>
     * @param toIndex	    Index of the last element, <b>exclusive</b>
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     * @throws IllegalArgumentException    if fromIndex >= toIndex
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public Pair<T, Integer> findItemAndIndex(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = notFound();
        if ((mList != null) && !mList.isEmpty()) {
            int length = mList.size();
            if (fromIndex >= toIndex) {
                throw new IllegalArgumentException("From index >= to index, cannot move forward");
            }
            if (fromIndex < 0) {
                throw new ArrayIndexOutOfBoundsException("From index before start");
            }
            if (toIndex > length) {
                throw new ArrayIndexOutOfBoundsException("To index after end");
            }
            result = findItemAndIndexInt(tester, fromIndex, Math.min(length, toIndex) - 1);
        }
        return result;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester        Tester to use to find required data
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     */
    public Pair<T, Integer> findItemAndIndex(ITester<T> tester) {
        return findItemAndIndex(tester, 0, mList.size());
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester        Tester to use to find required data
     * @param fromIndex	    Index of the first element, inclusive
     * @param toIndex	    Index of the last element, inclusive
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     */
    private Pair<T, Integer> findItemAndIndexInt(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = notFound();
        for (int cnt = 0, ll = Math.abs(fromIndex - toIndex) + 1; cnt < ll; cnt++) {
            int pos;
            if (fromIndex > toIndex) {
                pos = fromIndex - cnt;
            } else {
                pos = fromIndex + cnt;
            }
            T toTest = mList.get(pos);
            if (tester.test(toTest)) {
                result = Pair.create(toTest, pos);
                break;
            }
        }
        return result;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @return A Pair with which contains null & -1;
     */
    private Pair<T, Integer> notFound() {
        return Pair.create(null, -1);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @param toIndex	Index of the last element, exclusive
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws IllegalArgumentException    if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    @Nullable public T findItem(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = findItemAndIndex(tester, fromIndex, toIndex);
        return result.first;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    @Nullable public T findItem(ITester<T> tester, int fromIndex) {
        T item = null;
        if (mList != null) {
            item = findItem(tester, fromIndex, mList.size());
        }
        return item;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The data matching the test criteria, or <code>null</code> if not found
     */
    @Nullable public T findItem(ITester<T> tester) {
        return findItem(tester, 0);
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @param toIndex	Index of the last element, exclusive
     * @return The index of the data, or -1 if nothing is found
     * @throws IllegalArgumentException    if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public int findItemIndex(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = findItemAndIndex(tester, fromIndex, toIndex);
        return result.second;
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @return The index of the data, or -1 if nothing is found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public int findItemIndex(ITester<T> tester, int fromIndex) {
        int index = -1;
        if (mList != null) {
            index = findItemIndex(tester, fromIndex, mList.size());
        }
        return index;
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The index of the data, or -1 if nothing is found
     */
    public int findItemIndex(ITester<T> tester) {
        return findItemIndex(tester, 0);
    }

    /**
     * Find the data item in the data set matching the test criteria, by searching in reverse order
     * @param tester        Tester to use to find required data
     * @param fromIndex	    Index of the first element, <b>exclusive</b>
     * @param toIndex	    Index of the last element, <b>inclusive</b>
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     * @throws IllegalArgumentException    if toIndex >= fromIndex
     * @throws ArrayIndexOutOfBoundsException    if toIndex < 0 or fromIndex > a.length
     */
    public Pair<T, Integer> findItemAndIndexReverse(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = notFound();
        if ((mList != null) && !mList.isEmpty()) {
            if (toIndex >= fromIndex) {
                throw new IllegalArgumentException("To index >= from index, cannot move backward");
            }
            if (toIndex < 0) {
                throw new ArrayIndexOutOfBoundsException("To index before start");
            }
            int length = mList.size();
            if (fromIndex > length) {
                throw new ArrayIndexOutOfBoundsException("From index after end");
            }
            result = findItemAndIndexInt(tester, fromIndex - 1, toIndex);
        }
        return result;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @param toIndex	Index of the last element, <b>inclusive</b>
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws IllegalArgumentException    if toIndex >= fromIndex
     * @throws ArrayIndexOutOfBoundsException    if toIndex < 0 or fromIndex > a.length
     */
    @Nullable public T findItemReverse(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = findItemAndIndexReverse(tester, fromIndex, toIndex);
        return result.first;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex > a.length
     */
    @Nullable public T findItemReverse(ITester<T> tester, int fromIndex) {
        T item = null;
        if (mList != null) {
            item = findItemReverse(tester, fromIndex, 0);
        }
        return item;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The data matching the test criteria, or <code>null</code> if not found
     */
    @Nullable public T findItemReverse(ITester<T> tester) {
        T item = null;
        if (mList != null) {
            item = findItemReverse(tester, mList.size());
        }
        return item;
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @param toIndex	Index of the last element, <b>inclusive</b>
     * @return The index of the data, or -1 if nothing is found
     * @throws IllegalArgumentException    if toIndex >= fromIndex
     * @throws ArrayIndexOutOfBoundsException    if toIndex < 0 or fromIndex > a.length
     */
    public int findItemIndexReverse(ITester<T> tester, int fromIndex, int toIndex) {
        Pair<T, Integer> result = findItemAndIndexReverse(tester, fromIndex, toIndex);
        return result.second;
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @return The index of the data, or -1 if nothing is found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex > a.length
     */
    public int findItemIndexReverse(ITester<T> tester, int fromIndex) {
        int index = -1;
        if (mList != null) {
            index = findItemIndexReverse(tester, fromIndex, 0);
        }
        return index;
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The index of the data, or -1 if nothing is found
     */
    public int findItemIndexReverse(ITester<T> tester) {
        int index = -1;
        if (mList != null) {
            index = findItemIndexReverse(tester, mList.size());
        }
        return index;
    }

    /**
     * Create a sub list of the entries which satisfy the specified tester
     * @param tester    Tester to use to generate sublist
     * @return  Sub list
     */
    public List<T> subList(ITester<T> tester) {
        return subList(tester, new ArrayList<T>());
    }

    /**
     * Create a sub list of the entries which satisfy the specified tester
     * @param tester    Tester to use to generate sublist
     * @param subList   List to add sub list to
     * @return  Sub list
     */
    public List<T> subList(ITester<T> tester, List<T> subList) {
        if (mList != null) {
            for (T toTest : mList) {
                if (tester.test(toTest)) {
                    subList.add(toTest);
                }
            }
        }
        return subList;
    }

}
