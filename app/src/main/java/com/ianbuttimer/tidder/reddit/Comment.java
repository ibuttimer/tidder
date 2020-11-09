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


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonToken;

import com.ianbuttimer.tidder.data.ITester;
import com.ianbuttimer.tidder.reddit.get.CommentResponse;
import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;
import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Class representing a Reddit comment
 */
@Parcel
public class Comment extends RedditObject<Comment, CommentProxy> implements BasicStatsView.IBasicStats {

    public static final String SUBREDDIT_ID = "subreddit_id";
    public static final String LINK_ID = "link_id";
    public static final String REPLIES = "replies";
    public static final String AUTHOR = "author";
    public static final String PARENT_ID = "parent_id";
    public static final String SCORE = "score";
    public static final String BODY = "body";
    public static final String BODY_HTML = "body_html";
    public static final String STICKIED = "stickied";
    public static final String SUBREDDIT = "subreddit";
    public static final String SCORE_HIDDEN = "score_hidden";
    public static final String PERMALINK = "permalink";
    public static final String SUBREDDIT_NAME_PREFIXED = "subreddit_name_prefixed";
    public static final String DEPTH = "depth";

    protected String mSubredditId;
    protected String mLinkId;         // fullname of link to which this comment belongs
    protected Comment[] mReplies;
    protected String mAuthor;
    protected String mParentId;
    protected int mScore;
    protected String mBody;
    protected String mBodyHtml;
    protected boolean mStickied;
    protected String mSubreddit;
    protected boolean mScoreHidden;
    protected String mPermalink;
    protected String mSubredditNamePrefixed;
    protected int mDepth;

    protected Comment mParent;

    public Comment() {
        init();
    }

    @Override
    protected void init() {
        super.init();
        mSubredditId = "";
        mLinkId = "";
        mReplies = new Comment[0];
        mAuthor = "";
        mParentId = "";
        mScore = 0;
        mBody = "";
        mBodyHtml = "";
        mStickied = false;
        mSubreddit = "";
        mScoreHidden = false;
        mPermalink = "";
        mSubredditNamePrefixed = "";
        mDepth = 0;
        mParent = null;
    }

    public boolean copy(Comment comment) {
        return Utils.copyFields(comment, this);
    }

    @Override
    protected Comment getInstance() {
        return new Comment();
    }

    @Override
    public CommentProxy getProxy() {
        return CommentProxy.getProxy(this);
    }

    @Override
    public CommentProxy addToCache() {
        CommentProxy proxy = getProxy();
        if (proxy != null) {
            proxy.addToCache(this);
        }
        return proxy;
    }

    @Override
    public String getRedditType() {
        return TYPE_COMMENT;
    }


    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, Comment obj)
                                        throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = super.parseToken(jsonReader, name, obj);
        if (!consumed) {
            consumed = true;
            if (SUBREDDIT_ID.equals(name)) {
                obj.setSubredditId(nextString(jsonReader, ""));
            } else if (LINK_ID.equals(name)) {
                obj.setLinkId(nextString(jsonReader, ""));
            } else if (AUTHOR.equals(name)) {
                obj.setAuthor(nextString(jsonReader, ""));
            } else if (PARENT_ID.equals(name)) {
                obj.setParentId(nextString(jsonReader, ""));
            } else if (BODY.equals(name)) {
                obj.setBody(nextString(jsonReader, ""));
            } else if (BODY_HTML.equals(name)) {
                obj.setBodyHtml(nextStringFromHtml(jsonReader, ""));
            } else if (SUBREDDIT.equals(name)) {
                obj.setSubreddit(nextString(jsonReader, ""));
            } else if (PERMALINK.equals(name)) {
                obj.setPermalink(nextString(jsonReader, ""));
            } else if (SUBREDDIT_NAME_PREFIXED.equals(name)) {
                obj.setSubredditNamePrefixed(nextString(jsonReader, ""));
            } else if (SCORE.equals(name)) {
                obj.setScore(nextInt(jsonReader, 0));
            } else if (DEPTH.equals(name)) {
                obj.setDepth(nextInt(jsonReader, 0));
            } else if (STICKIED.equals(name)) {
                obj.setStickied(nextBoolean(jsonReader, false));
            } else if (SCORE_HIDDEN.equals(name)) {
                obj.setScoreHidden(nextBoolean(jsonReader, false));
            } else if (REPLIES.equals(name)) {
                if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                    // have replies to process
                    CommentResponse commentResponse = new CommentResponse();
                    commentResponse.parseJson(jsonReader);
                    ArrayList<Comment> mList = commentResponse.getList();
                    obj.setReplies(mList.toArray(new Comment[0]));
                } else {
                    // should be an empty string so skip
                    obj.setReplies(new Comment[0]);
                    jsonReader.skipValue();
                }
            } else {
                consumed = false;
            }
        }
        return consumed;
    }

    public String getSubredditId() {
        return mSubredditId;
    }

    public void setSubredditId(String subredditId) {
        this.mSubredditId = subredditId;
    }

    /**
     * Get the fullname of the link to which this comment belongs
     * @return  fullname of link
     */
    public String getLinkId() {
        return mLinkId;
    }

    public void setLinkId(String linkId) {
        this.mLinkId = linkId;
    }

    public Comment[] getReplies() {
        return mReplies;
    }

    public Comment getReply(ITester<Comment> tester) {
        Comment reply = null;
        if (Utils.arrayHasSize(mReplies)) {
//            reply =
        }
        return reply;
    }

    @Override
    public int getNumComments() {
        int count = 0;
        if (mReplies != null) {
            count = mReplies.length;
        }
        return count;
    }

    public void setReplies(Comment[] replies) {
        for (Comment reply : replies) {
            reply.setParent(this);
        }
        this.mReplies = replies;
    }

    public void addReply(Comment reply) {
        addReplies(reply);
    }

    public void addReplies(Comment... add) {
        addReplies(Arrays.asList(add));
    }

    public void addReplies(Collection<Comment> add) {
        Comment[] replies;
        int addCnt = add.size();
        int start;
        if ((mReplies == null) || (mReplies.length == 0)) {
            start = 0;
            replies = new Comment[addCnt];
        } else {
            start = mReplies.length;
            replies = new Comment[start + addCnt];
            System.arraycopy(mReplies, 0, replies, 0, start);
        }
        for (Comment reply : add) {
            reply.setParent(this);
            replies[start] = reply;
            ++start;
        }
        mReplies = replies;
    }

    public int indexOfReply(Comment reply) {
        int index = -1;
        if (Utils.arrayHasSize(mReplies) && (reply != null)) {
            for (int i = 0; i < mReplies.length; i++) {
                if (reply.equals(mReplies[i])) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public boolean removeReply(Comment reply) {
        boolean removed = false;
        int index = indexOfReply(reply);
        if (index >= 0) {
            mReplies = Utils.splice(mReplies, index, 1, Comment[].class);
            removed = true;
        }
        return removed;
    }


    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    /**
     * Get the id of the parent. i.e. its fullname e.g. "t1_dvdb99v"
     * @return
     */
    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        this.mParentId = parentId;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        this.mBody = body;
    }

    public String getBodyHtml() {
        return mBodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.mBodyHtml = bodyHtml;
    }

    public boolean isStickied() {
        return mStickied;
    }

    public void setStickied(boolean stickied) {
        this.mStickied = stickied;
    }

    public String getSubreddit() {
        return mSubreddit;
    }

    public void setSubreddit(String subreddit) {
        this.mSubreddit = subreddit;
    }

    public boolean isScoreHidden() {
        return mScoreHidden;
    }

    public void setScoreHidden(boolean scoreHidden) {
        this.mScoreHidden = scoreHidden;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public void setPermalink(String permalink) {
        this.mPermalink = permalink;
    }

    public String getSubredditNamePrefixed() {
        return mSubredditNamePrefixed;
    }

    public void setSubredditNamePrefixed(String subredditNamePrefixed) {
        this.mSubredditNamePrefixed = subredditNamePrefixed;
    }

    public int getDepth() {
        return mDepth;
    }

    public void setDepth(int depth) {
        this.mDepth = depth;
    }

    @Nullable
    public Comment getParent() {
        return mParent;
    }

    public void setParent(Comment parent) {
        this.mParent = parent;
    }

    public static boolean disconnectFromParent(Comment child) {
        boolean removed = false;
        if (child != null) {
            Comment parent = child.getParent();
            if (parent != null) {
                removed = parent.removeReply(child);
                child.setParent(null);
            }
        }
        return removed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return mScore == comment.mScore &&
                mStickied == comment.mStickied &&
                mScoreHidden == comment.mScoreHidden &&
                mDepth == comment.mDepth &&
                Objects.equals(mSubredditId, comment.mSubredditId) &&
                Objects.equals(mLinkId, comment.mLinkId) &&
                Arrays.equals(mReplies, comment.mReplies) &&
                Objects.equals(mAuthor, comment.mAuthor) &&
                Objects.equals(mParentId, comment.mParentId) &&
                Objects.equals(mBody, comment.mBody) &&
                Objects.equals(mBodyHtml, comment.mBodyHtml) &&
                Objects.equals(mSubreddit, comment.mSubreddit) &&
                Objects.equals(mPermalink, comment.mPermalink) &&
                Objects.equals(mSubredditNamePrefixed, comment.mSubredditNamePrefixed) &&
                Objects.equals(mParent, comment.mParent);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(mSubredditId, mLinkId, mAuthor, mParentId, mScore, mBody, mBodyHtml, mStickied, mSubreddit, mScoreHidden, mPermalink, mSubredditNamePrefixed, mDepth, mParent);
        result = 31 * result + Arrays.hashCode(mReplies);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Comment{" +
                "mLinkId='" + mLinkId + '\'' +
                ", mParentId='" + mParentId + '\'' +
                ", mSubreddit='" + mSubreddit + '\'' +
                ", mDepth=" + mDepth +
                ", mName='" + mName + '\'' +
                ", mId='" + mId + '\'' +
                '}';
    }

    private static final int REPLIES_EXPANDED = FIRST_OBJECT_SPECIFIC_FLAG;
    private static final int IS_DISPLAYED = FIRST_OBJECT_SPECIFIC_FLAG << 1;

    public boolean isRepliesExpanded() {
        return isFlag(getTag(), REPLIES_EXPANDED);
    }

    public void setRepliesExpanded() {
        setFlag(getTag(), REPLIES_EXPANDED);
    }

    public void clearRepliesExpanded() {
        clearFlag(getTag(), REPLIES_EXPANDED);
    }

    public boolean isDisplayed() {
        return isFlag(getTag(), IS_DISPLAYED);
    }

    public void setDisplayed() {
        setFlag(getTag(), IS_DISPLAYED);
    }

    public void clearDisplayed() {
        clearFlag(getTag(), IS_DISPLAYED);
    }

    public boolean isMarked() {
        return isMarked(getTag());
    }

    public void setMarked(boolean marked) {
        setMarked(getTag(), marked);
    }

}
