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

import android.util.JsonReader;

import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;


/**
 * Base class for a dimensioned object.
 * See sub classes for class specific details.
 */

public abstract class DimensionedObject<T extends BaseObject> extends BaseObject<T>  {

    protected static final String WIDTH = "width";
    protected static final String HEIGHT = "height";

    protected int mWidth;
    protected int mHeight;

    /**
     * Default constructor
     */
    public DimensionedObject() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public DimensionedObject(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        mWidth = 0;
        mHeight = 0;
    }

    public boolean copy(DimensionedObject<T> object) {
        return Utils.copyFields(object, this);
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, T obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        DimensionedObject<T> object = ((DimensionedObject<T>) obj);
        boolean consumed = true;
        if (WIDTH.equals(name)) {
            object.setWidth(nextInt(jsonReader, 0));
        } else if (HEIGHT.equals(name)) {
            object.setHeight(nextInt(jsonReader, 0));
        } else {
            consumed = false;
        }
        return consumed;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }
}
