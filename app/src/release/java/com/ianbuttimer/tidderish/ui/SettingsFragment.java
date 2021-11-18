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
package com.ianbuttimer.tidderish.ui;


/**
 * Settings fragment to display the general preferences
 *
 * NOTE: this class utilises copies of some on the methods created by the Create Settings Activity wizard
 */

public class SettingsFragment extends AbstractSettingsFragment {

    @Override
    protected int[] getPreferenceKeys() {
        return new int[0];
    }

}
