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

package com.ianbuttimer.tidderish.reddit;

import android.util.JsonReader;

import com.ianbuttimer.tidderish.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;



/**
 * Base class for an  oEmbed object.
 * @see <a href="https://oembed.com/">oEmbed</a>
 */
@Parcel
public class SecureMedia extends BaseObject<SecureMedia> {

    protected static final String TYPE = "type";
    protected static final String OEMBED = "oembed";

    protected String mType;
    protected OEmbed mOembed;

    /**
     * Default constructor
     */
    public SecureMedia() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public SecureMedia(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        mType = "";
        mOembed = null;
    }

    public boolean copy(SecureMedia link) {
        return Utils.copyFields(link, this);
    }

    @Override
    protected SecureMedia getInstance() {
        return new SecureMedia();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, SecureMedia obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = true;
        if (TYPE.equals(name)) {
            obj.setType(nextString(jsonReader, ""));
        } else if (OEMBED.equals(name)) {
            OEmbed oembed = new OEmbed();
            oembed.parseJson(jsonReader);
            obj.setOembed(oembed);
        } else {
            consumed = false;
        }
        return consumed;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public OEmbed getOembed() {
        return mOembed;
    }

    public void setOembed(OEmbed oembed) {
        this.mOembed = oembed;
    }
}
