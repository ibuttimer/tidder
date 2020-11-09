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

package com.ianbuttimer.tidder.reddit;


import android.util.JsonReader;
import android.util.JsonToken;

import androidx.annotation.NonNull;

import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;
import com.ianbuttimer.tidder.utils.Utils;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class representing a 'more' comment
 */
@Parcel
public class CommentMore extends Comment implements BasicStatsView.IBasicStats {

    public static final String RESPONSE_CHILDREN = "children";
    public static final String COUNT = "count";

    protected String[] mChildren;
    protected int mCount;

    public CommentMore() {
        init();
    }

    @Override
    protected void init() {
        super.init();
        mChildren = new String[0];
        mCount = 0;
    }

    @Override
    protected CommentMore getInstance() {
        return new CommentMore();
    }

    @Override
    public String getRedditType() {
        return TYPE_MORE;
    }


    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, Comment obj)
                                        throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        CommentMore object = ((CommentMore) obj);
        boolean consumed = super.parseToken(jsonReader, name, object);
        if (!consumed) {
            consumed = true;
            if (COUNT.equals(name)) {
                object.mCount = nextInt(jsonReader, 0);
            } else if (RESPONSE_CHILDREN.equals(name)) {
                if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                    // have children to process
                    ArrayList<String> list = new ArrayList<>();
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        list.add(nextString(jsonReader, ""));
                    }
                    jsonReader.endArray();
                    object.mChildren = list.toArray(new String[0]);
                } else {
                    // should be an empty string so skip
                    object.mChildren = new String[0];
                    jsonReader.skipValue();
                }
            } else {
                consumed = false;
            }
        }
        return consumed;
    }

    public String[] getChildren() {
        return mChildren;
    }

    public boolean hasChildren() {
        return Utils.arrayHasSize(mChildren);
    }

    public void setChildren(String[] children) {
        this.mChildren = children;
    }

    public boolean removeChild(String child) {
        boolean removed = false;
        if (hasChildren()) {
            int length = mChildren.length;
            for (int i = 0; (i < length) && !removed; i++) {
                if ((mChildren[i] != null) && (mChildren[i].equals(child))) {
                    mChildren = Utils.splice(mChildren, i, 1, String[].class);
                    removed = true;
                }
            }
        }
        return removed;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CommentMore that = (CommentMore) o;

        if (mCount != that.mCount) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(mChildren, that.mChildren);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(mChildren);
        result = 31 * result + mCount;
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "CommentMore{" +
                "mChildren=" + Arrays.toString(mChildren) +
                ", mCount=" + mCount +
                ", mName='" + mName + '\'' +
                ", mId='" + mId + '\'' +
                '}';
    }
}
