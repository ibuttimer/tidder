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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Base64;

import com.ianbuttimer.tidderish.TidderApplication;
import com.ianbuttimer.tidderish.event.RedditClientEvent;
import com.ianbuttimer.tidderish.net.NetworkUtils;
import com.ianbuttimer.tidderish.net.RedditUriBuilder;
import com.ianbuttimer.tidderish.reddit.RedditToken.AuthorisationStatus;
import com.ianbuttimer.tidderish.reddit.get.SubredditsSearchResponse;
import com.ianbuttimer.tidderish.reddit.post.PostRequest;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;
import com.ianbuttimer.tidderish.utils.AppError.Codes;
import com.ianbuttimer.tidderish.utils.Utils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Headers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.ianbuttimer.tidderish.net.NetworkUtils.MEDIA_FORM;
import static com.ianbuttimer.tidderish.reddit.Api.CODE_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.DEVICE_PARAM;
import static com.ianbuttimer.tidderish.reddit.Api.EXACT_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.GRANT_CLIENT;
import static com.ianbuttimer.tidderish.reddit.Api.GRANT_CODE;
import static com.ianbuttimer.tidderish.reddit.Api.GRANT_PARAM;
import static com.ianbuttimer.tidderish.reddit.Api.GRANT_REFRESH;
import static com.ianbuttimer.tidderish.reddit.Api.OVER_18_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.QUERY_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.REFRESH_TOKEN_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.TOKEN_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.TOKEN_TYPE_HINT_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.UNADVERT_DATA;
import static com.ianbuttimer.tidderish.reddit.Api.URL_PARAM;
import static com.ianbuttimer.tidderish.reddit.ClientService.ACTION_GET;
import static com.ianbuttimer.tidderish.reddit.ClientService.ACTION_POST;
import static com.ianbuttimer.tidderish.reddit.ClientService.RESULT_HTTP_CODE;
import static com.ianbuttimer.tidderish.reddit.ClientService.RESULT_TEXT;
import static com.ianbuttimer.tidderish.reddit.RedditClient.ClientStatus.LOGOUT_IN_PROGRESS;
import static com.ianbuttimer.tidderish.reddit.RedditClient.ClientStatus.UNAUTHORISED;

/**
 * A Reddit client class. This class is a singleton.
 */
public class RedditClient {

    public enum ClientStatus {
        UNAUTHORISED,
        /** Authentication mode for "installed" type apps i.e. considered non-confidential.
         *  Max token life 1 hour, no refresh */
        IMPLICIT_AUTHORISATION,
        HYBRID_AUTHORISATION,
        /** Authentication mode for "web" and "script" type apps i.e. considered confidential.
         *  Refreshable token */
        STANDARD_AUTHORISATION,
        /** Authentication mode for a "user-less" Authorization token.
         *  Max token life 1 hour, no refresh */
        APP_ONLY_AUTHORISATION,
        AUTHORISATION_IN_PROGRESS,
        AUTHORISATION_ERROR,
        AUTHORISED,
        LOGOUT_IN_PROGRESS;


        public static boolean isImplicit(ClientStatus status) {
            return IMPLICIT_AUTHORISATION.equals(status);
        }

        public static boolean isHybridOrStandard(ClientStatus status) {
            return (HYBRID_AUTHORISATION.equals(status) || STANDARD_AUTHORISATION.equals(status));
        }

        public static boolean isHybridStandardOrAppOnly(ClientStatus status) {
            return (isHybridOrStandard(status) || APP_ONLY_AUTHORISATION.equals(status));
        }
    }

    enum Method {NO_METHOD, GET_USER }


    private static final String HEADER_AUTH = "Authorization";      //  authorisation field for request header
    private static final String HEADER_USER_AGENT = "User-Agent";   //  user-agent field for request header
    private static final String AUTH_BASIC = "Basic ";              //  basic authorisation field value for request header

    private static RedditClient sClient = null;     // singleton instance

    private ClientStatus mStatus;
    private ClientStatus mAuthMode;
    private static final ReentrantLock sLock = new ReentrantLock();
    private RedditToken mToken;     // current token

    private Headers mHeaders;   // http headers to use for requests
    private static final String sUserAgent;

    private User mUser;

    private static final HashMap<AuthorisationStatus, Codes> AUTH_TO_APP_ERROR_MAP;

    private static final ArrayDeque<Intent> sMethodQueue= new ArrayDeque<>();

    static {
        AUTH_TO_APP_ERROR_MAP = new HashMap<>();
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.UNAUTHORISED, Codes.UNAUTHORISED);
        
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.ACCESS_DENIED, Codes.ACCESS_DENIED);

        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.UNSUPPORTED_RESPONSE_TYPE, Codes.INVALID_AUTH_REQUEST);
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.INVALID_SCOPE, Codes.INVALID_AUTH_REQUEST);
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.INVALID_REQUEST, Codes.INVALID_AUTH_REQUEST);
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.INVALID_GRANT_TYPE, Codes.INVALID_AUTH_REQUEST);
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.INVALID_NO_CODE, Codes.INVALID_AUTH_REQUEST);
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.INVALID_GRANT, Codes.INVALID_AUTH_REQUEST);

        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.INVALID_EXPIRY, Codes.INVALID_AUTH_RESPONSE);
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.INVALID_STATE, Codes.INVALID_AUTH_RESPONSE);
        
        AUTH_TO_APP_ERROR_MAP.put(AuthorisationStatus.UNKNOWN_ERROR, Codes.UNKNOWN_AUTH_ERROR);


        sUserAgent = Utils.getUserAgentString(TidderApplication.getWeakApplicationContext().get());
    }

    /**
     * Constructor
     */
    private RedditClient() {
        init();
    }

    /**
     * Initialise object
     */
    private void init() {
        setStatus(ClientStatus.UNAUTHORISED);
        mToken = new RedditToken();
        mUser = new User();
        mAuthMode = null;
        mHeaders = null;
    }

    /**
     * Get a reference to the client
     * @return  Client reference
     */
    public static RedditClient getClient() {
        if (sClient == null) {
            sClient = new RedditClient();
        }
        return sClient;
    }

    /**
     * Begin an implicit flow authorisation
     * @param context   The current context
     * @return  Uri to begin process
     */
    public Uri loginImplicit(Context context) {
        Uri uri = RedditUriBuilder.getUserLoginImplicitUri(context, RedditUriBuilder.newAppState());
        setMode(ClientStatus.IMPLICIT_AUTHORISATION);
        return uri;
    }

    /**
     * Begin a hybrid flow authorisation
     * @param context   The current context
     * @return  Uri to begin process
     */
    public Uri loginHybrid(Context context) {
        Uri uri = RedditUriBuilder.getUserLoginStandardUri(context, RedditUriBuilder.newAppState());
        setMode(ClientStatus.HYBRID_AUTHORISATION);
        return uri;
    }

    /**
     * Begin an app only flow authorisation
     * @param context   The current context
     * @return  Uri to begin process
     */
    public void loginAppOnly(Context context) {
        setMode(ClientStatus.APP_ONLY_AUTHORISATION);

        setHeaders(false);  // app only auth requires basic auth headers

        appOnlyToken(context);
    }


    /**
     * Process an authorisation url
     * @param url   urk to process
     * @return  client status
     */
    public ClientStatus processAuthorisation(final Context context, String url) {

        setStatus(ClientStatus.AUTHORISATION_IN_PROGRESS);

        mToken = new RedditToken();
        mToken.parseResponseUrl(url, getMode());

        ClientStatus status;
        boolean post = true;    // post status flag
        if (mToken.isAuthorised()) {
            status = ClientStatus.AUTHORISED;
        } else if (mToken.isCodeAuthorised()) {
            status = ClientStatus.AUTHORISATION_IN_PROGRESS;
            post = false;
        } else {
            status = ClientStatus.AUTHORISATION_ERROR;
        }

        ClientStatus clientStatus = setStatus(status, post);
        setHeaders();

        if (mToken.isCodeAuthorised()) {
            retrieveToken(context);
        }

        return clientStatus;
    }

    /**
     * Process an authorisation url
     * @param uri   Uri to process
     * @return  client status
     */
    public ClientStatus processAuthorisation(final Context context, Uri uri) {
        return processAuthorisation(context, uri.toString());
    }


    public void logout(Context context) {

        // generate POST data: token=TOKEN&token_type_hint=TOKEN_TYPE
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put(TOKEN_DATA, mToken.getToken());
        map.put(TOKEN_TYPE_HINT_DATA, mToken.getTokenTypeHint());

        setHeaders(false);  // logout requires basic auth headers

        setStatus(LOGOUT_IN_PROGRESS);

        startServiceForPost(context,
                RedditUriBuilder.getLogoutUri(),
                map,
                new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == RESULT_OK) {
                            setStatus(UNAUTHORISED, true);
                            init();
                        } else if (resultCode == RESULT_CANCELED) {
                            postEvent(Codes.LOGOUT_ERROR,
                                    resultData.getInt(RESULT_HTTP_CODE),
                                    resultData.getString(RESULT_TEXT));
                        }
                        setHeaders();
                    }
                }
        );

    }

    public void retrieveToken(Context context) {

        // generate POST data: grant_type=authorization_code&code=CODE&redirect_uri=URI
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put(GRANT_PARAM, GRANT_CODE);
        map.put(CODE_DATA, mToken.getToken());
        map.put(URL_PARAM, RedditUriBuilder.getRedirectUri().toString());

        startServiceForToken(context, map);
    }

    public void refreshToken(Context context) {

        // generate POST data: grant_type=refresh_token&refresh_token=TOKEN
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put(GRANT_PARAM, GRANT_REFRESH);
        map.put(REFRESH_TOKEN_DATA, mToken.getRefresh());

        startServiceForToken(context, map);
    }

    private Intent getLaunchIntent(Context context, String action, Uri uri, ResultReceiver receiver) {
        return getLaunchBuilder(context, action, uri, receiver)
                .build();
    }

    private ClientService.RequestBuilder getLaunchBuilder(
            Context context, String action, Uri uri, ResultReceiver receiver) {
        return ClientService.getRequestBuilder(context, action)
                .uri(uri)
                .resultReceiver(receiver);
    }

    public void startServiceForPost(Context context, Uri uri, ArrayMap<String, String> map, ResultReceiver receiver) {
        startServiceForPost(context, uri, null, map, receiver);
    }

    public void startServiceForPost(Context context, Uri uri, Class<?> responseClass, ArrayMap<String, String> map, ResultReceiver receiver) {
        Intent intent = getLaunchBuilder(context, ACTION_POST, uri, receiver)
                .mediaType(MEDIA_FORM)
                .bodyData(NetworkUtils.makeKeyValuePairString(map, "&", "="))
                .responseClass(responseClass)
                .build();

        startService(context, intent);
    }

    public void startServiceForPost(Context context, PostRequest request, ResultReceiver receiver) {
        Intent intent = getLaunchBuilder(context, ACTION_POST, request.getUri(), receiver)
                .mediaType(MEDIA_FORM)
                .bodyData(
                        NetworkUtils.makeKeyValuePairString(
                                request.getDataMap(), "&", "="))
                .responseClass(request.getResponseClass())
                .additionalInfo(request.getAdditionalInfo())
                .build();

        startService(context, intent);
    }

    public void startServiceForGet(Context context, Uri uri, ResultReceiver receiver) {
        Intent intent = getLaunchIntent(context, ACTION_GET, uri, receiver);

        startService(context, intent);
    }

    public void startServiceForGet(Context context, Request request, ResultReceiver receiver) {
        Intent intent = getLaunchBuilder(context, ACTION_GET, request.getUri(), receiver)
                .responseClass(request.getResponseClass())
                .additionalInfo(request.getAdditionalInfo())
                .build();

        startService(context, intent);
    }

    private void startService(Context context, Intent intent) {
        if (mToken.isRefreshable()) {
            sMethodQueue.push(intent);

            refreshToken(context);
        } else {
            context.startService(intent);
        }
    }

    private void startServiceForToken(Context context, ArrayMap<String, String> map) {
        startServiceForPost(context, RedditUriBuilder.getTokenRetrievalUri(),
                        map, new TokenDelayMethodReceiver(new Handler(), context));
    }

    public void appOnlyToken(Context context) {

        // generate POST data: grant_type=https://oauth.reddit.com/grants/installed_client&device_id=DEVICE_ID
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put(GRANT_PARAM, GRANT_CLIENT);
        map.put(DEVICE_PARAM, Api.getDeviceId());

        startServiceForToken(context, map);
    }


    public void retrieveUser(Context context) {

        startServiceForGet(context,
                RedditUriBuilder.getUserUri(),
                new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == RESULT_OK) {
                            String json = resultData.getString(RESULT_TEXT);
                            mUser = new User(json);
                            postEvent(RedditClientEvent.newUserValidEvent());
                        } else if (resultCode == RESULT_CANCELED) {
                            postEvent(Codes.USER_INFO_ERROR,
                                    resultData.getInt(RESULT_HTTP_CODE),
                                    resultData.getString(RESULT_TEXT));
                        }
                    }
                }
        );
    }

    /**
     * Query subreddits
     * @param context   The current context
     * @see <a href="https://www.reddit.com/dev/api/oauth#POST_api_search_subreddits">POST /api/search_subreddits</a>
     */
    public void querySubreddits(final Context context) {

        // generate POST data: grant_type=https://oauth.reddit.com/grants/installed_client&device_id=DEVICE_ID
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put(EXACT_DATA, Boolean.FALSE.toString());
        map.put(OVER_18_DATA, Boolean.FALSE.toString());
        map.put(UNADVERT_DATA, Boolean.TRUE.toString());
        map.put(QUERY_DATA, "");

        startServiceForPost(context,
                RedditUriBuilder.querySubredditsUri(),
                map,
                new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == RESULT_OK) {
                            String json = resultData.getString(RESULT_TEXT);

                            SubredditsSearchResponse response =
                                    new SubredditsSearchResponse(json);



                        } else if (resultCode == RESULT_CANCELED) {
                            postEvent(Codes.USER_INFO_ERROR,
                                    resultData.getInt(RESULT_HTTP_CODE),
                                    resultData.getString(RESULT_TEXT));
                        }
                    }
                }
        );
    }


    @Nullable public User getUser() {
        return mUser;
    }

    public String getUserId() {
        String id = null;
        if (mUser != null) {
            id = mUser.getId();
        }
        if (id == null) {
            id = "";
        }
        return id;
    }

    /**
     * Set the current status
     * @param status    status to set
     * @param post      post event flag
     */
    private ClientStatus setStatus(ClientStatus status, boolean post) {
        sLock.lock();
        try {
            ClientStatus oldStatus = mStatus;
            mStatus = status;
            if (post) {
                postEvent(oldStatus, mStatus);
            }
        } finally {
            sLock.unlock();
        }
        return status;
    }

    /**
     * Set the current status
     * @param status    status to set
     */
    private ClientStatus setStatus(ClientStatus status) {
        return setStatus(status, false);
    }

    /**
     * Return the current status
     * @return  current status
     */
    public ClientStatus getStatus() {
        ClientStatus status;
        sLock.lock();
        try {
            status = mStatus;
        } finally {
            sLock.unlock();
        }
        return status;
    }

    /**
     * Set the current authentication mode
     * @param mode    status to set
     */
    private ClientStatus setMode(ClientStatus mode) {
        sLock.lock();
        try {
            mAuthMode = mode;
        } finally {
            sLock.unlock();
        }
        return mode;
    }

    /**
     * Return the current authentication mode
     * @return  current mode
     */
    public ClientStatus getMode() {
        ClientStatus mode;
        sLock.lock();
        try {
            mode = mAuthMode;
        } finally {
            sLock.unlock();
        }
        return mode;
    }

    private void processMethodQueue(Context context) {
        if (!sMethodQueue.isEmpty()) {
            Intent intent = sMethodQueue.pop();
            context.startService(intent);
        }
    }


    /**
     * Check if the client is authorised
     * @return  <code>true</code> if authorised
     */
    public boolean isAuthorised() {
        return ClientStatus.AUTHORISED.equals(getStatus());
    }

    /**
     * Get the current token status
     * @return  status
     */
    public RedditToken.AuthorisationStatus getTokenStatus() {
        return mToken.getStatus();
    }


    /**
     * Set the HTTP headers to use for requests
     */
    private void setHeaders() {
        setHeaders(isAuthorised());
    }

    /**
     * Set the HTTP headers to use for requests
     * @param token     Set token auth headers flag
     */
    private void setHeaders(boolean token) {
        ArrayMap<String, String> map = new ArrayMap<>();
        String authentication;

        if (token) {
            authentication = getTokenAuthString();
        } else {
            authentication = getBasicAuthImplicitString();
        }
        map.put(HEADER_AUTH, authentication);
        map.put(HEADER_USER_AGENT, sUserAgent);
        mHeaders = NetworkUtils.makeHttpRequestHeaders(map);
    }

    private String getTokenAuthString() {
        // used in Implicit grant flow
        // Authorization: bearer TOKEN
        return mToken.getTokenType() + " " + mToken.getToken();
    }

    private String getBasicAuthImplicitString() {
        // Base64 encoded authentication string containing CLIENT_ID and a blank password, separated by a :.
        return getBasicAuthString(RedditUriBuilder.getClientId(), "");
    }

    private String getBasicAuthStandardString() {
        // Base64 encoded authentication string containing CLIENT_ID and client secret, separated by a :.
        return getBasicAuthString(RedditUriBuilder.getClientId(), "");
    }

    private String getBasicAuthString(String username, String password) {
        // Base64 encoded authentication string containing username and a blank password, separated by a :.
        String authString = username + ":" + password;
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
        return AUTH_BASIC + encodedAuthString;
    }

    /**
     * Get the HTTP headers to use for requests
     * @return  headers
     */
    public Headers getHeaders() {
        return mHeaders;
    }

    /**
     * Notify subscribers of a client event
     * @param event     Event to post
     */
    private void postEvent(RedditClientEvent event) {
        PostOffice.postEvent(event);
    }

    /**
     * Notify subscribers of an status change event
     * @param oldStatus     previous status of client
     * @param newStatus     current status of client
     */
    private void postEvent(ClientStatus oldStatus, ClientStatus newStatus) {
        postEvent(RedditClientEvent.newStatusEvent(oldStatus, newStatus));
    }

    /**
     * Notify subscribers of an authorisation error event
     * @param error         app error code
     * @param httpCode      http code
     * @param message       http error message
     */
    private void postEvent(Codes error, int httpCode, String message) {
        postEvent(RedditClientEvent.newAuthErrorEvent(error, httpCode, message));
    }

    /**
     * ResultReceiver to handle token receipt
     */
    class TokenDelayMethodReceiver extends DelayMethodReceiver {

        public TokenDelayMethodReceiver(Handler handler, @Nullable Context context) {
            super(handler, context);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_OK) {
                // resultText is provided in json format
                AuthorisationStatus status = mToken.parseResponseJson(resultData.getString(RESULT_TEXT));
                if (mToken.isAuthorised()) {
                    setStatus(ClientStatus.AUTHORISED, true);

                    processMethodQueue(getContext());
                } else {
                    setStatus(ClientStatus.AUTHORISATION_ERROR, false);

                    Codes error = AUTH_TO_APP_ERROR_MAP.get(status);
                    if (error == null) {
                        error = Codes.UNKNOWN_AUTH_ERROR;
                    }
                    postEvent(error, 0, "");
                }
            } else if (resultCode == RESULT_CANCELED) {
                postEvent(Codes.TOKEN_RETRIEVE_ERROR,
                        resultData.getInt(RESULT_HTTP_CODE),
                        resultData.getString(RESULT_TEXT));
            }
            setHeaders();
        }
    }

}
