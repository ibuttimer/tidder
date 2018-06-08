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
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;

import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;
import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;

import timber.log.Timber;


/**
 * Base class for a link object.
 * See sub classes for class specific details.
 */
@Parcel
public class Link extends RedditObject
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


//    "domain": "aljazeera.com"
//    "subreddit": "worldnews",
//            "likes": null,
//            "id": "7yff8f",
//            "author": "papivebipi",
//            "score": 3
//            "over_18": false
//            "subreddit_id": "t5_2qh13",
//            "name": "t3_7yff8f"
//            "permalink": "/r/worldnews/comments/7yff8f/palestinian_teenagers_killed_in_israeli_air_raids/"
//            "created": 1519001369.0,
//            "url":"http://www.aljazeera.com/news/2018/02/palestinian-teenagers-killed-israeli-air-strikes-180218070120500.html",
//            "title": "Palestinian teenagers killed in Israeli air raids",
//            "subreddit_name_prefixed": "r/worldnews"
//            "num_comments": 17
//            "is_video": false

//    "secure_media_embed": {
// "content": "&lt;iframe width=\"600\" height=\"338\" src=\"https://www.youtube.com/embed/sFTvlywhg9Q?feature=oembed&amp;enablejsapi=1&amp;enablejsapi=1&amp;enablejsapi=1\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen&gt;&lt;/iframe&gt;",
// "width": 600, "scrolling": false, "media_domain_url": "https://www.redditmedia.com/mediaembed/87p2jc", "height": 338}

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
    protected String getRedditType() {
        return TYPE_LINK;
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, BaseObject obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            Link object = ((Link) obj);
            consumed = true;
            // process required fields
            if (URL.equals(name)) {
                object.setUrl(nextUri(jsonReader));
            } else if (THUMBNAIL.equals(name)) {
                object.setThumbnail(nextUri(jsonReader));
            } else if (LIKES.equals(name)) {
                object.setLikes(nextInt(jsonReader, 0));
            } else if (SCORE.equals(name)) {
                object.setScore(nextInt(jsonReader, 0));
            } else if (NUM_COMMENTS.equals(name)) {
                object.setNumComments(nextInt(jsonReader, 0));
            } else if (OVER_18.equals(name)) {
                object.setOver18(nextBoolean(jsonReader, false));
            } else if (IS_VIDEO.equals(name)) {
                object.setVideo(nextBoolean(jsonReader, false));
            } else if (HIDE_SCORE.equals(name)) {
                object.setHideScore(nextBoolean(jsonReader, false));
            } else if (DOMAIN.equals(name)) {
                object.setDomain(nextString(jsonReader, ""));
            } else if (SUBREDDIT.equals(name)) {
                object.setSubreddit(nextString(jsonReader, ""));
            } else if (AUTHOR.equals(name)) {
                object.setAuthor(nextString(jsonReader, ""));
            } else if (SUBREDDIT_ID.equals(name)) {
                object.setSubredditId(nextString(jsonReader, ""));
            } else if (PERMALINK.equals(name)) {
                object.setPermalink(nextString(jsonReader, ""));
            } else if (SELFTEXT.equals(name)) {
                object.setSelfText(nextString(jsonReader, ""));
            } else if (SELFTEXT_HTML.equals(name)) {
                object.setSelfTextHtml(nextStringFromHtml(jsonReader, ""));
            } else if (TITLE.equals(name)) {
                object.setTitle(nextStringFromHtml(jsonReader, ""));
            } else if (SUBREDDIT_NAME_PREFIXED.equals(name)) {
                object.setSubredditNamePrefixed(nextString(jsonReader, ""));
            } else if (SECURE_MEDIA.equals(name)) {
                if (!skipNull(jsonReader)) {
                    SecureMedia secureMedia = new SecureMedia();
                    secureMedia.parseJson(jsonReader);
                    object.setSecureMedia(secureMedia);
                }
            } else if (SECURE_MEDIA_EMBED.equals(name)) {
                object.setSecureMediaEmbed(readMedisEmbed(jsonReader));
            } else if (MEDIA_EMBED.equals(name)) {
                object.setMediaEmbed(readMedisEmbed(jsonReader));
            } else if (PREVIEW.equals(name)) {
                if (!skipNull(jsonReader)) {
                    Preview preview = new Preview();
                    preview.parseJson(jsonReader);
                    object.setPreview(preview);
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
