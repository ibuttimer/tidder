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

package com.ianbuttimer.tidder.reddit;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Base class for a reddit kind/data object reader
 */

public abstract class KindDataReader {

    static final String RESPONSE_KIND = "kind";
    static final String RESPONSE_DATA = "data";

    static final String KIND_LISTING = "Listing";

    protected String mKind;

    public KindDataReader(String kind) {
        this.mKind = kind;
    }

    /**
     * Parse a json object
     * @param jsonReader  reader to parse
     * @throws IOException
     */
    public void parseJson(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (!parseToken(jsonReader, name)) {
                // not consumed so skip
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
    }


    protected boolean parseToken(JsonReader jsonReader, String name) throws IOException, IllegalArgumentException {
        boolean consumed = false;
        if (RESPONSE_DATA.equals(name)) {
            consumed = parseDataToken(jsonReader);
        } else if (RESPONSE_KIND.equals(name)) {
            String kind = nextString(jsonReader, "");
            consumed = mKind.equals(kind);
            if (!consumed) {
                throw new IllegalStateException("Incorrect listing type: expected " + mKind + " got " + kind);
            }
        }

        return consumed;
    }

    /**
     * Parse the data field of the json string.<br>
     * <b>Default implementation is to parse a single child object.</b>
     * @param jsonReader    Reader to use
     * @return  <code>true</code> indicating taken has been consumed
     * @throws IOException
     * @throws IllegalArgumentException
     */
    protected boolean parseDataToken(JsonReader jsonReader) throws IOException, IllegalArgumentException {
        // default implementation is to parse a single child object
        parseChildToken(jsonReader);
        return true;
    }

    /**
     * Parse a child object from the json string
     * @param jsonReader    Reader to use
     * @throws IOException
     * @throws IllegalArgumentException
     */
    protected abstract void parseChildToken(JsonReader jsonReader)
            throws IOException, IllegalArgumentException;


    /**
     * Returns the int value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  int value
     */
    public static int nextInt(JsonReader reader, int dfltValue) {
        int value = dfltValue;
        try {
            if (!skipNull(reader)) {
                value = reader.nextInt();
            }
        } catch (IllegalStateException | NumberFormatException | IOException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * Returns the long value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  long value
     */
    public static long nextLong(JsonReader reader, long dfltValue) {
        long value = dfltValue;
        try {
            if (!skipNull(reader)) {
                value = reader.nextLong();
            }
        } catch (IllegalStateException | NumberFormatException | IOException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * Returns the double value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  double value
     */
    public static double nextDouble(JsonReader reader, double dfltValue) {
        double value = dfltValue;
        try {
            if (!skipNull(reader)) {
                value = reader.nextDouble();
            }
        } catch (IllegalStateException | NumberFormatException | IOException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * Returns the string value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  string value
     */
    public static String nextString(JsonReader reader, String dfltValue) {
        String value = dfltValue;
        try {
            if (!skipNull(reader)) {
                value = reader.nextString();
            }
        } catch (IllegalStateException | IOException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * Returns the string value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  string value
     */
    public static String nextStringFromHtml(JsonReader reader, String dfltValue) {
        return Html.fromHtml(nextString(reader, dfltValue)).toString();
    }

    /**
     * Returns the boolean value of the next token, consuming it
     * @param reader        Reader to read from
     * @param dfltValue     Default value to return in case of error
     * @return  boolean value
     */
    public static boolean nextBoolean(JsonReader reader, boolean dfltValue) {
        boolean value = dfltValue;
        try {
            if (!skipNull(reader)) {
                value = reader.nextBoolean();
            }
        } catch (IllegalStateException | IOException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * Returns the Uri value of the next token, consuming it
     * @param reader        Reader to read from
     * @return  boolean value
     */
    @Nullable
    public static Uri nextUri(JsonReader reader) {
        Uri value = null;
        try {
            String url = nextString(reader, "");
            if (!TextUtils.isEmpty(url)) {
                value = Uri.parse(url);
            }
        } catch (IllegalStateException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * Returns the Date value from the epoch of next token, consuming it
     * @param reader        Reader to read from
     * @param sourceUnit    Unit of the source time
     * @return  date value
     */
    @Nullable public static Date nextDate(JsonReader reader, TimeUnit sourceUnit) {
        Date value = null;
        try {
            long created = nextLong(reader, 0L);
            if (created > 0L) {
                created = TimeUnit.MILLISECONDS.convert(created, sourceUnit);
                value = new Date(created);
            }
        } catch (IllegalStateException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * Returns the Date value from the epoch seconds value of next token, consuming it
     * @param reader        Reader to read from
     * @return  date value
     */
    @Nullable public static Date nextDate(JsonReader reader) {
        return nextDate(reader, TimeUnit.SECONDS);
    }

    /**
     * Skip the next token, if its null
     * @param reader        Reader to read from
     * @return  <code>true</code> if skipped
     */
    public static boolean skipNull(JsonReader reader) {
        boolean skipped = false;
        try {
            if (reader.peek() == JsonToken.NULL) {
                reader.skipValue();
                skipped = true;
            }
        } catch (IOException e) {
            Timber.e(e);
        }
        return skipped;
    }
}

//{
//    "kind": "XXX",
//    "data": {
//          .....
//    }
//}
