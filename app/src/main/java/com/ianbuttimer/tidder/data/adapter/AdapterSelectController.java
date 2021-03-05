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

package com.ianbuttimer.tidder.data.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.view.KeyEvent;
import android.view.View;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.reddit.BaseObject;

import timber.log.Timber;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_MULTIPLE;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.KeyEvent.KEYCODE_UNKNOWN;

/**
 * RecycleViewAdapter selected item controller
 * @param <T> class of reddit object
 * @param <B> class of view binding
 * @param <K> class of view holder
 */
public class AdapterSelectController<T extends BaseObject<T>, B extends ViewBinding, K extends AbstractViewHolder<T, B>> implements IAdapterHandler {

    private AbstractRecycleViewAdapter<T, B, K> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public AdapterSelectController(RecyclerView recyclerView) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter instanceof AbstractRecycleViewAdapter) {
            setAdapter((AbstractRecycleViewAdapter<T, B, K>)adapter);
        }
        mLayoutManager = recyclerView.getLayoutManager();
    }


    @Override
    public void onItemClick(View view) {
        mAdapter.setSelectedPos(mLayoutManager, view);
    }

    @Override
    public boolean onItemLongClick(View view) {
        mAdapter.setSelectedPos(mLayoutManager, view);
        return false;
    }

    @Override
    public void onItemDoubleClick(View view) {
        mAdapter.setSelectedPos(mLayoutManager, view);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

        logKeyEvent(keyEvent, "onKey");

        /* only get action up when entering focus & action down when leaving
         * also item you're leaving get's down action & moving to gets up action */

        boolean upKey = (keyCode == KeyEvent.KEYCODE_DPAD_UP);
        boolean downKey = (keyCode == KeyEvent.KEYCODE_DPAD_DOWN);
        if (upKey || downKey) {
            int selectedPos = mAdapter.getSelectedPos();
            int position = mLayoutManager.getPosition(view);
            boolean noPos = (selectedPos == RecyclerView.NO_POSITION);
            boolean setPos = false;

            if (upKey && (selectedPos == 0)) {
                // leaving top of recyclerview
                mAdapter.setSelectedPos(RecyclerView.NO_POSITION);
            } else if (downKey && noPos) {
                // entering top of recyclerview
                setPos = true;
            } else if (downKey && (selectedPos == (mAdapter.getItemCount() - 1))) {
                // leaving bottom of recyclerview
                mAdapter.setSelectedPos(RecyclerView.NO_POSITION);
            } else if (upKey && noPos) {
                // entering bottom of recyclerview
                setPos = true;
            } else if (selectedPos != position) {
                // set selected to view's position (on key down)
                setPos = (keyEvent.getAction() == KeyEvent.ACTION_UP);
            }

            if (setPos) {
                // set selected to view's position
                mAdapter.setSelectedPos(mLayoutManager, view);
            }
        }

        return false;
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        mAdapter.setSelectedPos(RecyclerView.NO_POSITION);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        mAdapter.setSelectedPos(toPosition);
        return false;
    }


    public Object getSelectedObject(View view) {
        return view.getTag(R.id.base_obj_tag);
    }


    public void setAdapter(AbstractRecycleViewAdapter<T, B, K> adapter) {
        this.mAdapter = adapter;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public static void logKeyEvent(KeyEvent event, String tag) {
        String action;
        switch (event.getAction()) {
            case ACTION_DOWN:
                action = "DOWN";
                break;
            case ACTION_UP:
                action = "UP";
                break;
            case ACTION_MULTIPLE:
                action = "MULTIPLE";
                break;
            default:
                action = Integer.toString(event.getAction());
                break;
        }
        String keyCode;
        switch (event.getKeyCode()) {
            case KEYCODE_DPAD_CENTER:
                keyCode = "DPAD_CENTER";
                break;
            case KEYCODE_DPAD_UP:
                keyCode = "DPAD_UP";
                break;
            case KEYCODE_DPAD_DOWN:
                keyCode = "DPAD_DOWN";
                break;
            case KEYCODE_ENTER:
                keyCode = "ENTER";
                break;
            case KEYCODE_UNKNOWN:
                keyCode = "UNKNOWN";
                break;
            default:
                keyCode = Integer.toString(event.getKeyCode());
                break;
        }
        Timber.i("%s: key %s  action %s  repeat %d  time %d",
                tag, keyCode, action, event.getRepeatCount(), event.getEventTime());
    }

}
