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

package com.ianbuttimer.tidderish.event;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ianbuttimer.tidderish.data.ContentProviderResponse;
import com.ianbuttimer.tidderish.reddit.BaseObject;
import com.ianbuttimer.tidderish.reddit.Link;
import com.ianbuttimer.tidderish.reddit.ListingTracker;
import com.ianbuttimer.tidderish.reddit.Response;
import com.ianbuttimer.tidderish.reddit.get.SubredditLinkResponse;
import com.ianbuttimer.tidderish.ui.ICommonEvents;

/**
 * Class representing events related to posts activities
 */

public class PostsEvent extends AbstractEvent<PostsEvent>
                            implements ICommonEvents<PostsEvent, Response<? extends BaseObject<?>>> {

    private static PostsEvent mFactoryInstance;

    protected static final String NAME_PARAM = "name";
    protected static final String SOURCE_PARAM = "source";
    protected static final String TITLE_PARAM = "title";
    protected static final String POSITION_PARAM = "position";



    public PostsEvent(@EventType int event) {
        super(event);
    }

    public PostsEvent(@EventType int event, @EventMode int mode) {
        super(event, mode);
    }

    public static ICommonEvents<PostsEvent, Response<? extends BaseObject<?>>> getFactory() {
        if (mFactoryInstance == null) {
            mFactoryInstance = new PostsEvent(EventType.FACTORY_INSTANCE);
        }
        return mFactoryInstance;
    }

    @Override
    public ICommonEvents<PostsEvent, Response<? extends BaseObject<?>>> getFactoryInstance() {
        return getFactory();
    }

    /**
     * Create a new view post request event
     * @param name      Post name
     * @param title     Post title
     * @param link      Post permalink
     * @param position  Adapter position of post
     * @return  event object
     */
    public static PostsEvent newViewPostRequest(String name, String title, String link, int position) {
        return new PostsEvent(EventType.VIEW_POST_REQUEST)
                        .setName(name)
                        .setTitle(title)
                        .setSource(link)
                        .setPosition(position);
    }

    /**
     * Create a new listing request event
     * @param type      Event type
     * @param name      Subreddit name
     * @param source    Post source
     * @param before    before anchor point of the listing slice
     * @param after     after anchor point of the listing slice
     * @param count     number of items already seen in listing
     * @param limit     maximum number of items desired in listing
     * @return  event object
     */
    private static PostsEvent newListingRequest(@EventType int type, String name, String source,
                                                String before, String after, int count, int limit) {
        return new PostsEvent(type).setName(name)
                    .setSource(source)
                    .newListingRequest(before, after, count, limit);
    }

    /**
     * Create a Get Post request event
     * @param name      Subreddit name
     * @param source    Post source          
     * @param before    before anchor point of the listing slice
     * @param after     after anchor point of the listing slice
     * @param count     number of items already seen in listing
     * @param limit     maximum number of items desired in listing
     * @return  event object
     */
    public static PostsEvent newGetPostAfterRequest(String name, String source, String before, String after, int count, int limit) {
        return newListingRequest(EventType.GET_POST_REQUEST, name, source, before, after, count, limit);
    }

    /**
     * Create a Get Post after request event
     * @param name      Subreddit name
     * @param source    Post source
     * @param tracker   Listing tracker
     * @param limit     maximum number of items desired in listing
     * @return  event object
     */
    public static PostsEvent newGetPostAfterRequest(String name, String source, ListingTracker<Link> tracker, int limit) {
        return newListingRequest(EventType.GET_POST_REQUEST, name, source,
                            null, tracker.getAfter(), tracker.getCount(), limit);
    }

    /**
     * Create a Get Post before request event
     * @param name      Subreddit name
     * @param source    Post source
     * @param tracker   Listing tracker
     * @param limit     maximum number of items desired in listing
     * @return  event object
     */
    public static PostsEvent newGetPostBeforeRequest(String name, String source, ListingTracker<Link> tracker, int limit) {
        return newListingRequest(EventType.GET_POST_REQUEST, name, source,
                            tracker.getBefore(), null, tracker.getCount(), limit);
    }

    /**
     * Create a Refresh Posts Command event
     * @return  event object
     */
    public static PostsEvent newRefreshPostsCommand() {
        return new PostsEvent(EventType.REFRESH_POSTS_CMD);
    }

    /**
     * Create a Clear Posts Command event
     * @return  event object
     */
    public static PostsEvent newClearPostsCommand() {
        return new PostsEvent(EventType.CLEAR_POSTS_CMD);
    }

    /**
     * Create a new Response result event
     * @param response  Response
     * @return  event object
     */
    @Override
    public PostsEvent newResponseResult(Response<? extends BaseObject<?>> response) {
        PostsEvent event = null;
        if (response != null) {
            @EventType int type = response.getEventType();
            if (type != EventType.TYPE_NA) {
                event = new PostsEvent(type);
                event.mSrvResponse = response;
            }
        }
        return event;
    }

    @Override
    @Nullable public <T extends ContentProviderResponse> PostsEvent newCpResponseResult(T response) {
        return null;
    }

    @Nullable
    public SubredditLinkResponse getPostResponse() {
        return getResponse(isGetPostResult(), SubredditLinkResponse.class);
    }

    @Override
    protected PostsEvent getThis() {
        return this;
    }

    public String getName() {
        return getStringParam(NAME_PARAM, "");
    }

    public String getSource() {
        return getStringParam(SOURCE_PARAM, "");
    }

    public String getTitle() {
        return getStringParam(TITLE_PARAM, "");
    }

    public int getPosition() {
        return getIntParam(POSITION_PARAM, 0);
    }

    public PostsEvent setName(String name) {
        mParamMap.put(NAME_PARAM, name);
        return this;
    }

    public PostsEvent setSource(String source) {
        mParamMap.put(SOURCE_PARAM, source);
        return this;
    }

    public PostsEvent setTitle(String title) {
        mParamMap.put(TITLE_PARAM, title);
        return this;
    }

    public PostsEvent setPosition(int position) {
        mParamMap.put(POSITION_PARAM, position);
        return this;
    }

    public boolean isViewPostRequest() {
        return isEvent(EventType.VIEW_POST_REQUEST);
    }
    public boolean isGetPostRequest() {
        return isEvent(EventType.GET_POST_REQUEST);
    }
    public boolean isGetPostResult() {
        return isEvent(EventType.GET_POST_RESULT);
    }
    public boolean isRefreshPostsCommand() {
        return isEvent(EventType.REFRESH_POSTS_CMD);
    }
    public boolean isClearPostsCommand() {
        return isEvent(EventType.CLEAR_POSTS_CMD);
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
                AbstractEvent.AdditionalInfoBuilder<AdditionalInfoBuilder, PostsEvent>
            implements
                ICommonEvents.IAdditionalInfoBuilder<AdditionalInfoBuilder> {

        public AdditionalInfoBuilder(PostsEvent event) {
            super(event);
        }

        @Override
        protected AdditionalInfoBuilder getThis() {
            return this;
        }
    }

    @Override
    public AdditionalInfoBuilder infoBuilder(@NonNull PostsEvent event) {
        return new AdditionalInfoBuilder(event);
    }

    @Override
    @Nullable
    public Bundle additionalInfoTag(@NonNull PostsEvent event) {
        // additional info bundle for request
        return infoBuilder(event)
                .tag()
                .build();
    }

    @Override
    @Nullable
    public Bundle additionalInfoAll(@NonNull PostsEvent event) {
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
                AbstractEvent.AdditionalInfoExtractor<AdditionalInfoExtractor, PostsEvent>
            implements
                ICommonEvents.IAdditionalInfoExtractor<PostsEvent, AdditionalInfoExtractor> {

        public AdditionalInfoExtractor(PostsEvent event, Bundle bundle) {
            super(event, bundle);
        }

        @Override
        public AdditionalInfoExtractor all() {
            return super.all();
        }

        @Override
        protected AdditionalInfoExtractor getThis() {
            return this;
        }
    }

    @Override
    public AdditionalInfoExtractor infoExtractor(@NonNull PostsEvent event, @Nullable Bundle bundle) {
        return new AdditionalInfoExtractor(event, bundle);
    }


}
