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

package com.ianbuttimer.tidderish.ui.widgets;

import android.widget.AbsListView;

/**
 * Endless scrolling for ListView or GridView
 *
 * @see <a href="https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView">Endless Scrolling with AdapterViews and RecyclerView</a>
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    // The minimum number of items to have below your current scroll position
    // before mLoading more.
    private int mVisibleThreshold = 5;
    // The current offset index of data you have loaded
    private int mCurrentPage = 0;
    // The total number of items in the dataset after the last load
    private int mPreviousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean mLoading = true;
    // Sets the starting page index
    private int mStartingPageIndex = 0;

    /**
     * Default constructor
     */
    public EndlessScrollListener() {
        // no op
    }

    /**
     * Constructor
     * @param visibleThreshold  Minimum number of items below current scroll position
     */
    public EndlessScrollListener(int visibleThreshold) {
        this(visibleThreshold, 0);
    }

    /**
     * Constructor
     * @param visibleThreshold  Minimum number of items below current scroll position
     * @param startPage         Starting page index
     */
    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.mVisibleThreshold = visibleThreshold;
        this.mStartingPageIndex = startPage;
        this.mCurrentPage = startPage;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < mPreviousTotalItemCount) {
            this.mCurrentPage = this.mStartingPageIndex;
            this.mPreviousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.mLoading = true; }
        }
        // If it's still mLoading, we check to see if the dataset count has
        // changed, if so we conclude it has finished mLoading and update the current page
        // number and total item count.
        if (mLoading && (totalItemCount > mPreviousTotalItemCount)) {
            mLoading = false;
            mPreviousTotalItemCount = totalItemCount;
            mCurrentPage++;
        }

        // If it isn't currently mLoading, we check to see if we have breached
        // the mVisibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!mLoading
                && ((firstVisibleItem + visibleItemCount + mVisibleThreshold) >= totalItemCount)) {
            mLoading = onLoadMore(mCurrentPage + 1, totalItemCount);
        }
    }

    // Defines the process for actually mLoading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}
