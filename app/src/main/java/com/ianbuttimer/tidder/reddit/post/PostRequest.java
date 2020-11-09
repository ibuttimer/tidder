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

package com.ianbuttimer.tidder.reddit.post;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.ArrayMap;

import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.Request;

/**
 * Base class for requests
 */

public abstract class PostRequest extends Request {

    protected ArrayMap<String, String> mDataMap;

    public PostRequest(Uri uri) {
        super(uri);
    }

    public PostRequest(Uri uri, Class<? extends Response<? extends BaseObject<?>>> responseClass) {
        super(uri, responseClass);
    }

    public ArrayMap<String, String> getDataMap() {
        return mDataMap;
    }

    public void setDataMap(ArrayMap<String, String> dataMap) {
        this.mDataMap = dataMap;
    }

    @Override
    public PostRequest setAdditionalInfo(@Nullable Bundle additionalInfo) {
        super.setAdditionalInfo(additionalInfo);
        return this;
    }

    public abstract static class Builder extends Request.Builder {

        protected ArrayMap<String, String> mDataMap;

        /**
         * Constructor
         */
        protected Builder(String urlString) {
            this(Uri.parse(urlString));
        }

        public Builder(Uri uri) {
            super(uri);
            mDataMap = new ArrayMap<>();
        }

        protected Builder post(String key, String value) {
            mDataMap.put(key, value);
            return this;
        }

        protected Builder post(String key, int value) {
            mDataMap.put(key, Integer.toString(value));
            return this;
        }

        protected Builder post(String key, boolean value) {
            mDataMap.put(key, Boolean.toString(value));
            return this;
        }

        public ArrayMap<String, String> buildDataMap() {
            return mDataMap;
        }

    }

}
