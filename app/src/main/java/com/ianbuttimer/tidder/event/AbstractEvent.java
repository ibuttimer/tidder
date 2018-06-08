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
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Joiner;
import com.ianbuttimer.tidder.data.ContentProviderResponse;
import com.ianbuttimer.tidder.reddit.ListingList;
import com.ianbuttimer.tidder.reddit.Response;

import java.util.HashMap;

/**
 * Base class representing events<br>
 * The parameter <code>T</code> represents the enum for the event types utilised.
 * The parameter <code>M</code> represents the enum for the mode modifier for the event types.
 */

public abstract class AbstractEvent<E extends AbstractEvent, T extends Enum, M extends Enum> {

    protected static final String DESTINATION_INFO = "dest_additional_info";
    protected static final String MODE_INFO = "mode_additional_info";

    private static final String TAG_SEPARATOR = ",";

    private T mEvent;

    @Nullable private M mMode;

    protected HashMap<String, Object> mParamMap;

    // tag indicating event response destination
    protected static final String DESTINATION_PARAM = "destination";
    // before item in the listing to use as the anchor point of the slice
    protected static final String BEFORE_PARAM = "before";
    // after item in the listing to use as the anchor point of the slice
    protected static final String AFTER_PARAM = "after";
    // the number of items already seen in listing
    protected static final String COUNT_PARAM = "count";
    // the maximum number of items desired in listing
    protected static final String LIMIT_PARAM = "limit";

    protected ContentProviderResponse mCpResponse;

    protected Response mSrvResponse;


    public AbstractEvent(T event) {
        this(event, null);
    }

    public AbstractEvent(T event, @Nullable M mode) {
        if (event == null) {
            throw new IllegalArgumentException("Event must be specified");
        }
        this.mEvent = event;
        this.mMode = mode;
        this.mParamMap = new HashMap<>();
    }

    protected void init() {
        this.mMode = null;
        this.mParamMap = new HashMap<>();
    }

    protected abstract E getThis();

    public T getEvent() {
        return mEvent;
    }

    protected E setEvent(T event) {
        mEvent = event;
        return getThis();
    }

    @Nullable
    public M getMode() {
        return mMode;
    }

    protected E setMode(@Nullable M mode) {
        mMode = mode;
        return getThis();
    }

    protected String getStringParam(String key, String dfltValue) {
        String param = (String) mParamMap.get(key);
        if (param == null) {
            param = dfltValue;
        }
        return param;
    }

    protected int getIntParam(String key, int dfltValue) {
        Integer param = (Integer) mParamMap.get(key);
        if (param == null) {
            param = dfltValue;
        }
        return param;
    }

    protected boolean getBooleanParam(String key, boolean dfltValue) {
        Boolean param = (Boolean) mParamMap.get(key);
        if (param == null) {
            param = dfltValue;
        }
        return param;
    }

    protected String[] getStringArrayParam(String key) {
        String[] param = (String[]) mParamMap.get(key);
        if (param == null) {
            param = new String[0];
        }
        return param;
    }

    public String getAddress() {
        return getStringParam(DESTINATION_PARAM, "");
    }

    public String[] getAddresss() {
        String[] tags;
        String destination = getAddress();
        if (!TextUtils.isEmpty(destination)) {
            tags = destination.split(TAG_SEPARATOR);
        } else {
            tags = new String[0];
        }
        return tags;
    }

    public E addAddress(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            String destination = getAddress();
            if (TextUtils.isEmpty(destination)) {
                destination = tag;
            } else {
                destination += TAG_SEPARATOR + tag;
            }
            setAddress(destination);
        }
        return getThis();
    }

    public E addAddress(String... tags) {
        if ((tags != null) && (tags.length > 0)) {
            String destination = Joiner.on(TAG_SEPARATOR).join(tags);
            addAddress(destination);
        }
        return getThis();
    }

    public E setAddress(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            mParamMap.put(DESTINATION_PARAM, tag);
        }
        return getThis();
    }

    public E setAddress(String... tags) {
        for (String tag : tags) {
            String destination = Joiner.on(TAG_SEPARATOR).join(tags);
            addAddress(destination);
        }
        return getThis();
    }

    public boolean isBroadcast() {
        return TextUtils.isEmpty(getAddress());
    }

    public boolean isForTag(String tag) {
        boolean is = false;
        String destination = getAddress();
        if (!TextUtils.isEmpty(destination) && !TextUtils.isEmpty(tag)) {
            is = destination.contains(tag);
        }
        return is;
    }

    public String getBefore() {
        return getStringParam(BEFORE_PARAM, "");
    }

    public String getAfter() {
        return getStringParam(AFTER_PARAM, "");
    }

    public int getCount() {
        return getIntParam(COUNT_PARAM, 0);
    }

    public int getLimit() {
        return getIntParam(LIMIT_PARAM, 0);
    }

    protected E setBefore(String before) {
        mParamMap.put(BEFORE_PARAM, before);
        return getThis();
    }

    protected E setAfter(String after) {
        mParamMap.put(AFTER_PARAM, after);
        return getThis();
    }

    protected E setCount(int count) {
        mParamMap.put(COUNT_PARAM, count);
        return getThis();
    }

    protected E setLimit(int limit) {
        mParamMap.put(LIMIT_PARAM, limit);
        return getThis();
    }

    @Nullable
    protected <T extends Response> T getResponse(boolean valid, Class<T> tClass) {
        T response = null;
        if (valid) {
            response = tClass.cast(mSrvResponse);
        }
        return response;
    }

    @Nullable
    protected <T extends ContentProviderResponse> T getContentProviderResponse(boolean valid, Class<T> tClass) {
        T response = null;
        if (valid) {
            response = tClass.cast(mCpResponse);
        }
        return response;
    }


    public boolean isEvent(T event) {
        boolean is = false;
        if (event != null) {
            is = event.equals(mEvent);
        }
        return is;
    }

    public boolean isMode(M mode) {
        boolean is = false;
        if (mode != null) {
            is = mode.equals(mMode);
        }
        return is;
    }

    /**
     * Create a new listing request event
     * @param before    before anchor point of the listing slice
     * @param after     after anchor point of the listing slice
     * @param count     number of items already seen in listing
     * @return  event object
     */
    protected E newListingRequest(String before, String after, int count) {
        return newListingRequest(before, after, count, 0);
    }

    /**
     * Create a new listing request event
     * @param before    before anchor point of the listing slice
     * @param after     after anchor point of the listing slice
     * @param count     number of items already seen in listing
     * @param limit     maximum number of items desired in listing
     * @return  event object
     */
    protected E newListingRequest(String before, String after, int count, int limit) {
        setBefore(before);
        setAfter(after);
        setCount(count);
        setLimit(limit);
        return getThis();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('{');

        T event = getEvent();
        sb.append(event != null ? event : "null event");

        M mode = getMode();
        sb.append(" mode=").append(mode != null ? mode : "na");

        sb.append(" tag=").append(getAddress());

        if (mSrvResponse instanceof ListingList) {
            sb.append(" entries=").append(((ListingList) mSrvResponse).getCount());
        }

        String extra = toStringExtra();
        if (!TextUtils.isEmpty(extra)) {
            sb.append(' ').append(extra);
        }

        return sb.append('}').toString();
    }

    protected abstract String toStringExtra();

    /**
     * Builder class for Additional Info bundles<br>
     * The parameter <code>B</code> represents the class of the builder.
     * The parameter <code>T</code> represents the class for the event.
     */
    public abstract static class AdditionalInfoBuilder<B extends AdditionalInfoBuilder, T extends AbstractEvent> {

        protected Bundle mBundle;
        protected T mEvent;

        public AdditionalInfoBuilder(T event) {
            if (event == null) {
                throw new IllegalArgumentException("Event required");
            }
            mBundle = new Bundle();
            mEvent = event;
        }

        public B tag() {
            mBundle.putString(DESTINATION_INFO, mEvent.getAddress());
            return (B)this;
        }

        public B mode() {
            mBundle.putSerializable(MODE_INFO, mEvent.getMode());
            return (B)this;
        }

        public B all() {
            return (B)mode().tag();
        }

        public B clear() {
            mBundle.clear();
            return (B)this;
        }

        public Bundle build() {
            return (Bundle)mBundle.clone();
        }
    }

    /**
     * Extractor class for Additional Info bundles
     * The parameter <code>E</code> represents the class of the extractor.
     * The parameter <code>T</code> represents the class for the event.
     */
    public abstract static class AdditionalInfoExtractor<E extends AdditionalInfoExtractor, T extends AbstractEvent, M extends Enum> {

        @Nullable protected Bundle mBundle;
        @Nullable protected T mEvent;

        public AdditionalInfoExtractor(T event, Bundle bundle) {
            mBundle = bundle;
            mEvent = event;
        }

        @SuppressWarnings("ConstantConditions")
        public E tag() {
            if (proceed()) {
                mEvent.setAddress(mBundle.getString(DESTINATION_INFO));
            }
            return (E)this;
        }

        @SuppressWarnings("ConstantConditions")
        public E mode() {
            if (proceed()) {
                mEvent.setMode((Enum)mBundle.getSerializable(MODE_INFO));
            }
            return (E)this;
        }

        public E all() {
            return (E)mode().tag();
        }

        public boolean proceed() {
            return ((mBundle != null) && (mEvent != null));
        }

        public T done() {
            return mEvent;
        }
    }
}
