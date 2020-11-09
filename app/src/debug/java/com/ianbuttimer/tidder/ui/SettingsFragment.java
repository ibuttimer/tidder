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
package com.ianbuttimer.tidder.ui;


import android.os.Bundle;
import androidx.annotation.StringRes;

import com.ianbuttimer.tidder.R;

/**
 * Settings fragment to display the general preferences
 *
 * NOTE: this class utilises copies of some on the methods created by the Create Settings Activity wizard
 */

public class SettingsFragment extends AbstractSettingsFragment {
//        PreferenceFragmentCompat {

    @StringRes
    private static final int[] PREFERENCE_KEYS = new int[] {
        R.string.pref_event_post_key,
        R.string.pref_event_delivery_key,
        R.string.pref_event_handled_key,
    };

    @Override
    protected int[] getPreferenceKeys() {
        return PREFERENCE_KEYS;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);

        addPreferencesFromResource(R.xml.pref_debug);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_event_delivery_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_event_handled_key)));

        bindOnOffPreference(findPreference(getString(R.string.pref_event_post_key)));
    }

}
