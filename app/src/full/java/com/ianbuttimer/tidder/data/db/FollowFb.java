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

package com.ianbuttimer.tidder.data.db;

import android.content.ContentValues;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FollowFb extends AbstractFbRow implements IFbCursorable {

    @Exclude
    public static final String[] CURSOR_COLUMNS = new String[] {
            FollowColumns.SUBREDDIT,
            FollowColumns.KEY_COLOUR,
            FollowColumns.ICON_IMG
    };

    @Exclude
    protected static final FollowFb sFactory = new FollowFb();

    public String subreddit;
    public String key_colour;
    public String icon_img;

    public FollowFb() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this("", "", "");
    }

    public FollowFb(String subreddit, String key_colour, String icon_img) {
        super();
        this.subreddit = subreddit;
        this.key_colour = key_colour;
        this.icon_img = icon_img;
    }

    @Override
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FollowColumns.SUBREDDIT, subreddit);
        result.put(FollowColumns.KEY_COLOUR, key_colour);
        result.put(FollowColumns.ICON_IMG, icon_img);

        return result;
    }

    @Exclude
    public static FollowFb generate(ContentValues contentValues) {
        return update(new FollowFb(), contentValues);
    }

    @Exclude
    public static FollowFb update(FollowFb obj, ContentValues contentValues) {
        if (contentValues.containsKey(FollowColumns.SUBREDDIT)) {
            obj.subreddit = contentValues.getAsString(FollowColumns.SUBREDDIT);
        }
        if (contentValues.containsKey(FollowColumns.KEY_COLOUR)) {
            obj.key_colour = contentValues.getAsString(FollowColumns.KEY_COLOUR);
        }
        if (contentValues.containsKey(FollowColumns.ICON_IMG)) {
            obj.icon_img = contentValues.getAsString(FollowColumns.ICON_IMG);
        }
        return obj;
    }

    @Override
    @Exclude
    public void update(ContentValues contentValues) {
        update(this, contentValues);
    }

    @Exclude
    public static IFbCursorable getFactory() {
        return sFactory;
    }

    @Override
    @Exclude
    protected String[] getCursorColumns() {
        return CURSOR_COLUMNS;
    }

    @Override
    @Exclude
    protected Object[] getCursorColumnValues() {
        return new String[] {
                subreddit,
                key_colour,
                icon_img
        };
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getKey_colour() {
        return key_colour;
    }

    public void setKey_colour(String key_colour) {
        this.key_colour = key_colour;
    }

    public String getIcon_img() {
        return icon_img;
    }

    public void setIcon_img(String icon_img) {
        this.icon_img = icon_img;
    }
}
