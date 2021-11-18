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

package com.ianbuttimer.tidderish.reddit;

import androidx.annotation.Nullable;
import android.util.LruCache;
import android.util.Pair;

public abstract class RedditCache<T extends RedditObject> extends LruCache<String, T> {


    public RedditCache(int size) {
        super(size);
    }


    protected abstract T getObject();

    @Override
    protected T create(String key) {
        T object = getObject();
        object.tagIfNotTagged();
        object.setCacheInstantiated();

        Pair<String, String> split = BaseObject.splitFullname(key);
        if (split != null) {
            String objType = object.getRedditType();
            object.setName(key);
            object.setId(split.second);
            if (!split.first.equals(objType)) {
                throw new IllegalStateException("Cache key type [" + split.first
                        + "] does not match object type [" + objType + "]");
            }
        } else {
            throw new IllegalStateException("Unexpected cache key: " + key);
        }

        return object;
    }

    /**
     * Returns the value for key if it exists in the cache or can be created by #create.
     * @param key       Key
     * @param listener  Callback
     * @return  The value for key or <code>null</code> if a value is not cached and cannot be created.
     */
    @Nullable
    public T get(String key, @Nullable ICacheListener<T> listener) {
        T object = get(key);
        if (object.isCacheInstantiated() && (listener != null)) {
            // created by the cache
            listener.onCreate(key, object);
        }
        return object;
    }


    /**
     * Interface to be implemented by cacheable objects
     */
    public interface ICacheable {
        /**
         * Get the cache key to use for this object
         * @return  Cache key
         */
        String getCacheKey();
    }
    /**
     * Callback interface to be implemented by cache users
     */
    public interface ICacheListener<T extends RedditObject>  {
        /**
         * Called when the cache creates an object
         * @param key       Cache key
         * @param object    Created object
         */
        void onCreate(String key, T object);
    }
}
