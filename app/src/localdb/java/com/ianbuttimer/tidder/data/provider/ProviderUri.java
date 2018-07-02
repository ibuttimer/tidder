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

package com.ianbuttimer.tidder.data.provider;

import android.net.Uri;

public class ProviderUri {


    public static final Uri FOLLOW_CONTENT_URI = TidderProvider.Follow.CONTENT_URI;
    public static final Uri PINNED_CONTENT_URI = TidderProvider.Pinned.CONTENT_URI;
    public static final Uri CONFIG_CONTENT_URI = Uri.EMPTY;


    private ProviderUri() {
        // can't be instantiated
    }
}
