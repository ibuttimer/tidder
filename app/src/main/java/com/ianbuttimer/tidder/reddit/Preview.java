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

import android.support.annotation.Nullable;
import android.util.JsonReader;

import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Base class for a link object.
 * See sub classes for class specific details.
 */
@Parcel
public class Preview extends RedditObject {

    protected static final String IMAGES = "images";
    protected static final String ENABLED = "enabled";

    protected boolean mEnabled;
    protected PreviewImages[] mPreview;

    /**
     * Default constructor
     */
    public Preview() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public Preview(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        super.init();
        mEnabled = false;
        mPreview = null;
    }

    public boolean copy(Preview link) {
        return Utils.copyFields(link, this);
    }

    @Override
    protected Preview getInstance() {
        return new Preview();
    }

    @Override
    public String getRedditType() {
        return "";
    }

    @Override
    public AbstractProxy getProxy() {
        return null;
    }

    @Override
    public AbstractProxy addToCache() {
        return null;
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, BaseObject obj)
                                        throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            Preview object = ((Preview) obj);
            consumed = true;
            // process required fields
            if (IMAGES.equals(name)) {
                object.setPreview(readPreviewImages(jsonReader));
            } else if (ENABLED.equals(name)) {
                object.setEnabled(nextBoolean(jsonReader, false));
            } else {
                consumed = false;
            }
        }
        return consumed;
    }


    private PreviewImages[] readPreviewImages(JsonReader jsonReader) throws IOException {
        PreviewImages[] previewImages = null;
        if (!skipNull(jsonReader)) {

            ArrayList<PreviewImages> list = new ArrayList<>();

            PreviewImages.INSTANCE.parseJsonArray(jsonReader, list);

            if (list.size() > 0) {
                previewImages = list.toArray(new PreviewImages[list.size()]);
            }
        }
        return previewImages;
    }


    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    @Nullable public PreviewImages[] getPreview() {
        return mPreview;
    }

    public void setPreview(PreviewImages[] preview) {
        this.mPreview = preview;
    }
}
