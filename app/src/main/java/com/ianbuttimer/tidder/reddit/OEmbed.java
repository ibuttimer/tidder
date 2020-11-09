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
import java.util.Objects;

import javax.annotation.Nullable;


/**
 * Base class for an  oEmbed object.
 * @see <a href="https://oembed.com/">oEmbed</a>
 */
@Parcel
public class OEmbed extends BaseObject<OEmbed> {

    public static final OEmbed EMPTY = new OEmbed();

    /*
     * The following response parameters are valid for all response types:
     *  type (required)
     *      The resource type. Valid values, along with value-specific parameters, are described below.
     *  version (required)
     *      The oEmbed version number. This must be 1.0.
     *  title (optional)
     *      A text title, describing the resource.
     *  author_name (optional)
     *      The name of the author/owner of the resource.
     *  author_url (optional)
     *      A URL for the author/owner of the resource.
     *  provider_name (optional)
     *      The name of the resource provider.
     *  provider_url (optional)
     *      The url of the resource provider.
     *  cache_age (optional)
     *      The suggested cache lifetime for this resource, in seconds. Consumers may choose to use this value or not.
     *  thumbnail_url (optional)
     *      A URL to a thumbnail image representing the resource. The thumbnail must respect any maxwidth and maxheight parameters. If this parameter is present, thumbnail_width and thumbnail_height must also be present.
     *  thumbnail_width (optional)
     *      The width of the optional thumbnail. If this parameter is present, thumbnail_url and thumbnail_height must also be present.
     *  thumbnail_height (optional)
     *      The height of the optional thumbnail. If this parameter is present, thumbnail_url and thumbnail_width must also be present.
     */

    protected static final String TYPE = "type";
    protected static final String VERSION = "version";
    protected static final String TITLE = "title";
    protected static final String AUTHOR_NAME = "author_name";
    protected static final String AUTHOR_URL = "author_url";
    protected static final String PROVIDER_NAME = "provider_name";
    protected static final String PROVIDER_URL = "provider_url";
    protected static final String CACHE_AGE = "cache_age";
    protected static final String THUMBNAIL_URL = "thumbnail_url";
    protected static final String THUMBNAIL_WIDTH = "thumbnail_width";
    protected static final String THUMBNAIL_HEIGHT = "thumbnail_height";

    protected String mType;
    protected String mVersion;
    protected String mTitle;
    protected String mAuthor;
    protected Uri mAuthorUrl;
    protected String mProvider;
    protected Uri mProviderUrl;
    protected long mCacheAge;
    protected Uri mThumbnailUrl;
    protected int mThumbnailWidth;
    protected int mThumbnailHeight;

    /*
     * The photo type
     * This type is used for representing static photos. The following parameters are defined:
     *  url (required)
     *      The source URL of the image. Consumers should be able to insert this URL into an <img> element. Only HTTP and HTTPS URLs are valid.
     *  width (required)
     *      The width in pixels of the image specified in the url parameter.
     *  height (required)
     *      The height in pixels of the image specified in the url parameter.
     */
    protected static final String URL = "url";
    protected static final String WIDTH = "width";
    protected static final String HEIGHT = "height";

    protected Uri mUrl;
    protected int mWidth;
    protected int mHeight;

    /*
     * The video type
     * This type is used for representing playable videos. The following parameters are defined:
     *  html (required)
     *      The HTML required to embed a video player. The HTML should have no padding or margins. Consumers may wish to load the HTML in an off-domain iframe to avoid XSS vulnerabilities.
     *  width (required)
     *      The width in pixels required to display the HTML.
     *  height (required)
     *      The height in pixels required to display the HTML.
     */
    protected static final String HTML = "html";

    protected String mHtml;

    /*
     * The rich type
     * This type is used for rich HTML content that does not fall under one of the other categories. The following parameters are defined:
     *  html (required)
     *      The HTML required to display the resource. The HTML should have no padding or margins. Consumers may wish to load the HTML in an off-domain iframe to avoid XSS vulnerabilities. The markup should be valid XHTML 1.0 Basic.
     *  width (required)
     *      The width in pixels required to display the HTML.
     *  height (required)
     *      The height in pixels required to display the HTML.
     */

    /**
     * Default constructor
     */
    public OEmbed() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public OEmbed(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        mType = "";
        mVersion = "";
        mTitle = "";
        mAuthor = "";
        mAuthorUrl = null;
        mProvider = "";
        mProviderUrl = null;
        mCacheAge = 0;
        mThumbnailUrl = null;
        mThumbnailWidth = 0;
        mThumbnailHeight = 0;
        mUrl = null;
        mWidth = 0;
        mHeight = 0;
        mHtml = "";
    }

    public boolean copy(OEmbed link) {
        return Utils.copyFields(link, this);
    }

    @Override
    protected OEmbed getInstance() {
        return new OEmbed();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, OEmbed obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        OEmbed object = ((OEmbed) obj);
        boolean consumed = true;
        if (AUTHOR_URL.equals(name)) {
            object.setAuthorUrl(nextUri(jsonReader));
        } else if (PROVIDER_URL.equals(name)) {
            object.setProviderUrl(nextUri(jsonReader));
        } else if (THUMBNAIL_URL.equals(name)) {
            object.setThumbnailUrl(nextUri(jsonReader));
        } else if (URL.equals(name)) {
            object.setUrl(nextUri(jsonReader));
        } else if (TYPE.equals(name)) {
            object.setType(nextString(jsonReader, ""));
        } else if (VERSION.equals(name)) {
            object.setVersion(nextString(jsonReader, ""));
        } else if (TITLE.equals(name)) {
            object.setTitle(nextString(jsonReader, ""));
        } else if (AUTHOR_NAME.equals(name)) {
            object.setAuthor(nextString(jsonReader, ""));
        } else if (PROVIDER_NAME.equals(name)) {
            object.setProvider(nextString(jsonReader, ""));
        } else if (HTML.equals(name)) {
            object.setHtml(nextString(jsonReader, ""));
        } else if (CACHE_AGE.equals(name)) {
            object.setCacheAge(nextLong(jsonReader, 0));
        } else if (THUMBNAIL_WIDTH.equals(name)) {
            object.setThumbnailWidth(nextInt(jsonReader, 0));
        } else if (THUMBNAIL_HEIGHT.equals(name)) {
            object.setThumbnailHeight(nextInt(jsonReader, 0));
        } else if (WIDTH.equals(name)) {
            object.setWidth(nextInt(jsonReader, 0));
        } else if (HEIGHT.equals(name)) {
            object.setHeight(nextInt(jsonReader, 0));
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

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String mVersion) {
        this.mVersion = mVersion;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    @Nullable public Uri getAuthorUrl() {
        return mAuthorUrl;
    }

    public void setAuthorUrl(Uri mAuthorUrl) {
        this.mAuthorUrl = mAuthorUrl;
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String mProvider) {
        this.mProvider = mProvider;
    }

    @Nullable public Uri getProviderUrl() {
        return mProviderUrl;
    }

    public void setProviderUrl(Uri mProviderUrl) {
        this.mProviderUrl = mProviderUrl;
    }

    public long getCacheAge() {
        return mCacheAge;
    }

    public void setCacheAge(long mCacheAge) {
        this.mCacheAge = mCacheAge;
    }

    @Nullable public Uri getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(Uri mThumbnailUrl) {
        this.mThumbnailUrl = mThumbnailUrl;
    }

    public int getThumbnailWidth() {
        return mThumbnailWidth;
    }

    public void setThumbnailWidth(int mThumbnailWidth) {
        this.mThumbnailWidth = mThumbnailWidth;
    }

    public int getThumbnailHeight() {
        return mThumbnailHeight;
    }

    public void setThumbnailHeight(int mThumbnailHeight) {
        this.mThumbnailHeight = mThumbnailHeight;
    }

    @Nullable public Uri getUrl() {
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

    public String getHtml() {
        return mHtml;
    }

    public void setHtml(String html) {
        this.mHtml = html;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OEmbed oEmbed = (OEmbed) o;
        return mCacheAge == oEmbed.mCacheAge &&
                mThumbnailWidth == oEmbed.mThumbnailWidth &&
                mThumbnailHeight == oEmbed.mThumbnailHeight &&
                mWidth == oEmbed.mWidth &&
                mHeight == oEmbed.mHeight &&
                Objects.equals(mType, oEmbed.mType) &&
                Objects.equals(mVersion, oEmbed.mVersion) &&
                Objects.equals(mTitle, oEmbed.mTitle) &&
                Objects.equals(mAuthor, oEmbed.mAuthor) &&
                Objects.equals(mAuthorUrl, oEmbed.mAuthorUrl) &&
                Objects.equals(mProvider, oEmbed.mProvider) &&
                Objects.equals(mProviderUrl, oEmbed.mProviderUrl) &&
                Objects.equals(mThumbnailUrl, oEmbed.mThumbnailUrl) &&
                Objects.equals(mUrl, oEmbed.mUrl) &&
                Objects.equals(mHtml, oEmbed.mHtml);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mType, mVersion, mTitle, mAuthor, mAuthorUrl, mProvider, mProviderUrl, mCacheAge, mThumbnailUrl, mThumbnailWidth, mThumbnailHeight, mUrl, mWidth, mHeight, mHtml);
    }
}
