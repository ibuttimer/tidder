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

import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.ColorInt;

import com.ianbuttimer.tidder.data.db.FollowColumns;
import com.ianbuttimer.tidder.net.UriUtils;
import com.ianbuttimer.tidder.reddit.ISubredditName;

import org.parceler.Parcel;

import java.util.Objects;

import static com.ianbuttimer.tidder.reddit.Subreddit.DEFAULT_KEY_COLOUR;

/**
 * Class representing the result of a database follow table query
 */
@Parcel
public class Follow extends AbstractDbRow implements ISubredditName {

    protected String mUuid;
    protected String mSubreddit;
    @ColorInt protected int mKeyColour;
    protected String mIconImg;

    /**
     * Default constructor
     */
    public Follow() {
        super();
    }

    /**
     * Constructor
     * @param cursor    Cursor from whose current row to create object
     */
    public Follow(Cursor cursor) {
        super(cursor);

        mUuid = getString(cursor, FollowColumns.UUID, "");
        mSubreddit = getString(cursor, FollowColumns.SUBREDDIT, "");
        mKeyColour = getInt(cursor, FollowColumns.KEY_COLOUR, DEFAULT_KEY_COLOUR);
        mIconImg = getString(cursor, FollowColumns.ICON_IMG, "");
    }

    @Override
    protected void init() {
        super.init();
        mUuid = "";
        mSubreddit = "";
        mKeyColour = DEFAULT_KEY_COLOUR;
        mIconImg = "";
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }

    @Override
    public String getSubreddit() {
        return mSubreddit;
    }

    @Override
    public void setSubreddit(String subreddit) {
        this.mSubreddit = subreddit;
    }

    @ColorInt public int getKeyColour() {
        return mKeyColour;
    }

    public void setKeyColour(@ColorInt int keyColour) {
        this.mKeyColour = keyColour;
    }

    public String getIconImg() {
        return mIconImg;
    }

    public Uri getIconImgUri() {
        return UriUtils.parse(mIconImg);
    }

    public void setIconImg(String iconImg) {
        this.mIconImg = iconImg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Follow follow = (Follow) o;
        return mKeyColour == follow.mKeyColour &&
                Objects.equals(mUuid, follow.mUuid) &&
                Objects.equals(mSubreddit, follow.mSubreddit) &&
                Objects.equals(mIconImg, follow.mIconImg);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), mUuid, mSubreddit, mKeyColour, mIconImg);
    }
}
