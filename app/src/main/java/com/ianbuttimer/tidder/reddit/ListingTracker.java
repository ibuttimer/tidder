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

import org.parceler.Parcel;

/**
 * Class for tracking position in a Reddit listing
 */
@Parcel
public class ListingTracker<T extends BaseObject<T>> {

    public enum UpdateDir { FORWARD, BACKWARD }

    protected String mBefore; // before (i.e. prev) from last listing response
    protected String mAfter;  // after (i.e. next) from last listing response
    protected int mCount;     // count from last listing response

    public ListingTracker() {
        mBefore = null;
        mAfter = null;
        mCount = 0;
    }

    private void update(ListingList<T> list, int count) {
        mAfter = list.getAfter();
        mBefore = list.getBefore();
        mCount += count;
    }

    public void updateForward(ListingList<T> list) {
        update(list, UpdateDir.FORWARD);
    }

    public void updateBackward(ListingList<T> list) {
        update(list, UpdateDir.BACKWARD);
    }

    public void update(ListingList<T> list, UpdateDir direction) {
        int count = list.getCount();    // default forward
        switch (direction) {
            case BACKWARD:
                count = -count;
                break;
            case FORWARD:
                break;
            default:
                count = 0;
                break;
        }
        update(list, count);
    }

    public String getBefore() {
        return mBefore;
    }

    public void setBefore(String before) {
        this.mBefore = before;
    }

    public String getAfter() {
        return mAfter;
    }

    public void setAfter(String after) {
        this.mAfter = after;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }
}
