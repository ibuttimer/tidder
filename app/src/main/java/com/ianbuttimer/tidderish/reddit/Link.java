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
import androidx.annotation.Nullable;
import android.util.JsonReader;

import com.ianbuttimer.tidderish.ui.widgets.BasicStatsView;
import com.ianbuttimer.tidderish.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;

import timber.log.Timber;


/**
 * Base class for a link object.
 * See sub classes for class specific details.
 */
@Parcel
public class Link extends RedditObject<Link, LinkProxy>
        implements BasicStatsView.IBasicStats, ISubredditName {

    protected static final String DOMAIN = "domain";
    protected static final String SUBREDDIT = "subreddit";
    protected static final String LIKES = "likes";
    protected static final String AUTHOR = "author";
    protected static final String SCORE = "score";
    protected static final String OVER_18 = "over_18";
    protected static final String SUBREDDIT_ID = "subreddit_id";
    protected static final String PERMALINK = "permalink";
    protected static final String URL = "url";
    protected static final String THUMBNAIL = "thumbnail";
    protected static final String TITLE = "title";
    protected static final String SUBREDDIT_NAME_PREFIXED = "subreddit_name_prefixed";
    protected static final String NUM_COMMENTS = "num_comments";
    protected static final String IS_VIDEO = "is_video";
    protected static final String HIDE_SCORE = "hide_score";
    protected static final String SELFTEXT = "selftext";
    protected static final String SELFTEXT_HTML = "selftext_html";
    protected static final String PREVIEW = "preview";
    protected static final String IMAGES = "images";
    protected static final String SOURCE = "source";
    protected static final String RESOLUTIONS = "resolutions";
    protected static final String SECURE_MEDIA = "secure_media";
    protected static final String SECURE_MEDIA_EMBED = "secure_media_embed";
    protected static final String MEDIA_EMBED = "media_embed";


    protected static final Uri SELF_URI = Uri.parse("self");

    protected String mDomain;
    protected String mSubreddit;
    protected int mLikes;
    protected String mAuthor;
    protected int mScore;
    protected boolean mOver18;
    protected String mSubredditId;
    protected String mPermalink;
    protected Uri mUrl;
    protected Uri mThumbnail;
    protected String mTitle;
    protected String mSubredditNamePrefixed;        // prefixed name of subreddit, e.g. "r/news"
    protected String mSelfText;
    protected String mSelfTextHtml;
    protected int mNumComments;
    protected Boolean mVideo;
    protected Boolean mHideScore;
    protected SecureMedia mSecureMedia;
    protected MediaEmbed mMediaEmbed;
    protected MediaEmbed mSecureMediaEmbed;
    protected Preview mPreview;


    /**
     * Default constructor
     */
    public Link() {
        init();
    }

    /**
     * Constructor
     * @param json  Json string to parse to create user
     */
    public Link(String json) {
        parseJson(json);
    }

    @Override
    protected void init() {
        super.init();
        mDomain = "";
        mSubreddit = "";
        mLikes = 0;
        mAuthor = "";
        mScore = 0;
        mOver18 = false;
        mSubredditId = "";
        mPermalink = "";
        mUrl = null;
        mThumbnail = null;
        mTitle = "";
        mSubredditNamePrefixed = "";
        mSelfText = "";
        mSelfTextHtml = "";
        mNumComments = 0;
        mVideo = false;
        mHideScore = false;
        mSecureMedia = null;
        mMediaEmbed = null;
        mSecureMediaEmbed = null;
        mPreview = null;
    }

    public boolean copy(Link link) {
        return Utils.copyFields(link, this);
    }

    @Override
    protected Link getInstance() {
        return new Link();
    }

    @Override
    public String getRedditType() {
        return TYPE_LINK;
    }

    @Override
    public LinkProxy getProxy() {
        return LinkProxy.getProxy(this);
    }

    @Override
    public LinkProxy addToCache() {
        LinkProxy proxy = getProxy();
        if (proxy != null) {
            proxy.addToCache(this);
        }
        return proxy;
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, Link obj)
                                        throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            consumed = true;
            // process required fields
            if (URL.equals(name)) {
                obj.setUrl(nextUri(jsonReader));
            } else if (THUMBNAIL.equals(name)) {
                obj.setThumbnail(nextUri(jsonReader));
            } else if (LIKES.equals(name)) {
                obj.setLikes(nextInt(jsonReader, 0));
            } else if (SCORE.equals(name)) {
                obj.setScore(nextInt(jsonReader, 0));
            } else if (NUM_COMMENTS.equals(name)) {
                obj.setNumComments(nextInt(jsonReader, 0));
            } else if (OVER_18.equals(name)) {
                obj.setOver18(nextBoolean(jsonReader, false));
            } else if (IS_VIDEO.equals(name)) {
                obj.setVideo(nextBoolean(jsonReader, false));
            } else if (HIDE_SCORE.equals(name)) {
                obj.setHideScore(nextBoolean(jsonReader, false));
            } else if (DOMAIN.equals(name)) {
                obj.setDomain(nextString(jsonReader, ""));
            } else if (SUBREDDIT.equals(name)) {
                obj.setSubreddit(nextString(jsonReader, ""));
            } else if (AUTHOR.equals(name)) {
                obj.setAuthor(nextString(jsonReader, ""));
            } else if (SUBREDDIT_ID.equals(name)) {
                obj.setSubredditId(nextString(jsonReader, ""));
            } else if (PERMALINK.equals(name)) {
                obj.setPermalink(nextString(jsonReader, ""));
            } else if (SELFTEXT.equals(name)) {
                obj.setSelfText(nextString(jsonReader, ""));
            } else if (SELFTEXT_HTML.equals(name)) {
                obj.setSelfTextHtml(nextStringFromHtml(jsonReader, ""));
            } else if (TITLE.equals(name)) {
                obj.setTitle(nextStringFromHtml(jsonReader, ""));
            } else if (SUBREDDIT_NAME_PREFIXED.equals(name)) {
                obj.setSubredditNamePrefixed(nextString(jsonReader, ""));
            } else if (SECURE_MEDIA.equals(name)) {
                if (!skipNull(jsonReader)) {
                    SecureMedia secureMedia = new SecureMedia();
                    secureMedia.parseJson(jsonReader);
                    obj.setSecureMedia(secureMedia);
                }
            } else if (SECURE_MEDIA_EMBED.equals(name)) {
                obj.setSecureMediaEmbed(readMedisEmbed(jsonReader));
            } else if (MEDIA_EMBED.equals(name)) {
                obj.setMediaEmbed(readMedisEmbed(jsonReader));
            } else if (PREVIEW.equals(name)) {
                if (!skipNull(jsonReader)) {
                    Preview preview = new Preview();
                    preview.parseJson(jsonReader);
                    obj.setPreview(preview);
                }
            } else {
                consumed = false;
            }
        }
        return consumed;
    }


    private MediaEmbed readMedisEmbed(JsonReader jsonReader) {
        MediaEmbed mediaEmbed = null;
        if (!skipNull(jsonReader)) {
            mediaEmbed = new MediaEmbed();
            try {
                mediaEmbed.parseJson(jsonReader);
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        return mediaEmbed;
    }


    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String domain) {
        this.mDomain = domain;
    }

    @Override
    public String getSubreddit() {
        return mSubreddit;
    }

    @Override
    public void setSubreddit(String subreddit) {
        this.mSubreddit = subreddit;
    }

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        this.mLikes = likes;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public int getScore() {
        int score = 0;
        if (!mHideScore) {
            score = mScore;
        }
        return score;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public boolean isOver18() {
        return mOver18;
    }

    public void setOver18(boolean over18) {
        this.mOver18 = over18;
    }

    public String getSubredditId() {
        return mSubredditId;
    }

    public void setSubredditId(String subredditId) {
        this.mSubredditId = subredditId;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public void setPermalink(String permalink) {
        this.mPermalink = permalink;
    }

    @Nullable public Uri getUrl() {
        return mUrl;
    }

    public void setUrl(Uri url) {
        this.mUrl = url;
    }

    @Nullable public Uri getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Uri thumbnail) {
        this.mThumbnail = thumbnail;
    }

    public boolean isSelfThumbnail() {
        boolean self = false;
        if (mThumbnail != null) {
            self = SELF_URI.equals(mThumbnail);
        }
        return self;
    }

    public boolean isLoadableThumbnail() {
        boolean loadable = false;
        if (mThumbnail != null) {
            loadable = !isSelfThumbnail() && !Uri.EMPTY.equals(mThumbnail);
        }
        return loadable;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getSubredditNamePrefixed() {
        return mSubredditNamePrefixed;
    }

    public void setSubredditNamePrefixed(String subredditNamePrefixed) {
        this.mSubredditNamePrefixed = subredditNamePrefixed;
    }

    public int getNumComments() {
        return mNumComments;
    }

    public void setNumComments(int numComments) {
        this.mNumComments = numComments;
    }

    public Boolean isVideo() {
        return mVideo;
    }

    public void setVideo(Boolean video) {
        this.mVideo = video;
    }

    public Boolean isHideScore() {
        return mHideScore;
    }

    public void setHideScore(Boolean hideScore) {
        this.mHideScore = hideScore;
    }

    public String getSelfText() {
        return mSelfText;
    }

    public void setSelfText(String selfText) {
        this.mSelfText = selfText;
    }

    public String getSelfTextHtml() {
        return mSelfTextHtml;
    }

    public void setSelfTextHtml(String selfTextHtml) {
        this.mSelfTextHtml = selfTextHtml;
    }

    @Nullable public SecureMedia getSecureMedia() {
        return mSecureMedia;
    }

    public void setSecureMedia(SecureMedia secureMedia) {
        this.mSecureMedia = secureMedia;
    }

    @Nullable public MediaEmbed getMediaEmbed() {
        return mMediaEmbed;
    }

    public void setMediaEmbed(MediaEmbed mediaEmbed) {
        this.mMediaEmbed = mediaEmbed;
    }

    @Nullable public MediaEmbed getSecureMediaEmbed() {
        return mSecureMediaEmbed;
    }

    public void setSecureMediaEmbed(MediaEmbed secureMediaEmbed) {
        this.mSecureMediaEmbed = secureMediaEmbed;
    }

    @Nullable public Preview getPreview() {
        return mPreview;
    }

    public void setPreview(Preview preview) {
        this.mPreview = preview;
    }
}
