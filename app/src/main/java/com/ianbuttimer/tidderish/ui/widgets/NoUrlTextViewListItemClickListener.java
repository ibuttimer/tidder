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

import android.content.Context;
import androidx.annotation.IdRes;
import android.view.View;
import android.widget.TextView;

import com.ianbuttimer.tidderish.data.IAdapterHandler;

import java.util.ArrayList;

public class NoUrlTextViewListItemClickListener extends ListItemClickListener {

    public NoUrlTextViewListItemClickListener(Context context, @IdRes ArrayList<Integer> viewIds,
                                              IAdapterHandler handler, TypedGestureDetector.Gestures... gestures) {
        super();
        init(context, viewIds, handler, gestures);
    }

    public NoUrlTextViewListItemClickListener(Context context, @IdRes int[] viewIds,
                                              IAdapterHandler handler, TypedGestureDetector.Gestures... gestures) {
        super();
        init(context, asList(viewIds), handler, gestures);
    }

//    private ListItemClickListener.IListItemClickTester mTester =
//        new ListItemClickListener.IListItemClickTester() {
//            @Override
//            public void onItemClick(View view, int position) {
//                if (mHandler != null) {
//                    mHandler.onItemClick(view, position);
//                }
//            }
//
//            @Override
//            public void onItemDoubleClick(View view, int position) {
//                // no op
//            }
//
//            @Override
//            public boolean onInterceptTouchEvent(View parent, View child, int position) {
//                // intercept if there are no urls in the text
//                return (((TextView)child).getUrls().length == 0);
//            }
//        };

    protected void init(Context context, @IdRes ArrayList<Integer> viewIds, IAdapterHandler handler,
                        TypedGestureDetector.Gestures... gestures) {

        ListItemClickListener.IListItemClickTester tester =
                new ListItemClickListener.SimpleListItemClickTester(handler) {
                    @Override
                    public boolean onInterceptTouchEvent(View parent, View child, int position) {
                        // intercept if there are no urls in the text
                        return (((TextView)child).getUrls().length == 0);
                    }
                };

        super.init(context, tester, viewIds, gestures);
    }
}
