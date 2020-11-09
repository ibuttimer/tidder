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

package com.ianbuttimer.tidder.reddit;

import android.net.Uri;
import android.text.TextUtils;
import android.util.JsonReader;

import com.ianbuttimer.tidder.net.NetworkUtils;
import com.ianbuttimer.tidder.net.RedditUriBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.ianbuttimer.tidder.reddit.Api.*;

/**
 * A Reddit access token
 */
@SuppressWarnings("unused")
public class RedditToken extends BaseObject<RedditToken> {
    
    public enum AuthorisationStatus {
        // corresponding to reddit errors
        UNAUTHORISED,
        AUTHORISED,
        ACCESS_CODE,
        ACCESS_DENIED,
        UNSUPPORTED_RESPONSE_TYPE,
        INVALID_SCOPE,
        INVALID_REQUEST,
        INVALID_GRANT_TYPE,
        INVALID_NO_CODE,
        INVALID_GRANT,
        // home grown errors
        UNKNOWN_ERROR,      // an unknown error
        INVALID_EXPIRY,     // invalid expiry
        INVALID_STATE       // invalid app state received
        ;

        public boolean isAuthorised() {
            return AuthorisationStatus.AUTHORISED.equals(this);
        }

        public boolean isUnauthorised() {
            return AuthorisationStatus.UNAUTHORISED.equals(this);
        }
    }

    private String mToken;      // access token
    private String mTokenType;  // token type
    private String mTokenTypeHint;  // token type hint
    private Date mExpires;      // token expiry
    private String mScope;      // token scope
    private String mRefresh;    // refresh token
    private String mDeviceId;   // device id

    private AuthorisationStatus mStatus;

    private static final HashMap<String, AuthorisationStatus> ERROR_MAP;

    static {
        ERROR_MAP = new HashMap<>();
        ERROR_MAP.put(ERROR_DENIED, AuthorisationStatus.ACCESS_DENIED);
        ERROR_MAP.put(ERROR_UNSUPPORTED, AuthorisationStatus.UNSUPPORTED_RESPONSE_TYPE);
        ERROR_MAP.put(ERROR_SCOPE, AuthorisationStatus.INVALID_SCOPE);
        ERROR_MAP.put(ERROR_REQUEST, AuthorisationStatus.INVALID_REQUEST);
        ERROR_MAP.put(ERROR_GRANT_TYPE, AuthorisationStatus.INVALID_GRANT_TYPE);
        ERROR_MAP.put(ERROR_NO_CODE, AuthorisationStatus.INVALID_NO_CODE);
        ERROR_MAP.put(ERROR_INVALID_GRANT, AuthorisationStatus.INVALID_GRANT);
    }

    /**
     * Default Constructor
     */
    public RedditToken() {
        init();
    }

    @Override
    protected void init() {
        mToken = "";
        mTokenType = "";
        mTokenTypeHint= "";
        mExpires = null;
        mScope = "";
        mRefresh = "";
        mDeviceId = "";
        mStatus = AuthorisationStatus.UNAUTHORISED;
    }

    @Override
    protected RedditToken getInstance() {
        return new RedditToken();
    }

    /**
     * Parse the url string and extract the token information
     * @param url       Url string containing token details
     * @param authMode  Authentication mode
     * @return  Status of token
     */
    public AuthorisationStatus parseResponseUrl(String url, RedditClient.ClientStatus authMode) {
        init();

        if (!TextUtils.isEmpty(url)) {
            // implicit examples
            // http://www.example.com/my_redirect#access_token=TOKEN&token_type=bearer&state=STATE&expires_in=3600&scope=SCOPE
            // http://www.example.com/my_redirect#state=STATE&error=access_denied

            // hybrid/standard examples
            // http://www.example.com/my_redirect?state=P59u6d86MbnkYLeth780&code=OH_Mxaa7Nx4tIjXp2uNSzsgKzSc

            Uri uri = Uri.parse(url);
            String parameters;
            if (RedditClient.ClientStatus.isImplicit(authMode)) {
                parameters = uri.getFragment();
            } else if (RedditClient.ClientStatus.isHybridOrStandard(authMode)) {
                parameters = uri.getQuery();
            } else {
                parameters ="";
            }
            Map<String, String> map = NetworkUtils.parseKeyValuePairString(parameters, "&", "=");
            String state = null;

            for (String key : map.keySet()) {
                String value = map.get(key);
                switch (key) {
                    case ACCESS_TOKEN:
                        mToken = value;
                        mTokenTypeHint = ACCESS_TOKEN;
                        break;
                    case ACCESS_CODE:
                        mToken = value;
                        mTokenType = ACCESS_CODE;
                        break;
                    case TOKEN_TYPE:
                        mTokenType = value;
                        break;
                    case TOKEN_EXPIRY:
                        setExpiry(value, this);
                        break;
                    case TOKEN_SCOPE:
                        mScope = value;
                        break;
                    case TOKEN_STATE:
                        state = value;
                        break;
                    case ACCESS_ERROR:
                        setError(value, this);
                        break;
                }
            }
            // check validity
            if (mStatus.isUnauthorised()) {
                // check its the app state that's expected
                if (RedditUriBuilder.isAppState(state) && !TextUtils.isEmpty(mToken)) {
                    if (isAccessCode()) {
                        mStatus = AuthorisationStatus.ACCESS_CODE;
                        Timber.i("Parsed code (hybrid/standard)");
                    } else {
                        mStatus = AuthorisationStatus.AUTHORISED;
                        Timber.i("Parsed token (implicit): expiry %s", mExpires.toString());
                    }
                } else {
                    mStatus = AuthorisationStatus.INVALID_STATE;
                }
            }
        }
        return mStatus;
    }

    /**
     * Parse the json string and extract the token information
     * @param json   Json string containing token details
     * @return  Status of token
     */
    public AuthorisationStatus parseResponseJson(String json) {
//        {
//            "access_token": Your access token,
//            "token_type": "bearer",
//            "expires_in": Unix Epoch Seconds,
//            "scope": A scope string,
//            "refresh_token": Your refresh token
//        }
//          {
//              "access_token": Your access token,
//              "token_type": "bearer",
//              "device_id": Your device id,
//              "expires_in": Unix Epoch Seconds,
//              "scope": "*"
//          }
//        {
//            "error": "unsupported_grant_type"
//        }

        parseJson(json);

        // check validity
        boolean appOk = Api.isDeviceId(mDeviceId);
        boolean tokenOk = !TextUtils.isEmpty(mToken);
        if (mStatus.isUnauthorised() && (appOk || tokenOk)) {
            mStatus = AuthorisationStatus.AUTHORISED;
            Timber.i("Parsed token (json): expiry %s", mExpires.toString());
        }
        return mStatus;
    }

    @Override
    protected boolean parseToken(JsonReader jsonReader, String name, RedditToken obj)
            throws IOException, IllegalArgumentException {
        checkObject(obj, getClass());

        boolean consumed = true;
        if (ACCESS_TOKEN.equals(name)) {
            obj.mToken = nextString(jsonReader, "");
            obj.mTokenTypeHint = ACCESS_TOKEN;
        } else if (TOKEN_TYPE.equals(name)) {
            obj.mTokenType = nextString(jsonReader, "");
        } else if (TOKEN_EXPIRY.equals(name)) {
            setExpiry(nextString(jsonReader, ""), obj);
        } else if (TOKEN_SCOPE.equals(name)) {
            obj.mScope = nextString(jsonReader, "");
        } else if (REFRESH_TOKEN.equals(name)) {
            obj.mRefresh = nextString(jsonReader, "");
        } else if (ACCESS_ERROR.equals(name)) {
            setError(nextString(jsonReader, ""), obj);
        } else if (DEVICE_TOKEN.equals(name)) {
            obj.mDeviceId = nextString(jsonReader, "");
        } else {
            consumed = false;
        }
        return consumed;
    }

    private void setExpiry(String secFromNow, RedditToken token) {
        try {
            int seconds = Integer.parseInt(secFromNow);
            token.mExpires = new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS));
        } catch (NumberFormatException e) {
            token.mStatus = AuthorisationStatus.INVALID_EXPIRY;
            token.mExpires = new Date();
            Timber.e("Invalid token expiry value: %s", secFromNow);
        }
    }

    private void setError(String value, RedditToken token) {
        AuthorisationStatus status = ERROR_MAP.get(value);
        if (status == null) {
            status = AuthorisationStatus.UNKNOWN_ERROR;
        }
        Timber.i("Token error: %s", value);
        token.mStatus = status;
    }



    public String getToken() {
        return mToken;
    }

    public String getTokenTypeHint() {
        return mTokenTypeHint;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public boolean isAccessCode() {
        return (mTokenType.equals(ACCESS_CODE));
    }

    public Date getExpires() {
        return mExpires;
    }

    public boolean isExpired() {
        boolean expired = true;
        if (mExpires != null) {
            Date now = new Date();
            expired = now.after(mExpires);
        }
        return expired;
    }

    public String getScope() {
        return mScope;
    }

    public AuthorisationStatus getStatus() {
        return mStatus;
    }

    public boolean isAuthorised() {
        return AuthorisationStatus.AUTHORISED.equals(mStatus);
    }

    public boolean isCodeAuthorised() {
        return AuthorisationStatus.ACCESS_CODE.equals(mStatus);
    }

    public String getRefresh() {
        return mRefresh;
    }

    public boolean hasRefresh() {
        return !TextUtils.isEmpty(mRefresh);
    }

    public boolean isRefreshable() {
        return isExpired() && hasRefresh();
    }

    public String getDeviceId() {
        return mDeviceId;
    }
}
