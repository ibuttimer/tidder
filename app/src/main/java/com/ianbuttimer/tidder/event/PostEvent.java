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

package com.ianbuttimer.tidder.event;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ianbuttimer.tidder.data.ContentProviderResponse;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.get.CommentMoreResponse;
import com.ianbuttimer.tidder.reddit.get.CommentTreeResponse;
import com.ianbuttimer.tidder.ui.ICommonEvents;


/**
 * Class representing events related to post activities
 */

public class PostEvent extends AbstractEvent<PostEvent>
                            implements ICommonEvents<PostEvent, Response<? extends BaseObject<?>>> {

    private static PostEvent mFactoryInstance;

    public static final String POSITION_INFO = "position_additional_info";
    public static final String RANGE_START_INFO = "range_start_additional_info";
    public static final String RANGE_END_INFO = "range_end_additional_info";


    protected static final String PERMALINK_PARAM = "permalink";
    protected static final String ID_PARAM = "id";
    protected static final String CHILDREN_PARAM = "children";
    protected static final String LINK_ID_PARAM = "link_id";
    protected static final String POSITION_PARAM = "position";
    protected static final String NAME_PARAM = "name";
    protected static final String TITLE_PARAM = "title";

    public PostEvent(@EventType int event) {
        super(event);
    }

    public PostEvent(@EventType int event, @EventMode int mode) {
        super(event, mode);
    }

    public static ICommonEvents<PostEvent, Response<? extends BaseObject<?>>> getFactory() {
        if (mFactoryInstance == null) {
            mFactoryInstance = new PostEvent(EventType.FACTORY_INSTANCE);
        }
        return mFactoryInstance;
    }

    @Override
    public ICommonEvents<PostEvent, Response<? extends BaseObject<?>>> getFactoryInstance() {
        return getFactory();
    }


    /**
     * Create a new view comment request event
     * @return  event object
     */
    public static PostEvent newViewThreadRequest() {
        return newViewThreadRequest("", "", "");
    }

    /**
     * Create a new view comment request event
     * @param name      Post name
     * @param title     Post title
     * @param link      Post permalink
     * @return  event object
     */
    public static PostEvent newViewThreadRequest(String name, String title, String link) {
        PostEvent event = new PostEvent(EventType.VIEW_THREAD_REQUEST);
        event.setName(name);
        event.setTitle(title);
        event.setPermalink(link);
        return event;
    }

    /**
     * Create a Get Comment Tree request event
     * @param permalink permalink
     * @param limit     the maximum number of comments to return
     * @return  event object
     */
    public static PostEvent newGetCommentTreeRequest(String permalink, int limit) {
        PostEvent event = new PostEvent(EventType.GET_COMMENT_TREE_REQUEST);
        event.setPermalink(permalink);
        event.setLimit(limit);
        return event;
    }

    /**
     * Create a Get Comment More request event
     * @param id
     * @param linkId
     * @param children
     * @return  event object
     */
    public static PostEvent newGetCommentMoreRequest(String id, String linkId, String[] children, int position) {
        PostEvent event = new PostEvent(EventType.GET_COMMENT_MORE_REQUEST);
        event.setId(id);
        event.setLinkId(linkId);
        event.setChildren(children);
        event.setPosition(position);
        return event;
    }

    /**
     * Create a Pinned Status Change event
     * @return  event object
     */
    public static PostEvent newPinnedStatusChange() {
        return new PostEvent(EventType.PINNED_STATUS_CHANGE);
    }

    /**
     * Create a new Response result event
     * @param response  Response
     * @return  event object
     */
    @Override
    public PostEvent newResponseResult(Response<? extends BaseObject<?>> response) {
        PostEvent event = null;
        if (response != null) {
            @EventType int type = response.getEventType();
            if (type != EventType.TYPE_NA) {
                event = new PostEvent(type);
                event.mSrvResponse = response;
            }
        }
        return event;
    }

    @Override
    @Nullable public <T extends ContentProviderResponse> PostEvent newCpResponseResult(T response) {
        return null;
    }

    @Override
    protected PostEvent getThis() {
        return this;
    }

    @Nullable
    public CommentTreeResponse getCommentTreeResponse() {
        return getResponse(isGetCommentTreeResult(), CommentTreeResponse.class);
    }

    @Nullable
    public CommentMoreResponse getCommentMoreResponse() {
        return getResponse(isGetCommentMoreResult(), CommentMoreResponse.class);
    }

    public String getPermalink() {
        return getStringParam(PERMALINK_PARAM, "");
    }

    public String getId() {
        return getStringParam(ID_PARAM, "");
    }

    public String[] getChildren() {
        return getStringArrayParam(CHILDREN_PARAM);
    }

    public String getLinkId() {
        return getStringParam(LINK_ID_PARAM, "");
    }

    public int getPosition() {
        return getIntParam(POSITION_PARAM, 0);
    }

    public String getName() {
        return getStringParam(NAME_PARAM, "");
    }

    public String getTitle() {
        return getStringParam(TITLE_PARAM, "");
    }

    public void setPermalink(String permalink) {
        mParamMap.put(PERMALINK_PARAM, permalink);
    }

    public void setId(String id) {
        mParamMap.put(ID_PARAM, id);
    }

    public void setChildren(String[] children) {
        mParamMap.put(CHILDREN_PARAM, children);
    }

    public void setLinkId(String linkId) {
        mParamMap.put(LINK_ID_PARAM, linkId);
    }

    public void setPosition(int position) {
        mParamMap.put(POSITION_PARAM, position);
    }

    public void setName(String name) {
        mParamMap.put(NAME_PARAM, name);
    }

    public void setTitle(String title) {
        mParamMap.put(TITLE_PARAM, title);
    }

    public boolean isViewThreadRequest() {
        return isEvent(EventType.VIEW_THREAD_REQUEST);
    }
    public boolean isGetCommentTreeRequest() {
        return isEvent(EventType.GET_COMMENT_TREE_REQUEST);
    }
    public boolean isGetCommentTreeResult() {
        return isEvent(EventType.GET_COMMENT_TREE_RESULT);
    }
    public boolean isGetCommentMoreRequest() {
        return isEvent(EventType.GET_COMMENT_MORE_REQUEST);
    }
    public boolean isGetCommentMoreResult() {
        return isEvent(EventType.GET_COMMENT_MORE_RESULT);
    }
    public boolean isPinnedStatusChange() {
        return isEvent(EventType.PINNED_STATUS_CHANGE);
    }

    @Override
    protected String toStringExtra() {
        return null;
    }

    /**
     * Builder class for Additional Info bundles
     */
    public static class AdditionalInfoBuilder
            extends
                AbstractEvent.AdditionalInfoBuilder<AdditionalInfoBuilder, PostEvent>
            implements
                IAdditionalInfoBuilder<AdditionalInfoBuilder> {

        public AdditionalInfoBuilder(PostEvent event) {
            super(event);
        }

        @Override
        protected AdditionalInfoBuilder getThis() {
            return this;
        }

        public AdditionalInfoBuilder position() {
            mBundle.putInt(POSITION_INFO, mEvent.getPosition());
            return this;
        }

        @Override
        public AdditionalInfoBuilder all() {
            return super.all().position();
        }
    }

    @Override
    public AdditionalInfoBuilder infoBuilder(@NonNull PostEvent event) {
        return new AdditionalInfoBuilder(event);
    }

    @Override
    @Nullable public Bundle additionalInfoTag(@NonNull PostEvent event) {
        // additional info bundle for request
        return infoBuilder(event)
                .tag()
                .build();
    }

    @Override
    @Nullable public Bundle additionalInfoAll(@NonNull PostEvent event) {
        // additional info bundle for request
        return infoBuilder(event)
                .all()
                .build();
    }

    /**
     * Extractor class for Additional Info bundles
     */
    public static class AdditionalInfoExtractor
            extends
                AbstractEvent.AdditionalInfoExtractor<AdditionalInfoExtractor, PostEvent>
            implements
                IAdditionalInfoExtractor<PostEvent, AdditionalInfoExtractor> {

        public AdditionalInfoExtractor(PostEvent event, Bundle bundle) {
            super(event, bundle);
        }

        @Override
        protected AdditionalInfoExtractor getThis() {
            return this;
        }

        @SuppressWarnings("ConstantConditions")
        public AdditionalInfoExtractor position() {
            if (proceed()) {
                mEvent.setPosition(mBundle.getInt(POSITION_INFO));
            }
            return this;
        }

        @Override
        public AdditionalInfoExtractor all() {
            return super.all().position();
        }

    }

    @Override
    public AdditionalInfoExtractor infoExtractor(@NonNull PostEvent event, @Nullable Bundle bundle) {
        return new AdditionalInfoExtractor(event, bundle);
    }


}
