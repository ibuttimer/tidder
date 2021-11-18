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

import android.net.Uri;
import android.util.JsonReader;

import com.ianbuttimer.tidderish.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.Objects;


/**
 * Base class for a link object.
 * See sub classes for class specific details.
 */
@Parcel
public class MediaEmbed extends DimensionedObject<MediaEmbed> {

    public static final MediaEmbed EMPTY = new MediaEmbed();

    protected static final String CONTENT = "content";
    protected static final String SCROLLING = "scrolling";
    protected static final String MEDIA_DOMAIN_URL = "media_domain_url";

//    "secure_media_embed": {
// "content": "&lt;iframe width=\"600\" height=\"338\" src=\"https://www.youtube.com/embed/sFTvlywhg9Q?feature=oembed&amp;enablejsapi=1&amp;enablejsapi=1&amp;enablejsapi=1\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen&gt;&lt;/iframe&gt;",
// "width": 600, "scrolling": false, "media_domain_url": "https://www.redditmedia.com/mediaembed/87p2jc", "height": 338}

    protected String mContent;
    protected Boolean mScrolling;
    protected Uri mMediaDomainUrl;


    /**
     * Default constructor
     */
    public MediaEmbed() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public MediaEmbed(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        super.init();
        mContent = "";
        mScrolling = false;
        mMediaDomainUrl = null;
    }

    public boolean copy(MediaEmbed link) {
        return Utils.copyFields(link, this);
    }

    @Override
    protected MediaEmbed getInstance() {
        return new MediaEmbed();
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, MediaEmbed obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            consumed = true;
            // process required fields
            if (MEDIA_DOMAIN_URL.equals(name)) {
                obj.mMediaDomainUrl = nextUri(jsonReader);
            } else if (SCROLLING.equals(name)) {
                obj.mScrolling = nextBoolean(jsonReader, false);
            } else if (CONTENT.equals(name)) {
                obj.mContent = nextString(jsonReader, "");
            } else {
                consumed = false;
            }
        }
        return consumed;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public Boolean getScrolling() {
        return mScrolling;
    }

    public void setScrolling(Boolean scrolling) {
        this.mScrolling = scrolling;
    }

    public Uri getMediaDomainUrl() {
        return mMediaDomainUrl;
    }

    public void setMediaDomainUrl(Uri mediaDomainUrl) {
        this.mMediaDomainUrl = mediaDomainUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaEmbed that = (MediaEmbed) o;
        return Objects.equals(mContent, that.mContent) &&
                Objects.equals(mScrolling, that.mScrolling) &&
                Objects.equals(mMediaDomainUrl, that.mMediaDomainUrl);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mContent, mScrolling, mMediaDomainUrl);
    }
}
