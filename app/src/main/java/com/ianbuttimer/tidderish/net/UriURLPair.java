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

package com.ianbuttimer.tidderish.net;

import android.net.Uri;
import androidx.annotation.NonNull;

import java.net.URL;

/**
 * Utility class to store either a Uri or URL
 */

public class UriURLPair {

    private Uri uri;
    private URL url;

    /**
     * Constructor
     * @param uri   Uri to store
     */
    public UriURLPair(@NonNull Uri uri) {
        this.uri = uri;
        this.url = null;
    }

    /**
     * Constructor
     * @param url   Url to store
     */
    public UriURLPair(@NonNull URL url) {
        this.uri = null;
        this.url = url;
    }

    /**
     * Get stored Uri or Uri representation of stored Url
     * @return  Uri
     */
    public Uri getUri() {
        Uri result = null;
        if (uri != null) {
            result = uri;
        } else if (url != null){
            result = UriUtils.urlToUri(url);
        }
        return result;
    }

    /**
     * Get stored Url or Url representation of stored Uri
     * @return  Uri
     */
    public URL getUrl() {
        URL result = null;
        if (url != null) {
            result = url;
        } else if (uri != null){
            result = UriUtils.uriToUrl(uri);
        }
        return result;
    }
}
