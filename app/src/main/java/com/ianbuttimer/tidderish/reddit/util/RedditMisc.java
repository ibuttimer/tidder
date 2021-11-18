/*
 * Copyright (c) 2020  Ian Buttimer
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ianbuttimer.tidderish.reddit.util;

import android.content.res.Resources;
import android.net.Uri;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.net.UriUtils;

public class RedditMisc {

    private static String[] mThumbnailMarkers;
    private static String[] mThumbnailUrls;

    private RedditMisc() {
        // can't be instantiated
    }

    /**
     * Convert default thumbnail settings ("default" etc.) to corresponding uri
     * @param res Resources
     * @param uri Uri to convert
     * @return Uri
     */
    public static synchronized Uri convertDefaultThumbnailUri(Resources res, Uri uri) {
        if (mThumbnailMarkers == null) {
            mThumbnailMarkers = res.getStringArray(R.array.thumbnail_markers);
            mThumbnailUrls = res.getStringArray(R.array.thumbnail_urls);
        }
        if (uri != null) {
            String uriStr = uri.toString();
            for (int i = 0; i < mThumbnailMarkers.length; i++) {
                if (uriStr.equals(mThumbnailMarkers[i])) {
                    uri = UriUtils.parse(mThumbnailUrls[i]);
                    break;
                }
            }
        }
        return uri;
    }


}
