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

package com.ianbuttimer.tidderish.data.db;

import android.database.Cursor;
import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * Class containing various database related utility methods
 */
@SuppressWarnings("unused")
public class DbUtils {

    /** SqlLite whereClause to remove all rows and get a count */
    public static final String DB_DELETE_ALL = "1";
    public static final String DB_RAW_BOOLEAN_TRUE = "1";
    public static final String DB_RAW_BOOLEAN_FALSE = "0";

    public static final Date INVALID_DATE;

    static {
        // Gregorian calendar was instituted October 15, 1582 in some countries, later in others
        Calendar cal = new GregorianCalendar(1582, Calendar.OCTOBER, 15, 0, 0, 0);
        INVALID_DATE = cal.getTime();
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    /**
     * Get a SQLite timestamp
     * @param timestamp     Date & time
     * @return  timestamp
     */
    public static String getTimestamp(Date timestamp) {
        return sdf.format(timestamp);
    }

    /**
     * Convert an SQLite timestamp to a Date
     * @param timestamp     Timestamp string
     * @return  Date object
     */
    public static Date timestampToDate(String timestamp) {
        Date date;
        try {
            date = sdf.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            date = INVALID_DATE;
        }
        return date;
    }

    /**
     * Convert an SQLite timestamp to a Date
     * @param cursor    Cursor to get timestamp from
     * @return  Date object
     */
//    public static Date timestampToDate(Cursor cursor) {
//        return timestampToDate(cursor, COLUMN_TIMESTAMP);
//    }

    /**
     * Convert an SQLite timestamp to a Date
     * @param cursor    Cursor to get timestamp from
     * @param column    Name of the column in the cursor
     * @return  Date object
     */
    public static Date timestampToDate(Cursor cursor, String column) {
        Date date;
        int idx = cursor.getColumnIndex(column);
        if (idx >= 0) {
            date = timestampToDate(cursor.getString(idx));
        } else {
            date = INVALID_DATE;
        }
        return date;
    }


    /**
     * Generate an id argument array
     * @param id    id to include
     * @return  argument array
     */
    public static String[] idArgArray(int id) {
        return new String[] {
                String.valueOf(id)
        };
    }

    /**
     * Generate an id argument array
     * @param ids   Array of ids
     * @return  argument array
     */
    public static String[] idArgArray(@NonNull int[] ids) {
        String[] arg = new String[] {};
        if (ids.length > 0) {
            int length = ids.length;
            arg = new String[length];
            for (int i = 0; i < length; i++) {
                arg[i] = String.valueOf(ids[i]);
            }
        }
        return arg;
    }






}
