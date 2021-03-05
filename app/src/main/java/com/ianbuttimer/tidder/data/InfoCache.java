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

import androidx.annotation.Nullable;

import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.Subreddit;
import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;
import org.parceler.ParcelConverter;
import org.parceler.ParcelPropertyConverter;
import org.parceler.Parcels;

import java.util.ArrayDeque;
import java.util.Collection;

@Parcel(Parcel.Serialization.BEAN)
public class InfoCache {
    @ParcelPropertyConverter(InfoCache.InfoCacheConverter.class)

    ArrayDeque<Link> mQueue;    // posts queue
    Subreddit mSubreddit;       // subreddit info

    public InfoCache(Subreddit mSubreddit, ArrayDeque<Link> mQueue) {
        this.mSubreddit = mSubreddit;
        this.mQueue = mQueue;
    }

    public InfoCache() {
        this(null);
    }

    public InfoCache(Subreddit subreddit) {
        this(subreddit, new ArrayDeque<Link>());
    }

    /**
     * Get the size of the posts queue
     * @return  Que size
     */
    public int size() {
        int size = 0;
        if (mQueue != null) {
            size = mQueue.size();
        }
        return size;
    }

    public Link pop() {
        Link item = null;
        if (mQueue != null) {
            item = mQueue.pop();
        }
        return item;
    }

    public boolean add(Link link) {
        return mQueue.add(link);
    }

    public boolean addAll(Collection<Link> c) {
        boolean modified = false;
        if (mQueue != null) {
            modified = mQueue.addAll(c);
        }
        return modified;
    }

    public ArrayDeque<Link> getQueue() {
        return mQueue;
    }

    @Nullable
    public Subreddit getSubreddit() {
        return mSubreddit;
    }

    public void setSubreddit(Subreddit subreddit) {
        this.mSubreddit = subreddit;
    }


    static class InfoCacheConverter implements ParcelConverter<InfoCache> {
        @Override
        public void toParcel(InfoCache input, android.os.Parcel parcel) {
            Utils.writeBooleanToParcel(parcel, (input != null));
            if (input != null) {
                parcel.writeParcelable(Parcels.wrap(input.mSubreddit), 0);
                int size;
                if (input.mQueue != null) {
                    size = input.size();
                } else {
                    size = 0;
                }
                parcel.writeInt(size);
                if (size > 0) {
                    for (Link item : input.mQueue) {
                        parcel.writeParcelable(Parcels.wrap(item), 0);
                    }
                }
            }
        }

        @Override
        public InfoCache fromParcel(android.os.Parcel parcel) {
            InfoCache infoCache = null;
            if (Utils.readBooleanFromParcel(parcel)) {
                infoCache = new InfoCache(
                        Parcels.unwrap(parcel.readParcelable(Subreddit.class.getClassLoader())));
                int size = parcel.readInt();
                if (size > 0) {
                    for (int i = 0; i < size; ++i) {
                        infoCache.add(
                                Parcels.unwrap(parcel.readParcelable(Link.class.getClassLoader())));
                    }
                }
            }
            return infoCache;
        }
    }

}
