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
import java.util.ArrayList;

import timber.log.Timber;


/**
 * Base class for a link object.
 * See sub classes for class specific details.
 */
@Parcel
public class PreviewImages extends RedditObject {

    public static final PreviewImages INSTANCE = new PreviewImages();

    protected static final String SOURCE = "source";
    protected static final String RESOLUTIONS = "resolutions";
    protected static final String VARIANTS = "variants";

    protected ImageSource mSource;
    protected ImageSource[] mResolutions;

    /**
     * Default constructor
     */
    public PreviewImages() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public PreviewImages(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        super.init();
        mSource = null;
        mResolutions = null;
    }

    public boolean copy(PreviewImages link) {
        return Utils.copyFields(link, this);
    }

    @Override
    protected PreviewImages getInstance() {
        return new PreviewImages();
    }

    @Override
    protected String getRedditType() {
        return "";
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, BaseObject obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            PreviewImages object = ((PreviewImages) obj);
            consumed = true;
            // process required fields
            if (SOURCE.equals(name)) {
                ImageSource source = new ImageSource();
                source.parseJson(jsonReader);
                object.setSource(source);
            } else if (RESOLUTIONS.equals(name)) {
                ArrayList<ImageSource> list = new ArrayList<>();

                ImageSource.INSTANCE.parseJsonArray(jsonReader, list);

                ImageSource[] resolutions = null;
                if (list.size() > 0) {
                    resolutions = list.toArray(new ImageSource[list.size()]);
                }
                object.setResolutions(resolutions);
            } else {
                consumed = false;
            }
        }
        return consumed;
    }


    public ImageSource getSource() {
        return mSource;
    }

    public void setSource(ImageSource source) {
        this.mSource = source;
    }

    public ImageSource[] getResolutions() {
        return mResolutions;
    }

    public void setResolutions(ImageSource[] resolutions) {
        this.mResolutions = resolutions;
    }
}
