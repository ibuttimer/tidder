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

import android.view.KeyEvent;
import android.view.View;

import github.nisrulz.recyclerviewhelper.RVHAdapter;

/**
 * Interface to handle events for adapters
 */

public interface IAdapterHandler extends RVHAdapter {

    /**
     * Process a click event
     * @param view      View that was clicked
     *
     */
    void onItemClick(View view);

    /**
     * Process a long click event
     * @param view      View that was clicked
     * @return  <code>true</code> if the callback consumed the long click, <code>false</code> otherwise.
     */
    boolean onItemLongClick(View view);

    /**
     * Process a double click.
     * @param view     the view
     *
     */
    void onItemDoubleClick(View view);

    /**
     * Process a key event
     * @param view      The view the key has been dispatched to.
     * @param keyCode   The code for the physical key that was pressed
     * @param keyEvent  The KeyEvent object containing full information about the event.
     * @return  <code>true</code> if the listener has consumed the event, <code>false</code> otherwise.
     */
    boolean onKey(View view, int keyCode, KeyEvent keyEvent);
}
