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
import com.ianbuttimer.tidder.data.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class representing a Firebase Realtime database Config table row
 */
@IgnoreExtraProperties
public class ConfigFb extends AbstractFbRow implements IFbCursorable, Cloneable {

    @Exclude
    public static final String[] CURSOR_COLUMNS = new String[] {
            ConfigColumns.SAFE_FOR_WORK, ConfigColumns.REFRESH_ON_DISCARD,
            ConfigColumns.COMMENT_THREAD_EXPAND, ConfigColumns.POST_SOURCE
    };

    @Exclude
    private static final ConfigFb sFactory = new ConfigFb();

    public Boolean sfw;
    public Boolean refresh_on_discard;
    public int thread_expand;
    public String post_src;

    public ConfigFb() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        init(Config.getDfltSafeForWork(),
                Config.getDfltRefreshOnDiscard(),
                Config.calcDfltExpandLevel(),
                Config.getDfltPostSrc());
    }

    public ConfigFb(Boolean sfw, Boolean refresh_on_discard, int thread_expand, String post_src) {
        init(sfw, refresh_on_discard, thread_expand, post_src);
    }

    @Exclude
    private void init(Boolean sfw, Boolean refresh_on_discard, int thread_expand, String post_src) {
        this.sfw = sfw;
        this.refresh_on_discard = refresh_on_discard;
        this.thread_expand = thread_expand;
        this.post_src = post_src;
    }

    @Override
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(ConfigColumns.SAFE_FOR_WORK, sfw);
        result.put(ConfigColumns.REFRESH_ON_DISCARD, refresh_on_discard);
        result.put(ConfigColumns.COMMENT_THREAD_EXPAND, thread_expand);
        result.put(ConfigColumns.POST_SOURCE, post_src);

        return result;
    }

    @Exclude
    public static ConfigFb generate(ContentValues contentValues) {
        return update(new ConfigFb(), contentValues);
    }

    @Exclude
    public static ConfigFb update(ConfigFb obj, ContentValues contentValues) {
        if (contentValues.containsKey(ConfigColumns.SAFE_FOR_WORK)) {
            obj.sfw = contentValues.getAsBoolean(ConfigColumns.SAFE_FOR_WORK);
        }
        if (contentValues.containsKey(ConfigColumns.REFRESH_ON_DISCARD)) {
            obj.refresh_on_discard = contentValues.getAsBoolean(ConfigColumns.REFRESH_ON_DISCARD);
        }
        if (contentValues.containsKey(ConfigColumns.COMMENT_THREAD_EXPAND)) {
            obj.thread_expand = contentValues.getAsInteger(ConfigColumns.COMMENT_THREAD_EXPAND);
        }
        if (contentValues.containsKey(ConfigColumns.POST_SOURCE)) {
            obj.post_src = contentValues.getAsString(ConfigColumns.POST_SOURCE);
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
        return new Object[] {
                sfw, refresh_on_discard, thread_expand, post_src
        };
    }

    @Exclude
    public boolean getSfw() {
        return sfw;
    }

    @Exclude
    public void setSfw(boolean sfw) {
        this.sfw = sfw;
    }

    @Exclude
    public boolean getRefreshOnDiscard() {
        return refresh_on_discard;
    }

    @Exclude
    public void setRefreshOnDiscard(boolean refresh_on_discard) {
        this.refresh_on_discard = refresh_on_discard;
    }

    @Exclude
    public boolean getThreadExpand() {
        return (thread_expand > 0);
    }

    @Exclude
    public void setThreadExpand(boolean thread_expand) {
        this.thread_expand = (thread_expand ? Config.getDfltExpandLevel() : 0);
    }

    @Exclude
    public int getThreadExpandLevel() {
        return thread_expand;
    }

    @Exclude
    public void setThreadExpandLevel(int thread_expand) {
        this.thread_expand = thread_expand;
    }

    @Exclude
    public String getPostSrc() {
        return post_src;
    }

    @Exclude
    public void setPostSrc(String post_src) {
        this.post_src = post_src;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigFb configFb = (ConfigFb) o;
        return thread_expand == configFb.thread_expand &&
                Objects.equals(sfw, configFb.sfw) &&
                Objects.equals(refresh_on_discard, configFb.refresh_on_discard) &&
                Objects.equals(post_src, configFb.post_src);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sfw, refresh_on_discard, thread_expand, post_src);
    }

    @Override
    public ConfigFb clone() {
        ConfigFb clone = new ConfigFb();
        clone.setSfw(sfw);
        clone.setRefreshOnDiscard(refresh_on_discard);
        clone.setThreadExpandLevel(thread_expand);
        clone.setPostSrc(post_src);
        return clone;
    }
}
