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

package com.ianbuttimer.tidder.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;

import java.util.Set;

import timber.log.Timber;

/**
 * Base class for IntentService implementations
 */

public abstract class AbstractIntentService extends IntentService {

    public static final String RESULT_RECEIVER = "resultReceiver";

    /**
     * Constructor
     */
    public AbstractIntentService(String name) {
        super(name);
    }

    /** Builder class for service arguments */
    public abstract static class Builder {

        protected Intent mIntent;

        public Builder(Context context, Class<?> serviceClass, @Nullable String action) {
            mIntent = new Intent(context, serviceClass);
            action(action);
        }

        public Builder action(@Nullable String action) {
            mIntent.setAction(action);
            return this;
        }

        public Builder resultReceiver(ResultReceiver resultReceiver) {
            mIntent.putExtra(RESULT_RECEIVER, resultReceiver);
            return this;
        }

        public Intent build() {
            return (Intent)mIntent.clone();
        }
    }

    /** Service argument extractor class */
    public abstract static class Extractor {

        @Nullable protected Intent mIntent;

        public Extractor(@Nullable Intent intent) {
            mIntent = intent;
        }

        @Nullable public String action() {
            String action = null;
            if (mIntent != null) {
                action = mIntent.getAction();
            }
            return action;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable public ResultReceiver resultReceiver() {
            ResultReceiver resultReceiver = null;
            if (hasExtra(RESULT_RECEIVER)) {
                resultReceiver = mIntent.getParcelableExtra(RESULT_RECEIVER);
            }
            return resultReceiver;
        }

        @SuppressWarnings("ConstantConditions")
        protected int getInt(String name, int dfltValue) {
            int value = dfltValue;
            if (hasExtra(name)) {
                value = mIntent.getIntExtra(name, dfltValue);
            }
            return value;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable protected String getString(String name) {
            String string = null;
            if (hasExtra(name)) {
                string = mIntent.getStringExtra(name);
            }
            return string;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable
        protected String[] getStringArray(String name) {
            String[] array = null;
            if (hasExtra(name)) {
                array = mIntent.getStringArrayExtra(name);
            }
            return array;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable
        public ContentValues getContentValues(String name) {
            ContentValues cv = null;
            if (hasExtra(name)) {
                cv = mIntent.getParcelableExtra(name);
            }
            return cv;
        }

        protected boolean hasExtra(String key) {
            return ((mIntent != null) && mIntent.hasExtra(key));
        }
    }

    public abstract static class BundleBuilder {

        protected Bundle mBundle;

        public BundleBuilder() {
            mBundle = new Bundle();
        }

        public BundleBuilder dump() {
            if (mBundle != null) {
                Set<String> keys = mBundle.keySet();
                if (keys.isEmpty()) {
                    Timber.d("  Empty bundle");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (String key : keys) {
                        Object obj = mBundle.get(key);
                        sb.append(String.format("  %s : %s%n",
                                                    key, (obj != null ? obj.toString() : "null")));
                    }
                    Timber.d(sb.toString());
                }
            } else {
                Timber.d("  No bundle");
            }
            return this;
        }

        public BundleBuilder dump(boolean dump) {
            if (dump) {
                dump();
            }
            return this;
        }

        public Bundle build() {
            return (Bundle) mBundle.clone();
        }
    }

    public abstract static class BundleExtractor {

        @Nullable protected Bundle mBundle;

        public BundleExtractor(@Nullable Bundle bundle) {
            mBundle = bundle;
        }

        @SuppressWarnings("ConstantConditions")
        protected int getInt(String key, int dfltValue) {
            int value = dfltValue;
            if (containsKey(key)) {
                value = mBundle.getInt(key);
            }
            return value;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable
        protected String getString(String key) {
            String value = null;
            if (containsKey(key)) {
                value = mBundle.getString(key);
            }
            return value;
        }


        protected boolean containsKey(String key) {
            return ((mBundle != null) && mBundle.containsKey(key));
        }

    }


}
