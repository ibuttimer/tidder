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
import android.util.JsonReader;

import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;


/**
 * Base class for an image source object.
 * See sub classes for class specific details.
 */
@Parcel
public class ImageSource extends DimensionedObject<ImageSource> {

    public static final ImageSource INSTANCE = new ImageSource();

    protected static final String URL = "url";

    protected Uri mUrl;

    /**
     * Default constructor
     */
    public ImageSource() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public ImageSource(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        super.init();
        mUrl = null;
    }

    public boolean copy(ImageSource object) {
        return Utils.copyFields(object, this);
    }

    @Override
    protected ImageSource getInstance() {
        return new ImageSource();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, ImageSource obj) throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            consumed = true;
            if (URL.equals(name)) {
                obj.mUrl = nextUri(jsonReader);
            } else {
                consumed = false;
            }
        }
        return consumed;
    }

    public Uri getUrl() {
        return mUrl;
    }

    public void setUrl(Uri url) {
        this.mUrl = url;
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
