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
import com.ianbuttimer.tidder.reddit.get.AllSubredditsResponse;
import com.ianbuttimer.tidder.reddit.get.SubredditsSearchResponse;
import com.ianbuttimer.tidder.reddit.post.ApiSearchSubredditsResponse;
import com.ianbuttimer.tidder.ui.ICommonEvents;


/**
 * Class representing events related to follow activities
 */

public class FollowEvent extends AbstractEvent<FollowEvent>
                            implements ICommonEvents<FollowEvent, Response<? extends BaseObject<?>>> {

    private static FollowEvent mFactoryInstance;

    protected static final String QUERY_PARAM = "query";
    protected static final String FOLLOW_PARAM = "follow";
    protected static final String NAME_PARAM = "name";
    protected static final String KEY_COLOUR_PARAM = "key_colour";
    protected static final String ICON_PARAM = "icon";


    public FollowEvent(@EventType int event) {
        super(event);
    }

    public FollowEvent(@EventType int event, @EventMode int mode) {
        super(event, mode);
    }

    public static ICommonEvents<FollowEvent, Response<? extends BaseObject<?>>> getFactory() {
        if (mFactoryInstance == null) {
            mFactoryInstance = new FollowEvent(EventType.FACTORY_INSTANCE);
        }
        return mFactoryInstance;
    }

    @Override
    public ICommonEvents<FollowEvent, Response<? extends BaseObject<?>>> getFactoryInstance() {
        return getFactory();
    }

    /**
     * Create a new Search Interest Request event
     * @param query
     * @return  event object
     */
    public static FollowEvent newSearchInterestRequest(String query) {
        return newSearchInterestRequest(query, null, null, 0);
    }

    /**
     * Create a new listing request event
     * @param type      Event type
     * @param query     Request query
     * @param before    before anchor point of the listing slice
     * @param after     after anchor point of the listing slice
     * @param count     number of items already seen in listing
     * @return  event object
     */
    private static FollowEvent newListingRequest(@EventType int type, String query, String before, String after, int count) {
        FollowEvent event = new FollowEvent(type);
        return event.newListingRequest(before, after, count)
                .setQuery(query);
    }

    /**
     * Create a new Search Interest Request event
     * @param query     Request query
     * @param before    before anchor point of the listing slice
     * @param after     after anchor point of the listing slice
     * @param count     number of items already seen in listing
     * @return  event object
     */
    public static FollowEvent newSearchInterestRequest(String query, String before, String after, int count) {
        return newListingRequest(EventType.SEARCH_INTEREST_REQUEST, query, before, after, count);
    }

    /**
     * Create a new Search Name Request event
     * @param query     Request query
     * @return  event object
     */
    public static FollowEvent newSearchNameRequest(String query) {
        return new FollowEvent(EventType.SEARCH_NAME_REQUEST).setQuery(query);
    }

    /**
     * Create a new Response result event
     * @param response  Api response
     * @return  event object
     */
    @Override
    public FollowEvent newResponseResult(Response response) {
        FollowEvent event = null;
        if (response != null) {
            @EventType int type = response.getEventType();
            if (type != EventType.TYPE_NA) {
                event = new FollowEvent(type);
                event.mSrvResponse = response;
            }
        }
        return event;
    }

    @Nullable
    @Override
    public <T extends ContentProviderResponse> FollowEvent newCpResponseResult(T response) {
        return null;
    }

    /**
     * Create a new All Subreddit Request event
     * @return  event object
     */
    public static FollowEvent newAllSubredditRequest(String query, String before, String after, int count) {
        return newListingRequest(EventType.ALL_SUBREDDIT_REQUEST, query, before, after, count);
    }

    /**
     * Create a new Follow State Change Request event
     * @return  event object
     */
    public static FollowEvent newFollowStateChangeRequest(boolean follow, String displayName, int keyColour, String icon) {
        FollowEvent event = new FollowEvent(EventType.FOLLOW_STATE_CHANGE_REQUEST);
        return event.setFollow(follow)
            .setName(displayName)
            .setKeyColour(keyColour)
            .setIcon(icon);
    }

    @Override
    protected FollowEvent getThis() {
        return this;
    }

    public String getQuery() {
        return getStringParam(QUERY_PARAM, "");
    }

    public FollowEvent setQuery(String query) {
        mParamMap.put(QUERY_PARAM, query);
        return this;
    }

    public boolean getFollow() {
        return getBooleanParam(FOLLOW_PARAM, false);
    }

    public FollowEvent setFollow(boolean follow) {
        mParamMap.put(FOLLOW_PARAM, follow);
        return this;
    }

    public String getName() {
        return getStringParam(NAME_PARAM, "");
    }

    public FollowEvent setName(String name) {
        mParamMap.put(NAME_PARAM, name);
        return this;
    }

    public int getKeyColour() {
        return getIntParam(KEY_COLOUR_PARAM, 0);
    }

    public FollowEvent setKeyColour(int keyColour) {
        mParamMap.put(KEY_COLOUR_PARAM, keyColour);
        return this;
    }

    public String getIcon() {
        return getStringParam(ICON_PARAM, "");
    }

    public FollowEvent setIcon(String icon) {
        mParamMap.put(ICON_PARAM, icon);
        return this;
    }

    @Nullable
    public SubredditsSearchResponse getSearchInterestResponse() {
        return getResponse(isSearchInterestResult(), SubredditsSearchResponse.class);
    }

    @Nullable
    public ApiSearchSubredditsResponse getSearchNameResponse() {
        return getResponse(isSearchNameResult(), ApiSearchSubredditsResponse.class);
    }

    @Nullable
    public AllSubredditsResponse getAllResponse() {
        return getResponse(isAllSubredditResult(), AllSubredditsResponse.class);
    }

    public boolean isSearchInterestRequest() {
        return isEvent(EventType.SEARCH_INTEREST_REQUEST);
    }
    public boolean isSearchInterestResult() {
        return isEvent(EventType.SEARCH_INTEREST_RESULT);
    }
    public boolean isSearchNameRequest() {
        return isEvent(EventType.SEARCH_NAME_REQUEST);
    }
    public boolean isSearchNameResult() {
        return isEvent(EventType.SEARCH_NAME_RESULT);
    }
    public boolean isAllSubredditRequest() {
        return isEvent(EventType.ALL_SUBREDDIT_REQUEST);
    }
    public boolean isAllSubredditResult() {
        return isEvent(EventType.ALL_SUBREDDIT_RESULT);
    }
    public boolean isFollowStateChangeRequest() {
        return isEvent(EventType.FOLLOW_STATE_CHANGE_REQUEST);
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
                AbstractEvent.AdditionalInfoBuilder<AdditionalInfoBuilder, FollowEvent>
            implements
                ICommonEvents.IAdditionalInfoBuilder<AdditionalInfoBuilder> {

        public AdditionalInfoBuilder(FollowEvent event) {
            super(event);
        }

        @Override
        protected AdditionalInfoBuilder getThis() {
            return this;
        }
    }

    @Override
    public AdditionalInfoBuilder infoBuilder(@NonNull FollowEvent event) {
        return new AdditionalInfoBuilder(event);
    }

    @Override
    @Nullable public Bundle additionalInfoTag(@NonNull FollowEvent event) {
        // additional info bundle for request
        return infoBuilder(event)
                .tag()
                .build();
    }

    @Override
    @Nullable public Bundle additionalInfoAll(@NonNull FollowEvent event) {
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
                AbstractEvent.AdditionalInfoExtractor<AdditionalInfoExtractor, FollowEvent>
            implements
                ICommonEvents.IAdditionalInfoExtractor<FollowEvent, AdditionalInfoExtractor> {

        public AdditionalInfoExtractor(FollowEvent event, Bundle bundle) {
            super(event, bundle);
        }

        @Override
        protected AdditionalInfoExtractor getThis() {
            return this;
        }
    }

    @Override
    public AdditionalInfoExtractor infoExtractor(@NonNull FollowEvent event, @Nullable Bundle bundle) {
        return new AdditionalInfoExtractor(event, bundle);
    }

}
