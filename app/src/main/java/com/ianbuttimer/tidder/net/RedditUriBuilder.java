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

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.reddit.Api;
import com.ianbuttimer.tidder.utils.RandomStringGenerator;
import com.ianbuttimer.tidder.utils.Utils;

import java.util.concurrent.locks.ReentrantLock;

import timber.log.Timber;

import static com.ianbuttimer.tidder.reddit.Api.*;

/**
 * A Reddit-related Uri builder class
 * For user login documentation, see <a href="https://github.com/reddit/reddit/wiki/OAuth2#authorization-implicit-grant-flow">Authorization (Implicit grant flow)</a>
 * For app login documentation, see <a href="https://github.com/reddit/reddit/wiki/OAuth2#application-only-oauth">Application Only OAuth</a>
 */

public class RedditUriBuilder {

    public static final String BASE_URL = "https://www.reddit.com/";
    public static final String OAUTH_BASE_URL = "https://oauth.reddit.com/";
    public static final String API_PATH = "api/";
    public static final String API_V1_PATH = NetworkUtils.joinUrlPaths(API_PATH, "v1/");

    public static final String API_V1_URL = NetworkUtils.joinUrlPaths(BASE_URL, API_V1_PATH);
    public static final String OAUTH_API_URL = NetworkUtils.joinUrlPaths(OAUTH_BASE_URL, API_PATH);
    public static final String OAUTH_API_V1_URL = NetworkUtils.joinUrlPaths(OAUTH_BASE_URL, API_V1_PATH);

    // Application Only OAuth related
    public static final String APP_LOGIN_URL = NetworkUtils.joinUrlPaths(API_V1_URL, "access_token");


    // Authorization (Implicit grant flow) related
    public static final String USER_LOGIN_URL = NetworkUtils.joinUrlPaths(API_V1_URL, "authorize");
    public static final String USER_LOGIN_SML_SCRN_URL = NetworkUtils.joinUrlPaths(USER_LOGIN_URL, ".compact");

    public static final String sScope = getScopeString(new Scopes[] {
        Scopes.READ, Scopes.IDENTITY
    });

    public static final String GET_USER_URL = NetworkUtils.joinUrlPaths(OAUTH_API_V1_URL, "me");

    public static final int APP_STATE_LENGTH = 20;
    public static String mAppState = null;
    public static final ReentrantLock sLock = new ReentrantLock();

    /**
     * Search subreddits by title and description.
     * @see <a href="https://www.reddit.com/dev/api#GET_subreddits_search">GET /subreddits/search</a>
     */
    public static final String SUBREDDITS_SEARCH_URL = NetworkUtils.joinUrlPaths(OAUTH_BASE_URL, "subreddits/search");
    /**
     * List subreddits that begin with a query string.
     * @see <a href="https://www.reddit.com/dev/api#POST_api_search_subreddits">POST /api/search_subreddits</a>
     */
    public static final String SEARCH_SUBREDDITS_URL = NetworkUtils.joinUrlPaths(OAUTH_API_URL, "search_subreddits");

    public static final String SUBREDDIT_URL_R = "/r";
    public static final String SUBREDDIT_RELATIVE_START_URL = SUBREDDIT_URL_R + "/";

    /**
     * Return information about the subreddit
     * @see <a href="https://www.reddit.com/dev/api#GET_r_{subreddit}_about">/r/<i>subreddit</i>/about</a>
     */
    public static final String SUBREDDIT_ABOUT_BASE_URL = OAUTH_BASE_URL;
    public static final String SUBREDDIT_ABOUT_URL_END = "/about";

    /**
     * Return a listing of things specified by their fullnames.
     * Only Links, Comments, and Subreddits are allowed.
     * @see <a href="https://www.reddit.com/dev/api#GET_api_info">GET [/r/<i>subreddit</i>]/api/info</a>
     */
    public static final String THING_ABOUT_BASE_URL = OAUTH_API_URL;
    public static final String THING_ABOUT_URL_END = "/info.json";
    public static final String THING_ABOUT_URL = NetworkUtils.joinUrlPaths(THING_ABOUT_BASE_URL, THING_ABOUT_URL_END);

    /**
     * Return posts from a subreddit
     */
    public static final String SUBREDDIT_POST_BASE_URL = OAUTH_BASE_URL;
    public static final String SUBREDDIT_POST_URL_END = ".json";

    /**
     * Return a comment tree
     * @see <a href="https://www.reddit.com/dev/api#GET_comments_{article}">[/r/subreddit]/comments/article</a>
     */
    public static final String SUBREDDIT_COMMENT_TREE_BASE_URL = OAUTH_BASE_URL;
    public static final String SUBREDDIT_COMMENT_TREE_URL_MID = "/comments";

    /**
     * Return additional comments omitted from a base comment tree
     * @see <a href="https://www.reddit.com/dev/api#GET_api_morechildren">GET /api/morechildren</a>
     */
    public static final String SUBREDDIT_COMMENT_MORE_BASE_URL = NetworkUtils.joinUrlPaths(OAUTH_API_URL, "morechildren");

    /**
     * List all subreddits
     */
    public static final String ALL_SUBREDDITS_URL = NetworkUtils.joinUrlPaths(OAUTH_BASE_URL, "subreddits.json");

    // logout related
    public static final String USER_LOGOUT_URL = NetworkUtils.joinUrlPaths(API_V1_URL, "revoke_token");



    private static String sClientId;
    private static Uri sRedirectUri;

    static {
        Context context = TidderApplication.getWeakApplicationContext().get();

        String clientId = Utils.getManifestMetaDataString(context, R.string.client_id_metadata);
        if (TextUtils.isEmpty(clientId)) {
            clientId = "";
        }
        sClientId = clientId;

        String redirectUri = Utils.getManifestMetaDataString(context, R.string.redirect_uri_metadata);
        if (TextUtils.isEmpty(redirectUri)) {
            redirectUri = "";
        }
        sRedirectUri = Uri.parse(redirectUri);
    }

    /**
     * Builds a Reddit login Uri
     * @return  Uri
     */
    public static Uri getAppLoginUri() {

        // https://www.reddit.com/api/v1/access_token?grant_type=https://oauth.reddit.com/grants/installed_client&device_id=DEVICE_ID

        Uri.Builder builder = Uri.parse(APP_LOGIN_URL).buildUpon();
        builder.appendQueryParameter(GRANT_PARAM, GRANT_CLIENT);
        builder.appendQueryParameter(DEVICE_PARAM, Api.getDeviceId());

        Uri uri = builder.build();

        verbose(uri);

        return uri;
    }

    /**
     * Builds a Reddit login Uri
     * @param context   The current context
     * @param type      Token type
     * @param state     State string for authorization request
     * @param duration  Token duration (standard flow only)
     * @param scope     Requested scope string
     * @return  Uri
     */
    public static Uri getUserLoginUri(Context context, String type, String state, String duration, String scope) {
        // example implicit requests (for installed apps)
        // https://www.reddit.com/api/v1/authorize?client_id=CLIENT_ID&response_type=TYPE&state=RANDOM_STRING&redirect_uri=URI&scope=SCOPE_STRING
        // example standard requests (for web or script apps)
        // https://www.reddit.com/api/v1/authorize?client_id=CLIENT_ID&response_type=TYPE&state=RANDOM_STRING&redirect_uri=URI&duration=DURATION&scope=SCOPE_STRING
        // can hit www.reddit.com/api/v1/authorize.compact for small screen friendly authorisation

        Uri.Builder builder = Uri.parse(USER_LOGIN_SML_SCRN_URL).buildUpon();
        builder.appendQueryParameter(ID_PARAM, getClientId());
        builder.appendQueryParameter(TYPE_PARAM, type);
        builder.appendQueryParameter(STATE_PARAM, state);
        if (!TextUtils.isEmpty(duration)) {
            builder.appendQueryParameter(DURATION_PARAM, duration);
        }
        builder.appendQueryParameter(URL_PARAM, getRedirectUri().toString());
        if (TextUtils.isEmpty(scope)) {
            scope = context.getString(R.string.reddit_scope_config);
        }
        builder.appendQueryParameter(SCOPE_PARAM, scope);

        Uri uri = builder.build();

        verbose(uri);

        return uri;
    }

    /**
     * Builds a Reddit login Uri for the implicit authorisation flow
     * @param context   The current context
     * @param state     State string for authorization request
     * @return  Uri
     */
    public static Uri getUserLoginImplicitUri(Context context, String state) {
        return getUserLoginUri(context, TYPE_TOKEN, state, null, sScope);
    }

    /**
     * Builds a Reddit login Uri for the standard authorisation flow
     * @param context   The current context
     * @param state     State string for authorization request
     * @return  Uri
     */
    public static Uri getUserLoginStandardUri(Context context, String state) {
        return getUserLoginUri(context, TYPE_CODE, state, DURATION_PERM, sScope);
    }

    /**
     * Builds a Reddit token retrieval Uri for the standard, hybrid and app-only flows
     * @return  Uri
     */
    public static Uri getTokenRetrievalUri() {
        return getSimpleUri(APP_LOGIN_URL);
    }

    /**
     * Builds a Reddit login Uri
     * @return  Uri
     */
    public static Uri getLogoutUri() {
        return getSimpleUri(USER_LOGOUT_URL);
    }

    /**
     * Builds a Reddit get user Uri
     * @return  Uri
     */
    public static Uri getUserUri() {
        return getSimpleUri(GET_USER_URL);
    }

    /**
     * Builds a Reddit query subreddits Uri
     * @return  Uri
     */
    public static Uri querySubredditsUri() {
        return getSimpleUri(SEARCH_SUBREDDITS_URL);
    }



    /**
     * Builds a Reddit Uri
     * @param url   Reddit url
     * @return  Uri
     */
    private static Uri getSimpleUri(String url) {
        Uri uri = Uri.parse(url);
        verbose(uri);
        return uri;
    }

    /**
     * Builds a Reddit Oath Uri
     * @param relativeUrl   the relative path of the Uri to create
     * @return  Uri
     */
    public static Uri getOathUri(String relativeUrl) {
        return Uri.parse(NetworkUtils.joinUrlPaths(OAUTH_BASE_URL, relativeUrl));
    }

    /**
     * Verbose debug
     * @param uri   Built Uri
     */
    private static void verbose(Uri uri) {
        Timber.v("Built Uri %s", uri.toString());
    }





    /**
     * Generate a new random app state
     * @return  app state
     */
    public static String newAppState() {
        String appState;
        sLock.lock();
        try {
            mAppState = RandomStringGenerator.generate(APP_STATE_LENGTH, RandomStringGenerator.ALPHA_NUMERIC);
            appState = mAppState;
        } finally {
            sLock.unlock();
        }
        return appState;
    }

    /**
     * Check the app state argument matches the current app state
     * @param appState  state string to check
     * @return  <code>true</code> if state matches
     */
    public static boolean isAppState(String appState) {
        boolean isState;
        sLock.lock();
        try {
            isState = (mAppState.compareTo(appState) == 0);
        } finally {
            sLock.unlock();
        }
        return isState;
    }

    /**
     * Get the Reddit app client id specified in the manifest
     * @return  client id string
     */
    public static String getClientId() {
        return sClientId;
    }

    /**
     * Get the Reddit app redirect Uri specified in the manifest
     * @return  redirect Uri string
     */
    public static Uri getRedirectUri() {
        return sRedirectUri;
    }



}
