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

package com.ianbuttimer.tidder.data;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.data.db.ConfigColumns;

import org.parceler.Parcel;

import java.util.Objects;

/**
 * Class representing the result of a database config table query
 */
@Parcel
public class Config extends AbstractDbRow implements Cloneable {

    protected Boolean mSfw;
    protected Boolean mRefreshOnDiscard;
    protected int mThreadExpand;
    protected String mPostSrc;

    private static final boolean sDfltSafeForWork;
    private static final boolean sDfltRefreshOnDiscard;
    private static final boolean sDfltThreadExpand;
    private static final int sDfltExpandLevel;
    private static final String sDfltPostSrc;

    static {
        Context context = TidderApplication.getWeakApplicationContext().get();
        Resources resources = context.getResources();

        sDfltSafeForWork = resources.getBoolean(R.bool.pref_sfw_dflt_value);
        sDfltRefreshOnDiscard = resources.getBoolean(R.bool.pref_refresh_on_discard_dflt_value);
        sDfltThreadExpand = resources.getBoolean(R.bool.pref_autoexpand_dflt_value);
        sDfltExpandLevel = Integer.parseInt(
                                resources.getString(R.string.pref_autoexpand_level_dflt_value));
        sDfltPostSrc = resources.getString(R.string.pref_post_source_dflt_value);
    }

    /**
     * Default constructor
     */
    public Config() {
        super();
        init();
    }

    /**
     * Constructor
     * @param cursor    Cursor from whose current row to create object
     */
    public Config(Cursor cursor) {
        super(cursor);
        init();

        mSfw = getBoolean(cursor, ConfigColumns.SAFE_FOR_WORK, sDfltSafeForWork);
        mRefreshOnDiscard = getBoolean(cursor, ConfigColumns.REFRESH_ON_DISCARD, sDfltRefreshOnDiscard);
        mThreadExpand = getInt(cursor, ConfigColumns.COMMENT_THREAD_EXPAND, calcDfltExpandLevel());
        mPostSrc = getString(cursor, ConfigColumns.POST_SOURCE, sDfltPostSrc);
    }

    @Override
    protected void init() {
        super.init();
        mSfw = sDfltSafeForWork;
        mRefreshOnDiscard = sDfltRefreshOnDiscard;
        mThreadExpand = calcDfltExpandLevel();
        mPostSrc = sDfltPostSrc;
    }

    public Boolean getSfw() {
        return mSfw;
    }

    public void setSfw(Boolean sfw) {
        this.mSfw = sfw;
    }

    public Boolean getRefreshOnDiscard() {
        return mRefreshOnDiscard;
    }

    public void setRefreshOnDiscard(Boolean refreshOnDiscard) {
        this.mRefreshOnDiscard = refreshOnDiscard;
    }

    public int getThreadExpand() {
        return mThreadExpand;
    }

    public void setThreadExpand(int threadExpand) {
        this.mThreadExpand = threadExpand;
    }

    public boolean getAutoExpand() {
        return (mThreadExpand > 0);
    }

    public String getPostSrc() {
        return mPostSrc;
    }

    public void setPostSrc(String postSrc) {
        this.mPostSrc = postSrc;
    }

    public static boolean getDfltSafeForWork() {
        return sDfltSafeForWork;
    }

    public static boolean getDfltRefreshOnDiscard() {
        return sDfltRefreshOnDiscard;
    }

    public static boolean getDfltThreadExpand() {
        return sDfltThreadExpand;
    }

    public static int getDfltExpandLevel() {
        return sDfltExpandLevel;
    }

    public static int calcDfltExpandLevel() {
        int level = sDfltExpandLevel;
        if (!sDfltThreadExpand) {
            level = 0;
        }
        return level;
    }

    public static String getDfltPostSrc() {
        return sDfltPostSrc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Config config = (Config) o;
        return mThreadExpand == config.mThreadExpand &&
                Objects.equals(mSfw, config.mSfw) &&
                Objects.equals(mRefreshOnDiscard, config.mRefreshOnDiscard) &&
                Objects.equals(mPostSrc, config.mPostSrc);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), mSfw, mRefreshOnDiscard, mThreadExpand, mPostSrc);
    }

    @NonNull
    @Override
    protected Config clone() {
        Config clone = new Config();
        clone.setSfw(mSfw);
        clone.setRefreshOnDiscard(mRefreshOnDiscard);
        clone.setThreadExpand(mThreadExpand);
        clone.setPostSrc(mPostSrc);
        return clone;
    }
}
