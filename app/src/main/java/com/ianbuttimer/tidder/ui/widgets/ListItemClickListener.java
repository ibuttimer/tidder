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

package com.ianbuttimer.tidder.ui.widgets;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.ianbuttimer.tidder.data.IAdapterHandler;

import java.util.ArrayList;

import github.nisrulz.recyclerviewhelper.RVHItemClickListener;

/**
 * RecyclerView.OnItemTouchListener based on github.nisrulz.recyclerviewhelper.RVHItemClickListener
 */

public class ListItemClickListener implements RecyclerView.OnItemTouchListener {

//    public enum Gestures { SINGLE_TAP, DOUBLE_TAP, LONG_PRESS;
//
//        public static final int SINGLE_TAP_BIT = SINGLE_TAP.toGestureBit();
//        public static final int DOUBLE_TAP_BIT = DOUBLE_TAP.toGestureBit();
//        public static final int LONG_PRESS_BIT = LONG_PRESS.toGestureBit();
//
//        public static int toGestureBit(Gestures gesture) {
//            int bit = 0;
//            if (gesture != null) {
//                bit = 1 << gesture.ordinal();
//            }
//            return bit;
//        }
//
//        public int toGestureBit() {
//            return toGestureBit(this);
//        }
//
//        public static int toGestureMap(Gestures... gestures) {
//            int map = 0;
//            for (Gestures gesture : gestures) {
//                map |= toGestureBit(gesture);
//            }
//            return map;
//        }
//    }

    /**
     * The M gesture detector.
     */
    private TypedGestureDetector mGestureDetector;

    @Nullable private IListItemClickTester mListener;

    @IdRes private ArrayList<Integer> mViewIds;

    private int mGestureMap;

    protected ListItemClickListener() {
        mGestureMap = TypedGestureDetector.Gestures.SINGLE_TAP_BIT;
    }

    public ListItemClickListener(Context context, @Nullable IListItemClickTester listener, TypedGestureDetector.Gestures... gestures) {
        this(context, listener, (ArrayList<Integer>) null, gestures);
    }

    public ListItemClickListener(Context context, @Nullable IListItemClickTester listener,
                                 @IdRes ArrayList<Integer> viewIds, TypedGestureDetector.Gestures... gestures) {
        this();
        init(context, listener, viewIds, gestures);
    }

    public ListItemClickListener(Context context, @Nullable IListItemClickTester listener,
                                 @IdRes int[] viewIds, TypedGestureDetector.Gestures... gestures) {
        this();
        init(context, listener, asList(viewIds), gestures);
    }

    protected void init(Context context, @Nullable IListItemClickTester listener,
                      @IdRes ArrayList<Integer> viewIds, TypedGestureDetector.Gestures... gestures) {
        mListener = listener;
        mViewIds = viewIds;

        int map = TypedGestureDetector.Gestures.toGestureMap(gestures);
        if (map != 0) {
            mGestureMap = map;
        }

        mGestureDetector = new TypedGestureDetector(context,
                new TypedGestureDetector.SimpleTypedOnGestureListener(gestures));
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        boolean intercept = false;
        Pair<Boolean, TypedGestureDetector.Gestures> touched = mGestureDetector.onTypedTouchEvent(e);
        if (touched.first && (mListener != null)) {
//        if (mGestureDetector.onTouchEvent(e) && (mListener != null)) {
            Float x = e.getX();
            Float y = e.getY();
            View parentView = recyclerView.findChildViewUnder(x, y);
            if (parentView != null) {
                int position = recyclerView.getChildAdapterPosition(parentView);

                if (mViewIds != null) {
                    // check if event happened on a view with hyperlinks
                    for (int i = 0; (i < mViewIds.size()) && !intercept; i++) {
                        View childView = parentView.findViewById(mViewIds.get(i));
                        if (childView != null) {
                            int[] cvLoc = getLocationInWindow(childView);
                            int[] touchLoc = getLocationInWindow(recyclerView);
                            touchLoc[0] += x.intValue();
                            touchLoc[1] += y.intValue();
                            Rect cvRect = new Rect(cvLoc[0], cvLoc[1],
                                    cvLoc[0] + childView.getWidth(),
                                    cvLoc[1] + childView.getHeight());
                            if (cvRect.contains(touchLoc[0], touchLoc[1])) {
                                // touch position within child view bounds
                                intercept = mListener.onInterceptTouchEvent(parentView, childView, position);
                            }
                        }
                    }
                }
                if (intercept) {
                    if (TypedGestureDetector.Gestures.SINGLE_TAP.equals(touched.second)) {
                        mListener.onItemClick(parentView, position);
                    } else if (TypedGestureDetector.Gestures.DOUBLE_TAP.equals(touched.second)) {
                        mListener.onItemDoubleClick(parentView, position);
                    }
                }
            }
        }
        return intercept;
    }


    private int[] getLocationInWindow(View view) {
        int[] loc = new int[2];
        view.getLocationInWindow(loc);
        return loc;
    }


    public boolean addViewId(@IdRes int id) {
        boolean modified = false;
        if (!mViewIds.contains(id)) {
            modified = mViewIds.add(id);
        }
        return modified;
    }

    public boolean removeViewId(@IdRes int id) {
        return mViewIds.remove((Integer)id);
    }

    public void setListener(@Nullable IListItemClickTester listener) {
        this.mListener = listener;
    }

    public void setViewIds(ArrayList<Integer> viewIds) {
        this.mViewIds = viewIds;
    }

    public void setViewIds(@IdRes int[] viewIds) {
        setViewIds(asList(viewIds));
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // Do nothings
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        // Do nothing
        int i =0;
        ++i;
    }

    protected ArrayList<Integer> asList(int[] primatives) {
        ArrayList<Integer> list = null;
        if (primatives != null && primatives.length > 0) {
            list = new ArrayList<>();
            for (int id : primatives) {
                list.add(id);
            }
        }
        return list;
    }



    public interface IListItemClickTester extends RVHItemClickListener.OnItemClickListener {

        /**
         * On item double click.
         * @param view     the view
         * @param position the position
         */
        void onItemDoubleClick(View view, int position);


        /**
         * Check if the listener should take over touch events sent to the RecyclerView before they are handled by either the RecyclerView itself or its child views.
         * @param parent    Parent view in the list
         * @param child     View on which the motion event occured
         * @param position  Position of parent view in list
         * @return <code>true</code> if the listener should intercept the event, <code>false</code> to continue with the current behavior and continue observing future events in the gesture.
         */
        boolean onInterceptTouchEvent(View parent, View child, int position);

    }

    /**
     * Class providing basic implementation of IListItemClickTester interface
     */
    public static class SimpleListItemClickTester implements IListItemClickTester {

        private IAdapterHandler mHandler;
        private boolean mIntercept;

        public SimpleListItemClickTester(IAdapterHandler handler) {
            this(handler, false);   // default intercept nothing
        }

        public SimpleListItemClickTester(IAdapterHandler handler, boolean intercept) {
            this.mHandler = handler;
            this.mIntercept = intercept;
        }

        @Override
        public void onItemClick(View view, int position) {
            if (mHandler != null) {
                mHandler.onItemClick(view);
            }
        }

        @Override
        public void onItemDoubleClick(View view, int position) {
            if (mHandler != null) {
                mHandler.onItemDoubleClick(view);
            }
        }

        @Override
        public boolean onInterceptTouchEvent(View parent, View child, int position) {
            return mIntercept;
        }
    }

}
