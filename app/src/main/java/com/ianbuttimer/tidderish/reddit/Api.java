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

package com.ianbuttimer.tidderish.reddit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.TidderApplication;
import com.ianbuttimer.tidderish.ui.LoginActivity;
import com.ianbuttimer.tidderish.utils.PreferenceControl;
import com.ianbuttimer.tidderish.utils.Utils;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Utility class for Reddit API-related functionality
 */

public class Api {


    public static final String GRANT_PARAM = "grant_type";     // Tells reddit.com which grant type you are requesting a token for
    public static final String DEVICE_PARAM = "device_id";     // unique, per-device ID generated by client

    public static final String GRANT_CLIENT = "https://oauth.reddit.com/grants/installed_client";  // grant type for implicit flow request
    public static final String GRANT_CODE = "authorization_code";                                  // grant type for standard request
    public static final String GRANT_REFRESH = "refresh_token";                                    // grant type value to refresh token

    public static final String ID_PARAM = "client_id";         // Tells reddit.com which app is making the request
    public static final String TYPE_PARAM = "response_type";   // Must be the string "token"
    public static final String STATE_PARAM = "state";          // unique, possibly random, string for each authorization request. Returned with response
    public static final String URL_PARAM = "redirect_uri";     // registered redirect_uri
    public static final String SCOPE_PARAM = "scope";          // space-separated list of scope strings
    public static final String DURATION_PARAM = "duration";    // token duration

    public static final String TYPE_TOKEN = "token";           // token value for type parameter (implicit flow)
    public static final String TYPE_CODE = "code";             // code value for type parameter

    public static final String DURATION_TEMP = "temporary";    // temporary token value for duration parameter
    public static final String DURATION_PERM = "permanent";    // permanent token value for duration parameter

    public static final String CODE_DATA = "code";             // code to exchange for token
    public static final String REFRESH_TOKEN_DATA = GRANT_REFRESH;  // refresh token to exchange for token

    public static final String TOKEN_DATA = "token";                       //  token to revoke
    public static final String TOKEN_TYPE_HINT_DATA = "token_type_hint";   //  The type of token being revoked

    // successful authorisation related
    public static final String ACCESS_TOKEN = "access_token";   // access token parameter
    public static final String ACCESS_CODE = "code";            // access code parameter (to exchange for token)
    public static final String TOKEN_TYPE = "token_type";       // token type parameter
    public static final String TOKEN_EXPIRY = "expires_in";     // seconds until the token expires parameter (seconds)
    public static final String TOKEN_SCOPE = "scope";           // scope of the token parameter
    public static final String TOKEN_STATE = "state";           // app state parameter
    public static final String REFRESH_TOKEN = GRANT_REFRESH;   // refresh token parameter
    public static final String DEVICE_TOKEN = DEVICE_PARAM;     // device id token parameter

    // unsuccessful authorisation related
    public static final String ACCESS_ERROR = "error";   // error parameter

    public static final String ERROR_DENIED = "access_denied";                  // User chose not to grant access parameter
    public static final String ERROR_UNSUPPORTED = "unsupported_response_type"; // Invalid response_type parameter in initial request
    public static final String ERROR_SCOPE = "invalid_scope";                   // Invalid scope parameter in initial request
    public static final String ERROR_REQUEST = "invalid_request";               // There was an issue with the request
    public static final String ERROR_GRANT_TYPE = "unsupported_grant_type";     // grant_type parameter was invalid or Http Content type was not set correctly
    public static final String ERROR_NO_CODE = "NO_TEXT";                       // didn't include the code parameter (Note haven't seen in testing)
    public static final String ERROR_INVALID_GRANT = "invalid_grant";           // the code has expired or already been used

    public static final String EXACT_DATA = "exact";                        // if true, only an exact match will be returned
    public static final String OVER_18_DATA = "include_over_18";            // if false, subreddits with over-18 content restrictions will be filtered from the results
    public static final String UNADVERT_DATA = "include_unadvertisable";    // if false, subreddits that have hide_ads set to True or are on the anti_ads_subreddits list will be filtered.
    public static final String QUERY_DATA = "query";                        // a string up to 50 characters long, consisting of printable characters


    /** Reddit scopes */
    public enum Scopes {
        ACCOUNT, CREDDITS, EDIT, FLAIR, HISTORY, IDENTITY, LIVEMANAGE, MODCONFIG, MODCONTRIBUTORS,
        MODFLAIR, MODLOG, MODMAIL, MODOTHERS, MODPOSTS, MODSELF, MODWIKI, MYSUBREDDITS,
        PRIVATEMESSAGES, READ, REPORT, SAVE, SUBMIT, SUBSCRIBE, VOTE, WIKIEDIT, WIKIREAD
    }

    private static final String[] SCOPE_ARRAY;
    private static final String DEVICE_ID;

    static {
        Context context = TidderApplication.getWeakApplicationContext().get();

        SCOPE_ARRAY = context.getResources().getStringArray(R.array.reddit_scopes);

        // generate a device id
        String deviceId = PreferenceControl.getDeviceIdPreference(context);
        String deviceDflt = context.getString(R.string.pref_device_dflt_value);
        if (isEmptyOrMatches(deviceId, deviceDflt)) {
            deviceId = UUID.randomUUID().toString();
            PreferenceControl.setDeviceIdPreference(context, deviceId);
        }
        DEVICE_ID = deviceId;
    }

    /**
     * Generate a space-separated list of scope strings
     * @param scopes    Scopes to generate list for
     * @return  Scope string
     */
    public static String getScopeString(Scopes[] scopes) {
        String scopeStr = "";
        if (Utils.arrayHasSize(scopes)) {
            String[] strings = new String[scopes.length];
            int idx = 0;
            for (Scopes scope : scopes) {
                int ordinal = scope.ordinal();
                if (ordinal < SCOPE_ARRAY.length) {
                    strings[idx++] = SCOPE_ARRAY[ordinal];
                }
            }
            scopeStr = TextUtils.join(" ", strings);
        }
        return scopeStr;
    }

    /**
     * Get the device id used for Application Only OAuth
     * @return device id
     */
    public static String getDeviceId() {
        return DEVICE_ID;
    }

    /**
     * Check the argument matches the device id
     * @param deviceId  id string to check
     * @return  <code>true</code> if state matches
     */
    public static boolean isDeviceId(String deviceId) {
        return (DEVICE_ID.compareTo(deviceId) == 0);
    }

    /**
     * Check if the application configuration is valid
     * @param context   The current context
     * @return  <code>null</code> if the configuration is valid, or an appropriate error message if invalid
     */
    public static @Nullable String isConfigValid(Context context) {
        // check the app meta-data has been configured correctly
        String clientId = Utils.getManifestMetaDataString(context, R.string.client_id_metadata);
        String redirectUri = Utils.getManifestMetaDataString(context, R.string.redirect_uri_metadata);
        String clientIdDflt = context.getString(R.string.client_id_metadata_todo);
        String redirectUriDflt = context.getString(R.string.redirect_uri_metadata_todo);
        boolean clientIdErr = isEmptyOrMatches(clientId, clientIdDflt);
        boolean redirectUriErr = isEmptyOrMatches(redirectUri, redirectUriDflt);
        int msgId = 0;

        String errorMsg = null;

        if (clientIdErr && redirectUriErr) {
            msgId = R.string.config_error_client_id_redirect_uri;
        } else if (clientIdErr) {
            msgId = R.string.config_error_client_id;
        } else if (redirectUriErr) {
            msgId = R.string.config_error_redirect_uri;
        }

        if (msgId == 0) {
            // check intent-filter for login redirect
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUri));

            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY );
            String loginActivity = LoginActivity.class.getName();
            int i;
            for (i = 0; i < list.size(); i++) {
                ResolveInfo resolveInfo = list.get(i);
                if (loginActivity.compareTo(resolveInfo.activityInfo.name) == 0) {
                    // intent-filter set correctly
                    break;
                }
            }
            if (i == list.size()) {
                msgId = R.string.config_error_redirect_uri_intentfilter;
            }
        }

        if (msgId != 0) {
            // error
            errorMsg = MessageFormat.format(
                    context.getString(R.string.config_error), context.getString(msgId));
            Timber.e(errorMsg);
        }

        return errorMsg;
    }

    /**
     * Check if a string is empty or matches another
     * @param string    String to check
     * @param notSet    String to match
     * @return  <code>true</code> if string is empty or matches
     */
    private static boolean isEmptyOrMatches(String string, String notSet) {
        boolean result = TextUtils.isEmpty(string);
        if (!result) {
            result = (string.compareToIgnoreCase(notSet) == 0);
        }
        return result;
    }

}
