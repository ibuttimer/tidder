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
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Base class for reddit objects
 */

public abstract class BaseObject<T> {

    /** Comment type */
    public static final String TYPE_COMMENT = "t1";
    /** Account type */
    public static final String TYPE_ACCOUNT = "t2";
    /** Link type */
    public static final String TYPE_LINK = "t3";
    /** Message type */
    public static final String TYPE_MESSAGE = "t4";
    /** Subreddit type */
    public static final String TYPE_SUBREDDIT = "t5";
    /** Award type */
    public static final String TYPE_AWARD = "t6";
    /** Type & id separator for fullname */
    public static final String TYPE_ID_SEPARATOR = "_";
    /** Comment type prefix for fullname */
    public static final String TYPE_COMMENT_PREFIX = TYPE_COMMENT + TYPE_ID_SEPARATOR;
    /** Account type prefix for fullname */
    public static final String TYPE_ACCOUNT_PREFIX = TYPE_ACCOUNT + TYPE_ID_SEPARATOR;
    /** Link type prefix for fullname */
    public static final String TYPE_LINK_PREFIX = TYPE_LINK + TYPE_ID_SEPARATOR;
    /** Message type prefix for fullname */
    public static final String TYPE_MESSAGE_PREFIX = TYPE_MESSAGE + TYPE_ID_SEPARATOR;
    /** Subreddit type prefix for fullname */
    public static final String TYPE_SUBREDDIT_PREFIX = TYPE_SUBREDDIT + TYPE_ID_SEPARATOR;
    /** Award type prefix for fullname */
    public static final String TYPE_AWARD_PREFIX = TYPE_AWARD + TYPE_ID_SEPARATOR;

    /** More type */
    public static final String TYPE_MORE = "more";

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
        // get string converting new lines to breaks
        String text = nextString(reader, dfltValue).replace("\n", "<br>");
        return Html.fromHtml(text).toString();
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
    @Nullable public static Uri nextUri(JsonReader reader) {
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

    /**
     * Initialise this object
     */
    protected abstract void init();

    /**
     * Get a new instance
     */
    protected abstract T getInstance();

    /**
     * Parse a json string
     * @param json  string to parse
     */
    public void parseJson(String json) {
        init();

        StringReader strReader = new StringReader(json);
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(strReader);

            parseJson(jsonReader);
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            close(strReader, jsonReader);
        }
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

    /**
     * Parse a json token
     * @param jsonReader    Reader to get token from
     * @param name          Token name
     * @return  <code>true</code> if the token was consumed
     * @throws IOException
     */
    protected boolean parseToken(JsonReader jsonReader, String name) throws IOException {
        //noinspection unchecked
        return parseToken(jsonReader, name, (T)this);
    }

    /**
     * Parse a json token
     * @param jsonReader    Reader to get token from
     * @param name          Token name
     * @param obj           Object to save token value in
     * @return  <code>true</code> if the token was consumed
     * @throws IOException
     * @throws IllegalArgumentException     if object is null or incorrect class
     */
    protected abstract boolean parseToken(JsonReader jsonReader, String name, T obj) throws IOException, IllegalArgumentException;

    /**
     * Parse a json array string of these objects
     * @param jsonReader  reader to parse
     */
    public ArrayList<T> parseJsonArray(JsonReader jsonReader) throws IOException {
        ArrayList<T> list = new ArrayList<>();
        return parseJsonArray(jsonReader, list);
    }

    /**
     * Parse a json array string
     * @param jsonReader  reader to parse
     */
    @SuppressWarnings("unchecked")
    public ArrayList<T> parseJsonArray(JsonReader jsonReader, ArrayList<T> list) throws IOException {
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            T obj = getInstance();
            if (obj instanceof BaseObject) {
                ((BaseObject<T>)obj).parseJson(jsonReader);
            } else {
                throw new IllegalStateException("");
            }
            list.add(obj);
        }
        jsonReader.endArray();

        return list;
    }

    /**
     * Checks if an object is the correct type
     * @param obj   Object to check
     * @param clazz Expected class
     * @throws IllegalArgumentException     if object is null or incorrect class
     */
    protected void checkObject(Object obj, Class<?> clazz) {
        if (obj == null) {
            throw new IllegalArgumentException("Invalid null object");
        }
        if (obj.getClass() != clazz) {
            throw new IllegalArgumentException("Incorrect object type, expected "
                    + clazz.getName() + " but got "
                    + obj.getClass().getName());
        }
    }

    /**
     * Tidy up after json read
     * @param strReader     String reader
     * @param jsonReader    JSON reader
     */
    public void close(StringReader strReader, JsonReader jsonReader) {
        try {
            if (jsonReader != null) {
                jsonReader.close();
            }
        } catch (IOException e) {
            Timber.e(e);
        }
        if (strReader != null) {
            strReader.close();
        }
    }

    /**
     * Make a reddit object fullname
     * @param type  Type of object
     * @param id    Id of object
     * @return  Fullname
     */
    public static String makeFullname(String type, String id) {
        return type + TYPE_ID_SEPARATOR + id;
    }

    /**
     * Split a reddit object fullname into its constitute parts
     * @param fullname  Fiullname to split
     * @return  Pair with type as irst & id as second, or <code>null</code> if not valid fullname
     */
    @Nullable
    public static Pair<String, String> splitFullname(String fullname) {
        String[] splits = fullname.split(TYPE_ID_SEPARATOR);
        Pair<String, String> result = null;
        if (splits.length == 2) {
            result = new Pair<>(splits[0], splits[1]);
        }
        return result;
    }

}
