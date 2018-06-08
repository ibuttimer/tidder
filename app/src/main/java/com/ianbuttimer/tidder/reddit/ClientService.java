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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ianbuttimer.tidder.data.AbstractIntentService;
import com.ianbuttimer.tidder.exception.HttpException;
import com.ianbuttimer.tidder.net.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Headers;
import okhttp3.MediaType;
import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Api client IntentService
 */

public class ClientService extends AbstractIntentService {

    public static final String RESULT_RECEIVER = "resultReceiver";

    public static final String RESULT_CODE = "resultCode";
    public static final String RESULT_HTTP_CODE = "resultHttpCode";
    public static final String RESULT_TEXT = "resultText";
    public static final String REQUEST_URL = "requestURL";

    public static final String ACTION_POST = "action_post";
    public static final String ACTION_GET = "action_get";

    public static final String EXTRA_URI = "url";
    public static final String EXTRA_RESPONSE_CLASS = "response_class";
    public static final String EXTRA_MEDIA_TYPE = "media_type";
    public static final String EXTRA_BODY_DATA = "body_data";
    public static final String EXTRA_ADDITIONAL_INFO = "additional_info";

    private static final HashMap<String, NetworkUtils.Method> ACTION_METHOD_MAP;

    static {
        ACTION_METHOD_MAP = new HashMap<>();
        ACTION_METHOD_MAP.put(ACTION_POST, NetworkUtils.Method.POST);
        ACTION_METHOD_MAP.put(ACTION_GET, NetworkUtils.Method.GET);
    }

    /**
     * Constructor
     */
    public ClientService() {
        super(ClientService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return; // nothing to do
        }

        RequestExtractor extractor = getRequestExtractor(intent);
        URL url = extractor.url();
        ResultReceiver resultReceiver = extractor.resultReceiver();
        Bundle bundle = null;

        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_POST:
                case ACTION_GET:
                    bundle = httpRequest(url, action, extractor);
                    break;
            }
        }
        if ((resultReceiver != null) && (bundle != null)) {
            resultReceiver.send(bundle.getInt(RESULT_CODE), bundle);
        }
    }

    private Bundle httpRequest(
            URL url, String action, RequestExtractor extractor) {

        ResponseBuilder responseBuilder = getResponseBuilder()
                .requestUrl(url)
                .responseClass(extractor.responseClass());
        int resultCode = RESULT_CANCELED;
        NetworkUtils.Method method = ACTION_METHOD_MAP.get(action);
        String result;
        int code = HTTP_OK;
        if ((url != null) && (method != null)) {
            try {
                Headers headers = RedditClient.getClient().getHeaders();
                result = NetworkUtils.httpResponseStringSync(
                        url, method, headers, extractor.mediaType(), extractor.bodyData());
                resultCode = RESULT_OK;
            } catch (HttpException e) {
                code = e.getCode();
                result = e.getMessage();
                Timber.e(e, "HTTP %s error", method.toString());
            } catch (IOException e) {
                result = e.getMessage();
                Timber.e(e, "HTTP %s error", method.toString());
            }
            responseBuilder.httpCode(code)
                            .resultText(result);
        } else {
            if (url == null) {
                Timber.e("HTTP: missing URL");
            }
            if (method == null) {
                Timber.e("HTTP: missing mMethod");
            }
        }
        return responseBuilder.resultCode(resultCode)
                .additionalInfo(extractor.additionalInfo()) // echo back additional info
                .dump()
                .build();
    }

    /**
     * Get a launcher intent for this service
     * @param context   Current context
     * @param action    Action for the service to perform
     * @return  intent
     */
    public static Intent getLaunchIntent(Context context, String action) {
        Intent intent = new Intent(context, ClientService.class);
        intent.setAction(action);
        return intent;
    }

    /** Builder class for ClientService arguments */
    public static class RequestBuilder extends AbstractIntentService.Builder {

        public RequestBuilder(Context context, @Nullable String action) {
            super(context, ClientService.class, action);
        }

        @Override
        public RequestBuilder action(@Nullable String action) {
            super.action(action);
            return this;
        }

        public RequestBuilder uri(Uri uri) {
            if (uri != null) {
                mIntent.putExtra(EXTRA_URI, uri);
            }
            return this;
        }

        public RequestBuilder uri(String uri) {
            if (!TextUtils.isEmpty(uri)) {
                uri(Uri.parse(uri));
            }
            return this;
        }

        public RequestBuilder uri(URL url) {
            return uri(NetworkUtils.convertURLToUri(url));
        }

        public RequestBuilder responseClass(Class responseClass) {
            mIntent.putExtra(EXTRA_RESPONSE_CLASS, responseClass);
            return this;
        }

        public RequestBuilder mediaType(MediaType mediaType) {
            mIntent.putExtra(EXTRA_MEDIA_TYPE, mediaType.toString());
            return this;
        }

        public RequestBuilder bodyData(String bodyData) {
            mIntent.putExtra(EXTRA_BODY_DATA, bodyData);
            return this;
        }

        public RequestBuilder additionalInfo(Bundle additionalInfo) {
            mIntent.putExtra(EXTRA_ADDITIONAL_INFO, additionalInfo);
            return this;
        }

        @Override
        public RequestBuilder resultReceiver(ResultReceiver resultReceiver) {
            super.resultReceiver(resultReceiver);
            return this;
        }

        public Intent build() {
            return (Intent)mIntent.clone();
        }
    }

    public static RequestBuilder getRequestBuilder(Context context, String action) {
        return new RequestBuilder(context, action);
    }

    /** ClientService argument extractor class */
    public static class RequestExtractor extends AbstractIntentService.Extractor {

        public RequestExtractor(@Nullable Intent intent) {
            super(intent);
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable public Uri uri() {
            Uri uri = null;
            if (hasExtra(EXTRA_URI)) {
                uri = mIntent.getParcelableExtra(EXTRA_URI);
            }
            return uri;
        }

        @Nullable public URL url() {
            URL url = null;
            Uri uri = uri();
            if (uri != null) {
                url = NetworkUtils.convertUriToURL(uri);
            }
            return url;
        }

        @Nullable public Class responseClass() {
            Class responseClass = null;
            if (hasExtra(EXTRA_RESPONSE_CLASS)) {
                responseClass = (Class)mIntent.getSerializableExtra(EXTRA_RESPONSE_CLASS);
            }
            return responseClass;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable public MediaType mediaType() {
            MediaType mediaType = null;
            if (hasExtra(EXTRA_MEDIA_TYPE)) {
                mediaType = MediaType.parse(mIntent.getStringExtra(EXTRA_MEDIA_TYPE));
            }
            return mediaType;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable public String bodyData() {
            String bodyData = null;
            if (hasExtra(EXTRA_BODY_DATA)) {
                bodyData = mIntent.getStringExtra(EXTRA_BODY_DATA);
            }
            return bodyData;
        }

        @SuppressWarnings("ConstantConditions")
        @Nullable public Bundle additionalInfo() {
            Bundle additionalInfo = null;
            if (hasExtra(EXTRA_ADDITIONAL_INFO)) {
                additionalInfo = mIntent.getParcelableExtra(EXTRA_ADDITIONAL_INFO);
            }
            return additionalInfo;
        }
    }

    public static RequestExtractor getRequestExtractor(Intent intent) {
        return new RequestExtractor(intent);
    }

    /** Builder class for a ClientService response */
    public static class ResponseBuilder extends BundleBuilder {

        public ResponseBuilder() {
            super();
        }

        public ResponseBuilder resultCode(int resultCode) {
            mBundle.putInt(RESULT_CODE, resultCode);
            return this;
        }

        public ResponseBuilder resultText(String result) {
            mBundle.putString(RESULT_TEXT, result);
            return this;
        }

        public ResponseBuilder requestUrl(URL url) {
            mBundle.putSerializable(REQUEST_URL, url);
            return this;
        }

        public ResponseBuilder httpCode(int code) {
            mBundle.putInt(RESULT_HTTP_CODE, code);
            return this;
        }

        public ResponseBuilder responseClass(Class responseClass) {
            mBundle.putSerializable(EXTRA_RESPONSE_CLASS, responseClass);
            return this;
        }

        public ResponseBuilder additionalInfo(Bundle additionalInfo) {
            mBundle.putParcelable(EXTRA_ADDITIONAL_INFO, additionalInfo);
            return this;
        }
    }

    public static ResponseBuilder getResponseBuilder() {
        return new ResponseBuilder();
    }

    /** ClientService response extractor class */
    public static class ResponseExtractor extends BundleExtractor {

        public ResponseExtractor(@Nullable Bundle bundle) {
            super(bundle);
        }

        public int resultCode() {
            return getInt(RESULT_CODE, RESULT_CANCELED);
        }

        @Nullable public String resultText() {
            return getString(RESULT_TEXT);
        }

        @Nullable public URL requestUrl() {
            URL url = null;
            if (containsKey(REQUEST_URL)) {
                url = (URL)mBundle.getSerializable(REQUEST_URL);
            }
            return url;
        }

        public int httpCode() {
            return getInt(RESULT_HTTP_CODE, 0);
        }

        @Nullable public Class responseClass() {
            Class responseClass = null;
            if (containsKey(EXTRA_RESPONSE_CLASS)) {
                responseClass = (Class) mBundle.getSerializable(EXTRA_RESPONSE_CLASS);
            }
            return responseClass;
        }

        @Nullable public Bundle additionalInfo() {
            Bundle additionalInfo = null;
            if (containsKey(EXTRA_ADDITIONAL_INFO)) {
                additionalInfo = mBundle.getParcelable(EXTRA_ADDITIONAL_INFO);
            }
            return additionalInfo;
        }
    }

    public static ResponseExtractor getResponseExtractor(Bundle bundle) {
        return new ResponseExtractor(bundle);
    }

}
