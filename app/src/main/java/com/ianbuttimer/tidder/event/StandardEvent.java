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
import android.util.Pair;

import com.ianbuttimer.tidder.data.ConfigQueryResponse;
import com.ianbuttimer.tidder.data.ContentProviderResponse;
import com.ianbuttimer.tidder.data.FollowQueryResponse;
import com.ianbuttimer.tidder.data.PinnedQueryResponse;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.Subreddit;
import com.ianbuttimer.tidder.reddit.get.SubredditAboutResponse;
import com.ianbuttimer.tidder.reddit.get.ThingAboutResponse;
import com.ianbuttimer.tidder.ui.ICommonEvents;

import java.util.ArrayList;

/**
 * Class representing events related to standard activities
 */

public class StandardEvent extends AbstractEvent<StandardEvent, StandardEvent.Event, StandardEvent.EventMode>
                            implements ICommonEvents<StandardEvent, Response> {

    public enum Event {
        FACTORY_INSTANCE,

        /** Query Follow Status of Subreddit List request */
        QUERY_FOLLOW_STATUS_LIST_REQUEST,

        /** Get Subreddit Following List request */
        FOLLOWING_LIST_REQUEST,
        /** Get Subreddit Following List response */
        FOLLOWING_LIST_RESULT,

        /** Get Pinned List request */
        PINNED_LIST_REQUEST,
        /** Get Pinned List response */
        PINNED_LIST_RESULT,

        /** Subreddit info request */
        SUBREDDIT_INFO_REQUEST,
        /** Subreddit info result */
        SUBREDDIT_INFO_RESULT,

        /** Subreddit/Comment info request */
        THING_ABOUT_REQUEST,
        /** Subreddit/Comment info result */
        THING_ABOUT_RESULT,

        // full variant specific events

        /** Settings info request */
        SETTINGS_REQUEST,
        /** Settings info result */
        SETTINGS_RESULT

    }


    public enum EventMode {
        NEW_REQUEST,
        UPDATE_REQUEST
    }

    private static StandardEvent mFactoryInstance;

    public static final String RANGE_START_INFO = "range_start_additional_info";
    public static final String RANGE_END_INFO = "range_end_additional_info";

    protected static final String NAME_PARAM = "name";
    protected static final String NAMES_PARAM = "names";
    protected static final String LIST_PARAM = "list";
    protected static final String START_PARAM = "start";
    protected static final String END_PARAM = "end";

    public StandardEvent(Event event) {
        super(event);
    }

    public StandardEvent(Event event, @Nullable EventMode mode) {
        super(event, mode);
    }

    public static ICommonEvents<StandardEvent, Response> getFactory() {
        if (mFactoryInstance == null) {
            mFactoryInstance = new StandardEvent(Event.FACTORY_INSTANCE);
        }
        return mFactoryInstance;
    }

    @Override
    public ICommonEvents<StandardEvent, Response> getFactoryInstance() {
        return getFactory();
    }

    /**
     * Create a Get Subreddit Following List event
     * @return  event object
     */
    public static StandardEvent newFollowingListRequest() {
        return new StandardEvent(Event.FOLLOWING_LIST_REQUEST, EventMode.NEW_REQUEST);
    }

    /**
     * Create an Update Subreddit Following List event
     * @return  event object
     */
    public static StandardEvent newUpdateFollowingListRequest() {
        return new StandardEvent(Event.FOLLOWING_LIST_REQUEST, EventMode.UPDATE_REQUEST);
    }

    /**
     * Create a new Query Follow Status of List event
     * @param list  List of subreddits to check status of
     * @param start Start position of list in overall list
     * @param end   End position of list in overall list (inclusive)
     * @return  event object
     */
    public static StandardEvent newFollowingListRequest(ArrayList<Subreddit> list, int start, int end) {
        StandardEvent event = new StandardEvent(Event.QUERY_FOLLOW_STATUS_LIST_REQUEST);
        return event.setList(list)
                .setStart(start)
                .setEnd(end);
    }

    /**
     * Create a Get Pinned List event
     * @return  event object
     */
    public static StandardEvent newPinnedListRequest() {
        return newPinnedListRequest(null);
    }

    /**
     * Create a Get Pinned List event
     * @param name    Fullname of object
     * @return  event object
     */
    public static StandardEvent newPinnedListRequest(String name) {
        return newPinnedListRequest(EventMode.NEW_REQUEST, name);
    }

    /**
     * Create an update Get Pinned List event
     * @return  event object
     */
    public static StandardEvent newUpdatePinnedListRequest() {
        return newUpdatePinnedListRequest(null);
    }

    /**
     * Create an update Get Pinned List event
     * @param name    Fullname of object
     * @return  event object
     */
    public static StandardEvent newUpdatePinnedListRequest(String name) {
        return newPinnedListRequest(EventMode.UPDATE_REQUEST, name);
    }

    /**
     * Create an Get Pinned List event
     * @param name    Fullname of object
     *
     * @return  event object
     */
    private static StandardEvent newPinnedListRequest(EventMode mode, String name) {
        StandardEvent event = new StandardEvent(Event.PINNED_LIST_REQUEST, mode);
        return event.setName(name);
    }

    /**
     * Create a new Subreddit Info Request event
     * @param name  Subreddit name
     * @return  event object
     */
    public static StandardEvent newSubredditInfoRequest(String name) {
        StandardEvent event = new StandardEvent(Event.SUBREDDIT_INFO_REQUEST);
        return event.setName(name);
    }

    /**
     * Create a new Setting Info Request event
     * @return  event object
     */
    public static StandardEvent newSettingsRequest() {
        return new StandardEvent(Event.SETTINGS_REQUEST);
    }

    /**
     * Create a new Setting Info Result event
     * @return  event object
     */
    public static StandardEvent newSettingsResult() {
        return new StandardEvent(Event.SETTINGS_RESULT);
    }

    /**
     * Create a Get Thing About request event
     * @param name  Fullname of item ro request
     * @return  event object
     */
    public static StandardEvent newThingAboutRequest(String name) {
        return newThingAboutRequest(new String[] { name });
    }

    /**
     * Create a Get Thing About request event
     * @param names   Fullnames of items ro request
     * @return  event object
     */
    public static StandardEvent newThingAboutRequest(String[] names) {
        return new StandardEvent(Event.THING_ABOUT_REQUEST, EventMode.NEW_REQUEST)
                .setNames(names);
    }

    /**
     * Create a new Response result event
     * @param response
     * @return  event object
     */
    @Override
    @Nullable public StandardEvent newResponseResult(Response response) {
        StandardEvent event = null;
        Event type = null;
        if (response != null) {
            Enum eType = response.getEventType();
            if (eType instanceof Event) {
                type = (Event)eType;
            }
        }
        if (type != null) {
            event = new StandardEvent(type);
            event.mSrvResponse = response;
        }
        return event;
    }

    @Override
    @Nullable public <T extends ContentProviderResponse> StandardEvent newCpResponseResult(T response) {
        StandardEvent event = null;
        Event type = null;
        if (response != null) {
            Enum eType = response.getEventType();
            if (eType instanceof Event) {
                type = (Event)eType;
            }
        }
        if (type != null) {
            event = new StandardEvent(type);
            event.mCpResponse = response;
        }
        return event;
    }

    @Override
    protected StandardEvent getThis() {
        return this;
    }

    @Nullable
    public SubredditAboutResponse getSubredditAboutResponse() {
        return getResponse(isSubredditInfoResult(), SubredditAboutResponse.class);
    }

    @Nullable
    public FollowQueryResponse getFollowQueryResponse() {
        return getContentProviderResponse(isFollowingListResult(), FollowQueryResponse.class);
    }

    @Nullable
    public PinnedQueryResponse getPinnedQueryResponse() {
        return getContentProviderResponse(isPinnedListResult(), PinnedQueryResponse.class);
    }

    @Nullable
    public ConfigQueryResponse getConfigQueryResponse() {
        return getContentProviderResponse(isSettingsResult(), ConfigQueryResponse.class);
    }

    @Nullable
    public ThingAboutResponse getThingAboutResponse() {
        return getResponse(isThingAboutResult(), ThingAboutResponse.class);
    }

    public Pair<Integer, Integer> getRange() {
        return new Pair<>(getStart(), getEnd());
    }

    public String getName() {
        return getStringParam(NAME_PARAM, "");
    }

    public String[] getNames() {
        return getStringArrayParam(NAMES_PARAM);
    }

    public ArrayList<Subreddit> getList() {
        ArrayList<Subreddit> list = (ArrayList<Subreddit>) mParamMap.get(LIST_PARAM);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public int getStart() {
        return getIntParam(START_PARAM, 0);
    }

    public int getEnd() {
        return getIntParam(END_PARAM, 0);
    }

    public StandardEvent setName(String name) {
        mParamMap.put(NAME_PARAM, name);
        return this;
    }

    public StandardEvent setNames(String[] names) {
        mParamMap.put(NAMES_PARAM, names);
        return this;
    }

    public StandardEvent setList(ArrayList<Subreddit> list) {
        mParamMap.put(LIST_PARAM, list);
        return this;
    }

    public StandardEvent setStart(int start) {
        mParamMap.put(START_PARAM, start);
        return this;
    }

    public StandardEvent setEnd(int end) {
        mParamMap.put(END_PARAM, end);
        return this;
    }

    public boolean isNewMode() {
        return isMode(EventMode.NEW_REQUEST);
    }

    public boolean isUpdateMode() {
        return isMode(EventMode.UPDATE_REQUEST);
    }

    public boolean isQueryFollowStatusListRequest() {
        return isEvent(Event.QUERY_FOLLOW_STATUS_LIST_REQUEST);
    }
    public boolean isFollowingListRequest() {
        return isEvent(Event.FOLLOWING_LIST_REQUEST);
    }
    public boolean isFollowingListResult() {
        return isEvent(Event.FOLLOWING_LIST_RESULT);
    }
    public boolean isPinnedListRequest() {
        return isEvent(Event.PINNED_LIST_REQUEST);
    }
    public boolean isPinnedListResult() {
        return isEvent(Event.PINNED_LIST_RESULT);
    }
    public boolean isSubredditInfoRequest() {
        return isEvent(Event.SUBREDDIT_INFO_REQUEST);
    }
    public boolean isSubredditInfoResult() {
        return isEvent(Event.SUBREDDIT_INFO_RESULT);
    }
    public boolean isSettingsRequest() {
        return isEvent(Event.SETTINGS_REQUEST);
    }
    public boolean isSettingsResult() {
        return isEvent(Event.SETTINGS_RESULT);
    }
    public boolean isThingAboutRequest() {
        return isEvent(Event.THING_ABOUT_REQUEST);
    }
    public boolean isThingAboutResult() {
        return isEvent(Event.THING_ABOUT_RESULT);
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
                AbstractEvent.AdditionalInfoBuilder<AdditionalInfoBuilder, StandardEvent>
            implements
                IAdditionalInfoBuilder<AdditionalInfoBuilder> {

        public AdditionalInfoBuilder(StandardEvent event) {
            super(event);
        }

        public AdditionalInfoBuilder range() {
            mBundle.putInt(RANGE_START_INFO, mEvent.getStart());
            mBundle.putInt(RANGE_END_INFO, mEvent.getEnd());
            return this;
        }

        @Override
        public AdditionalInfoBuilder all() {
            return super.all().range();
        }
    }

    @Override
    public AdditionalInfoBuilder infoBuilder(@NonNull StandardEvent event) {
        return new AdditionalInfoBuilder(event);
    }

    @Override
    @Nullable public Bundle additionalInfoTag(@NonNull StandardEvent event) {
        // additional info bundle for request
        return infoBuilder(event)
                .tag()
                .build();
    }

    @Override
    @Nullable public Bundle additionalInfoAll(@NonNull StandardEvent event) {
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
                AbstractEvent.AdditionalInfoExtractor<AdditionalInfoExtractor, StandardEvent, StandardEvent.EventMode>
            implements
                IAdditionalInfoExtractor<StandardEvent, AdditionalInfoExtractor> {

        public AdditionalInfoExtractor(StandardEvent event, Bundle bundle) {
            super(event, bundle);
        }

        @SuppressWarnings("ConstantConditions")
        public AdditionalInfoExtractor range() {
            if (proceed()) {
                mEvent.setStart(mBundle.getInt(RANGE_START_INFO));
                mEvent.setEnd(mBundle.getInt(RANGE_END_INFO));
            }
            return this;
        }

        @Override
        public AdditionalInfoExtractor all() {
            return super.all().range();
        }
    }

    @Override
    public AdditionalInfoExtractor infoExtractor(@NonNull StandardEvent event, @Nullable Bundle bundle) {
        return new AdditionalInfoExtractor(event, bundle);
    }


}
