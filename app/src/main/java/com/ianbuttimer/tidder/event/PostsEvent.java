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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ianbuttimer.tidder.data.ContentProviderResponse;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.ListingTracker;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.get.SubredditLinkResponse;
import com.ianbuttimer.tidder.reddit.get.ThingAboutResponse;
import com.ianbuttimer.tidder.ui.ICommonEvents;

/**
 * Class representing events related to posts activities
 */

public class PostsEvent extends AbstractEvent<PostsEvent, PostsEvent.Event, PostsEvent.EventMode>
                            implements ICommonEvents<PostsEvent, Response> {

    public enum Event {
        FACTORY_INSTANCE,

        /** View Subreddit Post request */
        VIEW_POST_REQUEST,

        /** Get Subreddit Post request */
        GET_POST_REQUEST,
        /** Get Subreddit Post response */
        GET_POST_RESULT,

        /** Get Pinned Post request */
        GET_PINNED_POST_REQUEST,
        /** Get Pinned Post response */
        GET_PINNED_POST_RESULT,

        /** Refresh posts command */
        REFRESH_POSTS_CMD,
        /** Clear posts command */
        CLEAR_POSTS_CMD

    }

    public enum EventMode {
        NEW_REQUEST,
        UPDATE_REQUEST
    }

    private static PostsEvent mFactoryInstance;

    protected static final String NAME_PARAM = "name";
    protected static final String NAMES_PARAM = "names";
    protected static final String SOURCE_PARAM = "source";
    protected static final String TITLE_PARAM = "title";
    protected static final String POSITION_PARAM = "position";



    public PostsEvent(Event event) {
        super(event);
    }

    public PostsEvent(Event event, @Nullable EventMode mode) {
        super(event, mode);
    }

    public static ICommonEvents<PostsEvent, Response> getFactory() {
        if (mFactoryInstance == null) {
            mFactoryInstance = new PostsEvent(Event.FACTORY_INSTANCE);
        }
        return mFactoryInstance;
    }

    @Override
    public ICommonEvents<PostsEvent, Response> getFactoryInstance() {
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
        return new PostsEvent(Event.VIEW_POST_REQUEST)
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
    private static PostsEvent newListingRequest(Event type, String name, String source,
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
        return newListingRequest(Event.GET_POST_REQUEST, name, source, before, after, count, limit);
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
        return newListingRequest(Event.GET_POST_REQUEST, name, source,
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
        return newListingRequest(Event.GET_POST_REQUEST, name, source,
                            tracker.getBefore(), null, tracker.getCount(), limit);
    }

    /**
     * Create a Get Post after request event
     * @param names   Fullnames of posts ro request
     * @return  event object
     */
    public static PostsEvent newGetPinnedPostRequest(String[] names) {
        return new PostsEvent(Event.GET_PINNED_POST_REQUEST, EventMode.NEW_REQUEST)
                .setNames(names);
    }

    /**
     * Create a Refresh Posts Command event
     * @return  event object
     */
    public static PostsEvent newRefreshPostsCommand() {
        return new PostsEvent(Event.REFRESH_POSTS_CMD);
    }

    /**
     * Create a Clear Posts Command event
     * @return  event object
     */
    public static PostsEvent newClearPostsCommand() {
        return new PostsEvent(Event.CLEAR_POSTS_CMD);
    }

    /**
     * Create a new Response result event
     * @param response
     * @return  event object
     */
    @Override
    public PostsEvent newResponseResult(Response response) {
        PostsEvent event = null;
        Event type = null;
        if (response != null) {
            // TODO del before checkin
//            Class rspClass = response.getClass();
//            if (rspClass.equals(SubredditLinkResponse.class)) {
//                type = Event.GET_POST_RESULT;
//            } else if (rspClass.equals(ThingAboutResponse.class)) {
//                type = Event.GET_PINNED_POST_RESULT;
//            }

            Enum eType = response.getEventType();
            if (eType instanceof Event) {
                type = (Event)eType;
                // TODO del before checkin
//                if (!eType.equals(type)) {
//                    throw new IllegalArgumentException("wtf: " + eType + " " + type);
//                }
//            } else {
//                throw new IllegalArgumentException("wtf: " + eType);
            }
        }
        if (type != null) {
            event = new PostsEvent(type);
            event.mSrvResponse = response;
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

    @Nullable
    public ThingAboutResponse getThingResponse() {
        return getResponse(isGetPinnedPostResult(), ThingAboutResponse.class);
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

    public String[] getNames() {
        return getStringArrayParam(NAMES_PARAM);
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

    public PostsEvent setNames(String[] names) {
        mParamMap.put(NAMES_PARAM, names);
        return this;
    }

    public PostsEvent setPosition(int position) {
        mParamMap.put(POSITION_PARAM, position);
        return this;
    }

    public boolean isNewMode() {
        return isMode(EventMode.NEW_REQUEST);
    }

    public boolean isUpdateMode() {
        return isMode(EventMode.UPDATE_REQUEST);
    }

    public boolean isViewPostRequest() {
        return isEvent(Event.VIEW_POST_REQUEST);
    }
    public boolean isGetPostRequest() {
        return isEvent(Event.GET_POST_REQUEST);
    }
    public boolean isGetPostResult() {
        return isEvent(Event.GET_POST_RESULT);
    }
    public boolean isGetPinnedPostRequest() {
        return isEvent(Event.GET_PINNED_POST_REQUEST);
    }
    public boolean isGetPinnedPostResult() {
        return isEvent(Event.GET_PINNED_POST_RESULT);
    }
    public boolean isRefreshPostsCommand() {
        return isEvent(Event.REFRESH_POSTS_CMD);
    }
    public boolean isClearPostsCommand() {
        return isEvent(Event.CLEAR_POSTS_CMD);
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

    }

    @Override
    public AdditionalInfoBuilder infoBuilder(@NonNull PostsEvent event) {
        return new AdditionalInfoBuilder(event);
    }

    @Override
    @Nullable public Bundle additionalInfoTag(@NonNull PostsEvent event) {
        // additional info bundle for request
        return infoBuilder(event)
                .tag()
                .build();
    }

    @Override
    @Nullable public Bundle additionalInfoAll(@NonNull PostsEvent event) {
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
                AbstractEvent.AdditionalInfoExtractor<AdditionalInfoExtractor, PostsEvent, PostsEvent.EventMode>
            implements
                ICommonEvents.IAdditionalInfoExtractor<PostsEvent, AdditionalInfoExtractor> {

        public AdditionalInfoExtractor(PostsEvent event, Bundle bundle) {
            super(event, bundle);
        }

//        @Override
//        public AdditionalInfoExtractor tag() {
//            return (AdditionalInfoExtractor) super.tag();
//        }
//
//        @Override
//        public AdditionalInfoExtractor mode() {
//            return (AdditionalInfoExtractor) super.mode();
//        }
//
        @Override
        public AdditionalInfoExtractor all() {
            return super.all();
        }

    }

    @Override
    public AdditionalInfoExtractor infoExtractor(@NonNull PostsEvent event, @Nullable Bundle bundle) {
        return new AdditionalInfoExtractor(event, bundle);
    }


}
