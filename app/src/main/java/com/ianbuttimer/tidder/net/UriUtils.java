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

package com.ianbuttimer.tidder.net;

import android.content.ContentUris;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.ianbuttimer.tidder.utils.Utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Utility class for Uri functionality
 */
@SuppressWarnings("unused")
public class UriUtils {

    /**
     * Make a 'with id' Uri
     * @param contentUri    base content Uri
     * @param id            Id to use
     * @return  Uri
     */
    public static Uri getWithIdUri(Uri contentUri, int id) {
        return ContentUris.withAppendedId(contentUri, id);
    }

    /**
     * Make a 'with id' Uri
     * @param contentUri    base content Uri
     * @param id            Id to use
     * @return  Uri
     */
    public static Uri getWithIdUri(Uri contentUri, String id) {
        return contentUri.buildUpon().appendPath(id).build();
    }

    /**
     * Make a 'with id' Uri
     * @param contentUri    base content Uri
     * @param id            Id to use
     * @param info          additional info
     * @return  Uri
     */
    public static Uri getWithIdAdditionalInfoUri(Uri contentUri, int id, String info) {
        return ContentUris.withAppendedId(contentUri, id).buildUpon()
                .appendPath(info).build();
    }

    /**
     * Get the id from a 'with id' Uri
     * @param uri   Uri to get id from
     * @return  Id string or empty string if Uri doesn't represent a 'with id' Uri
     */
    public static String getIdFromWithIdUri(Uri uri) {
        String id = "";
        if (uri != null) {
            id = uri.getLastPathSegment();
            if (!TextUtils.isDigitsOnly(id)) {
                id = "";
            }
        }
        return id;
    }

    /**
     * Get a selection args id array from a 'with id' Uri
     * @param uri   Uri to get id from
     * @return  Id array
     */
    public static String[] getIdSelectionArgFromWithIdUri(@NonNull Uri uri) {
        String[] array;
        String id = getIdFromWithIdUri(uri);
        if (TextUtils.isEmpty(id)) {
            array = new String[] {};
        } else {
            array = new String[] { id };
        }
        return array;
    }

    /**
     * Convert a Url to a Uri
     * @param url   Url to convert
     * @return  Equivalent Uri
     */
    public static Uri urlToUri(@NonNull URL url) {
        Uri uri = null;
        try {
            URI netUri = url.toURI();
            uri = Uri.parse(netUri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * Convert a Uri to a Url
     * @param uri   Uri to convert
     * @return  Equivalent Url
     */
    public static URL uriToUrl(@NonNull Uri uri) {
        URL url = null;
        try {
            URI netUri = URI.create(uri.toString());
            url = netUri.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Convert a string to the equivalent Uri
     * @param str   String to convert
     * @return  Uri
     */
    public static Uri parse(String str) {
        Uri uri = Uri.EMPTY;
        if (str != null) {
            uri = Uri.parse(str);;
        }
        return uri;
    }

    /**
     * Check if a Uri is actionable
     * @param uri   Uri to check
     * @return  <code>true</code> if actionable
     */
    public static boolean actionable(Uri uri) {
        return ((uri != null) && !Uri.EMPTY.equals(uri));
    }


}
