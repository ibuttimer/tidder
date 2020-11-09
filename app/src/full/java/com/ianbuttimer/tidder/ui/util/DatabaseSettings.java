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

package com.ianbuttimer.tidder.ui.util;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.preference.Preference;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.data.Config;
import com.ianbuttimer.tidder.data.db.ConfigFb;
import com.ianbuttimer.tidder.data.provider.FirebaseProvider;
import com.ianbuttimer.tidder.utils.PreferenceControl;

/**
 * Process configuration from Firebase database
 */
public class DatabaseSettings implements IDatabaseSettings {

    private ConfigFb mConfig;

    private static DatabaseSettings sInstance = new DatabaseSettings();

    public static DatabaseSettings getInstance() {
        return sInstance;
    }

    private DatabaseSettings() {
        this.mConfig = new ConfigFb();
    }

    @Override
    public void onPreferenceChange(Preference preference, Object value, @StringRes int keyId) {

        Context context = preference.getContext();
        String strValue = value.toString();
        boolean boolValue = Boolean.valueOf(strValue);
        int intValue;
        boolean persist = false;

        switch (keyId) {
            case R.string.pref_autoexpand_key:
                persist = (mConfig.getThreadExpand() != boolValue);
                if (persist) {
                    mConfig.setThreadExpand(boolValue);
                }
                break;
            case R.string.pref_autoexpand_level_key:
                intValue = Integer.valueOf(strValue);
                persist = (mConfig.getThreadExpandLevel() != intValue);
                if (persist) {
                    mConfig.setThreadExpandLevel(intValue);
                }
                break;
            case R.string.pref_sfw_key:
                persist = (mConfig.getSfw() != boolValue);
                if (persist) {
                    mConfig.setSfw(boolValue);
                }
                break;
            case R.string.pref_post_source_key:
                persist = !mConfig.getPostSrc().equals(strValue);
                if (persist) {
                    mConfig.setPostSrc(strValue);
                }
                break;
            case R.string.pref_refresh_on_discard_key:
                persist = (mConfig.getRefreshOnDiscard() != boolValue);
                if (persist) {
                    mConfig.setRefreshOnDiscard(boolValue);
                }
                break;

            default:
                break;
        }

        // update database
        if (persist) {
            persist(context);
        }
    }

    private void persist(Context context) {
        // update database
        context.getContentResolver().insert(
                FirebaseProvider.Config.CONTENT_URI,
                mConfig.getContentValues()
        );
    }


    public void applyConfig(Config config) {
        Context context = TidderApplication.getWeakApplicationContext().get();

        boolean boolValue = config.getSfw();
        if (PreferenceControl.getSafeForWorkPreference(context) != boolValue) {
            PreferenceControl.setSharedBooleanPreference(context, R.string.pref_sfw_key, boolValue);
            mConfig.setSfw(boolValue);
        }
        boolValue = config.getRefreshOnDiscard();
        if (PreferenceControl.getRefreshOnDiscardPreference(context) != boolValue) {
            PreferenceControl.setSharedBooleanPreference(context, R.string.pref_refresh_on_discard_key, boolValue);
            mConfig.setRefreshOnDiscard(boolValue);
        }
        boolValue = config.getAutoExpand();
        if (PreferenceControl.getAutoExpandPreference(context) != boolValue) {
            PreferenceControl.setSharedBooleanPreference(context, R.string.pref_autoexpand_key, boolValue);
            mConfig.setThreadExpand(boolValue);
        }

        int level = config.getThreadExpand();
        int pref = PreferenceControl.getAutoExpandLevelPreference(context);
        if ((pref != level) || (mConfig.getThreadExpandLevel() != level)) {
            PreferenceControl.setSharedStringPreference(context, R.string.pref_autoexpand_level_key, Integer.toString(level));
            mConfig.setThreadExpandLevel(level);
        }

        String strValue = config.getPostSrc();
        if (!strValue.equals(PreferenceControl.getPostSourcePreference(context))) {
            PreferenceControl.setSharedStringPreference(context, R.string.pref_post_source_key, strValue);
            mConfig.setPostSrc(strValue);
        }
    }

}
