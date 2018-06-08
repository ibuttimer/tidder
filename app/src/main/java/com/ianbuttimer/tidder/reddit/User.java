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
import android.text.TextUtils;
import android.util.JsonReader;

import java.io.IOException;

/**
 * Class for a Reddit user object
 */

public class User extends BaseObject {

    /* There is a lot of detail returned from the /api/v1/me endpoint, only extracting
        what is currently needed
     */
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String ICON = "icon_img";
    private static final String HAS_SUSCRIBED = "has_subscribed";

    private String mName;
    private String mId;
    private Uri mIcon;
    private boolean mHasSuscribed;

    /**
     * Default constructor
     */
    public User() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public User(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        mName = "";
        mId = "";
        mIcon = null;
        mHasSuscribed = false;
    }

    @Override
    protected User getInstance() {
        return new User();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, BaseObject obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        User object = ((User) obj);
        boolean consumed = true;
        if (NAME.equals(name)) {
            object.mName = nextString(jsonReader, "");
        } else if (ID.equals(name)) {
            object.mId = nextString(jsonReader, "");
        } else if (ICON.equals(name)) {
            object.mIcon = nextUri(jsonReader);
        } else if (HAS_SUSCRIBED.equals(name)) {
            object.mHasSuscribed = nextBoolean(jsonReader, false);
        } else {
            consumed = false;
        }
        return consumed;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(getId());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Uri getIcon() {
        return mIcon;
    }

    public void setIcon(Uri icon) {
        this.mIcon = icon;
    }

    public boolean hasSuscribed() {
        return mHasSuscribed;
    }

    public void setHasSuscribed(boolean hasSuscribed) {
        this.mHasSuscribed = hasSuscribed;
    }

}
