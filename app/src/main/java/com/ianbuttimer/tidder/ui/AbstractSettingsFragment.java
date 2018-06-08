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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.ui.util.DatabaseSettings;

import java.util.Arrays;


/**
 * Settings fragment to display the general preferences
 *
 * NOTE: this class utilises copies of some on the methods created by the Create Settings Activity wizard
 */

public abstract class AbstractSettingsFragment extends PreferenceFragmentCompat {

    // preference keys common to all variants
    @StringRes private static final int[] PREFERENCE_KEYS = new int[] {
            R.string.pref_post_source_key,
            R.string.pref_sfw_key,
            R.string.pref_refresh_on_discard_key,
            R.string.pref_autoexpand_key,
            R.string.pref_autoexpand_level_key
    };

    @StringRes private int[] mPreferenceKeys = null;

    protected DatabaseSettings mDbSettings = DatabaseSettings.getInstance();


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_post_source_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_autoexpand_level_key)));

        bindOnOffPreference(findPreference(getString(R.string.pref_sfw_key)));
        bindOnOffPreference(findPreference(getString(R.string.pref_refresh_on_discard_key)));
        bindOnOffPreference(findPreference(getString(R.string.pref_autoexpand_key)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                setSummary(preference, (index >= 0
                                ? listPreference.getEntries()[index]
                                : null));
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                setSummary(preference, stringValue);
            }

            onSettingChange(preference, value);

            return true;
        }
    };


    void onSettingChange(Preference preference, Object value) {
        if (mDbSettings != null) {
            mDbSettings.onPreferenceChange(preference, value, getPreferenceKeyId(preference));
        }
    }

    /**
     * A preference value change listener that handles on/off type preference changes
     */
    private Preference.OnPreferenceChangeListener sOnOffPreferenceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Context context = preference.getContext();
            boolean boolValue = Boolean.valueOf(value.toString());
            int keyId = getPreferenceKeyId(preference);

            switch (keyId) {
                case R.string.pref_autoexpand_key:
                    // enable/disable autoexpand-related preferences
                    for (int prefKey : new int[] {
                            R.string.pref_autoexpand_level_key
                    }) {
                        Preference cachePref = findPreference(context.getString(prefKey));
                        cachePref.setEnabled(boolValue);
                    }
                    break;
                default:
                    break;
            }

            onSettingChange(preference, value);

            return true;
        }
    };

    /**
     * Set the summary for a preference
     * @param preference    Preference to set summary for
     * @param value         Value string
     */
    protected void setSummary(Preference preference, CharSequence value) {
        Context context = preference.getContext();
        CharSequence summary;
        switch (getPreferenceKeyId(preference)) {
            default:
                summary = value;
                break;
        }
        preference.setSummary(summary);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    protected void bindPreferenceSummaryToValue(Preference preference) {
        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
    }

    /**
     * Binds a on/off preference to an appropriate listener.
     *
     * @see #sOnOffPreferenceListener
     */
    protected void bindOnOffPreference(Preference preference) {
        // Trigger the listener immediately with the preference's current value.
        sOnOffPreferenceListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sOnOffPreferenceListener);
    }

    /**
     * Get the resource id of the specified preference
     * @param preference    The changed preference
     * @return  Resource id of preference key, or 0 if not found
     */
    @StringRes private int getPreferenceKeyId(Preference preference) {
        @StringRes int id = 0;
        Context context = preference.getContext();
        String key = preference.getKey();

        if (mPreferenceKeys == null) {
            int[] keys = getPreferenceKeys();
            mPreferenceKeys = Arrays.copyOf(
                    PREFERENCE_KEYS, PREFERENCE_KEYS.length + keys.length);
            System.arraycopy(keys, 0, mPreferenceKeys, PREFERENCE_KEYS.length, keys.length);
        }

        for (int i = 0; i < mPreferenceKeys.length; i++) {
            if (key.equals(context.getString(mPreferenceKeys[i]))) {
                id = mPreferenceKeys[i];
                break;
            }
        }
        return id;
    }

    /**
     * Get additional variant specific preference keys
     * @return
     */
    @StringRes protected abstract int[] getPreferenceKeys();


}
