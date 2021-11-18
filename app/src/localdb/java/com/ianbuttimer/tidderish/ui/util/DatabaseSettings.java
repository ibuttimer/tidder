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

package com.ianbuttimer.tidderish.ui.util;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.preference.Preference;

import com.ianbuttimer.tidderish.data.Config;

/**
 * No-op class for functionality not supported
 */
public class DatabaseSettings implements IDatabaseSettings {

    @Nullable
    public static DatabaseSettings getInstance() {
        return null;
    }

    private DatabaseSettings() {
    }

    @Override
    public void onPreferenceChange(Preference preference, Object value, @StringRes int keyId) {
        // no op
    }

    @Override
    public void applyConfig(Config config) {
        // no op
    }

}
