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

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.JsonReader;

import com.ianbuttimer.tidder.data.Follow;
import com.ianbuttimer.tidder.net.RedditUriBuilder;
import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;


/**
 * Base class for a subreddit object.
 * See sub classes for class specific details.
 */
@Parcel
public class Subreddit extends RedditObject {

    @ColorInt public static int DEFAULT_KEY_COLOUR = Color.BLACK;

    /* Subreddit info may be returned from a number of endpoints and the fields names are
        not always consistent.
        There is a lot of info in some results
     */
    // common fields
    protected static final String ICON = "icon_img";
    protected static final String ACTIVE_USER_CNT = "active_user_count";
    protected static final String KEY_COLOUR = "key_color";
    protected static final String ALLOW_IMAGES = "allow_images";

    protected String mDisplayName;          // display name of subreddit, e.g. "news"
    protected String mDisplayNamePrefixed;  // prefixed display name of subreddit, e.g. "r/news"
    protected String mTitle;
    protected Uri mIcon;
    protected Uri mHeader;
    protected Uri mBanner;
    protected int mActiveUsers;
    protected int mSuscribers;
    @ColorInt protected int mKeyColour;
    protected boolean mAllowImages;
    protected String mDescription;
    protected String mDescriptionHtml;
    protected boolean mOver18;
    protected String mUrl;                   // e.g. "/r/news/"
    protected boolean mFollowing;



    /**
     * Default constructor
     */
    public Subreddit() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public Subreddit(String json) {
        parseJson(json);
    }

    public Subreddit(Follow follow) {
        init();
        setDisplayName(follow.getSubreddit());
        setKeyColour(follow.getKeyColour());
        setIcon(follow.getIconImgUri());
    }

    @Override
    protected void init() {
        super.init();
        mDisplayName = "";
        mDisplayNamePrefixed = "";
        mTitle = "";
        mIcon = null;
        mHeader = null;
        mBanner = null;
        mActiveUsers = 0;
        mSuscribers = 0;
        mKeyColour = DEFAULT_KEY_COLOUR;
        mAllowImages = false;
        mDescription = "";
        mDescriptionHtml = "";
        mOver18 = false;
        mUrl = "";
        mFollowing = false;
    }

    /**
     * Copy fields from the specified object
     * @param subreddit Object to copy
     * @return  <code>true</code> if copied successfully
     */
    public boolean copy(Subreddit subreddit) {
        return Utils.copyFields(subreddit, this);
    }

    @Override
    protected Subreddit getInstance() {
        return new Subreddit();
    }

    @Override
    protected String getRedditType() {
        return TYPE_SUBREDDIT;
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, BaseObject obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        Subreddit object = ((Subreddit) obj);
        boolean consumed = super.parseToken(jsonReader, name, object);
        if (!consumed) {
            consumed = true;
            // process common subreddit fields
            if (ICON.equals(name)) {
                object.mIcon = nextUri(jsonReader);
            } else if (ACTIVE_USER_CNT.equals(name)) {
                object.mActiveUsers = nextInt(jsonReader, 0);
            } else if (KEY_COLOUR.equals(name)) {
                String colour = nextString(jsonReader, "");
                if (!TextUtils.isEmpty(colour)) {
                    object.mKeyColour = Color.parseColor(colour);
                }
            } else if (ALLOW_IMAGES.equals(name)) {
                object.mAllowImages = nextBoolean(jsonReader, false);
            } else {
                consumed = false;
            }
        }
        return consumed;
    }

    /**
     * Get the display name of object, e.g. "news"
     * @return display name
     */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Set the display name of object, e.g. "news"
     * @param displayName display name
     */
    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    /**
     * Get the prefixed display name of object, e.g. "r/news"
     * @return  prefixed display name
     */
    public String getDisplayNamePrefixed() {
        return mDisplayNamePrefixed;
    }

    public void setDisplayNamePrefixed(String displayNamePrefixed) {
        this.mDisplayNamePrefixed = displayNamePrefixed;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Nullable public Uri getIcon() {
        return mIcon;
    }

    public void setIcon(Uri icon) {
        this.mIcon = icon;
    }

    public Uri getHeader() {
        return mHeader;
    }

    public void setHeader(Uri header) {
        this.mHeader = header;
    }

    public Uri getBanner() {
        return mBanner;
    }

    public void setBanner(Uri banner) {
        this.mBanner = banner;
    }

    public int getActiveUsers() {
        return mActiveUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.mActiveUsers = activeUsers;
    }

    public int getSuscribers() {
        return mSuscribers;
    }

    public void setSuscribers(int suscribers) {
        this.mSuscribers = suscribers;
    }

    @ColorInt public int getKeyColour() {
        return mKeyColour;
    }

    public void setKeyColour(@ColorInt int keyColour) {
        this.mKeyColour = keyColour;
    }

    public boolean isAllowImages() {
        return mAllowImages;
    }

    public void setAllowImages(boolean allowImages) {
        this.mAllowImages = allowImages;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getDescriptionHtml() {
        return mDescriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.mDescriptionHtml = descriptionHtml;
    }

    public boolean isOver18() {
        return mOver18;
    }

    public void setOver18(boolean over18) {
        this.mOver18 = over18;
    }

    public String getUrl() {
        return mUrl;
    }

    public Uri getUri() {
        return RedditUriBuilder.getOathUri(mUrl);
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public boolean isFollowing() {
        return mFollowing;
    }

    public void setFollowing(boolean following) {
        this.mFollowing = following;
    }
}
