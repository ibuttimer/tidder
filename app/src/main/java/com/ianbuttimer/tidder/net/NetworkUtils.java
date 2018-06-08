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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.ianbuttimer.tidder.BuildConfig;
import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.exception.HttpException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Network-related utility function class
 */
@SuppressWarnings("unused")
public class NetworkUtils {

    public static final String PATH_JOIN = "/";

    public enum Method { PUT, POST, GET, DELETE }

    public static final MediaType MEDIA_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TEXT = MediaType.parse("text/plain; charset=utf-8");
    public static final MediaType MEDIA_FORM = MediaType.parse("application/x-www-form-urlencoded");    // form content type

    private final static OkHttpClient client;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        client = builder.build();
    }

    /**
     * This method synchronously returns the entire resultText from a HTTP response.
     * @param url   The URL to fetch the HTTP response from.
     * @param headers request headers
     * @param type  content type of HTTP request
     * @param data  content data
     * @return The contents of the HTTP response, or <code>null</code>
     * @throws IOException if the request could not be executed due to cancellation, a connectivity
     * problem or timeout.
     * @throws HttpException If the response was not successfully received, understood, and accepted.
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static String httpResponseStringSync(URL url, Method method, Headers headers, MediaType type, String data) throws IOException {
        Request request = httpRequest(url, method, headers, type, data);
        Call call = client.newCall(request);
        Response response = null;
        String body;

        try {
            response = call.execute();

            if (!response.isSuccessful()) {
                throw new HttpException("HTTP error " + response, response);
            }

            logHeaders(response);

            // response body can only be consumed once, and not on another thread
            body = getResponseBodyString(response);
            if (body != null) {
                body = new String(body);
            }
        }
        finally {
            //  must close the response body to prevent resource leaks
            if (response != null) {
                response.close();
            }
        }
        return body;
    }

    /**
     * This method synchronously returns the entire resultText from a HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @param headers request headers
     * @return The contents of the HTTP response, or <code>null</code>
     * @throws IOException if the request could not be executed due to cancellation, a connectivity
     * problem or timeout.
     * @throws HttpException If the response was not successfully received, understood, and accepted.
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static String httpResponseStringSync(URL url, Method method, Headers headers) throws IOException {
        return httpResponseStringSync(url, method, headers, null, null);
    }

    /**
     * Get the message corresponding to an error
     * @param e     Error exception
     * @return  Resource id of error message
     */
    public static @StringRes int getErrorId(IOException e) {
        int msgId = R.string.invalid_response;
        if (e instanceof UnknownHostException) {
            msgId = R.string.cant_contact_server;
        } else if (e instanceof HttpException) {
            if (((HttpException) e).isUnauthorised()) {
                msgId = R.string.unauthorised_access;
            }
        }
        return msgId;
    }

    /**
     * Get the response body from a SubredditsSearchResponse
     * @param response  SubredditsSearchResponse to get body from
     * @return  Body as a string
     */
    public static String getResponseBodyString(@NonNull Response response) {
        String bodyString = null;
        try {
            ResponseBody body = response.body();
            if (body != null) {
                bodyString = body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodyString;
    }

    /**
     * Generate a HTTP request with no body content
     * @param url       The URL to fetch the HTTP response from
     * @param method    http method
     * @param headers   request headers
     * @return Http request
     */
    private static Request httpRequest(URL url, Method method, Headers headers) {
        return httpRequest(url, method, headers, null, null);
    }

    /**
     * Generate a HTTP request with body content
     * @param url       The URL to fetch the HTTP response from
     * @param method    http method
     * @param headers   request headers
     * @param type      content type of HTTP request
     * @param data      content data
     * @return Http request
     */
    private static Request httpRequest(URL url, Method method, Headers headers, MediaType type, String data) {
        Request.Builder builder = new Request.Builder()
                                        .url(url.toString());
        String headersDbg = "";
        if (headers != null) {
            builder.headers(headers);
            headersDbg = headers.toString();
        }
        String typeDataDbg = "";
        if ((type != null) && (data != null)){
            RequestBody body = RequestBody.create(type, data);
            switch (method) {
                case POST:
                    builder.post(body);
                    break;
                case PUT:
                    builder.put(body);
                    break;
                case DELETE:
                    builder.delete(body);
                    break;
            }
            typeDataDbg = type.toString() + "\n" + data;
        } else {
            switch (method) {
                case GET:
                    builder.get();
                    break;
                case DELETE:
                    builder.delete();
                    break;
            }
        }
        Timber.d("HttpRequest: %s\n%s\n%s", url.toString(), headersDbg, typeDataDbg);
        return builder.build();
    }

    /**
     * Generate headers for a HTTP request.
     * @param map   mDataMap of the field/value pairs
     * @return Http headers
     */
    public static Headers makeHttpRequestHeaders(Map<String, String> map) {
        Headers.Builder builder = new Headers.Builder();
        for (String key : map.keySet()) {
            builder.add(key, map.get(key));
        }
        return builder.build();
    }

    /**
     * This method synchronously returns the entire resultText from a HTTP response.
     * @param url       The URL to fetch the HTTP response from.
     * @param method    http method
     * @param headers   request headers
     * @param type      content type of HTTP request
     * @param data      content data
     * @return The contents of the HTTP response, or <code>null</code>
     * @throws IOException If the response was not successfully received, understood, and accepted.
     * @throws HttpException If the response was unauthorised
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static JSONObject httpResponseJsonSync(URL url, Method method, Headers headers, MediaType type, String data) throws IOException {
        String body = httpResponseStringSync(url, method, headers, type, data);
        JSONObject json = null;
        try {
            if (body != null) {
                json = new JSONObject(body);
            }
        }
        catch (JSONException e) {
            Timber.e(e, "Error parsing response %s", body);
        }
        return json;
    }

    /**
     * This method synchronously returns the entire resultText from a HTTP response.
     * @param url       The URL to fetch the HTTP response from.
     * @param method    http method
     * @param headers   request headers
     * @return The contents of the HTTP response, or <code>null</code>
     * @throws IOException If the response was not successfully received, understood, and accepted.
     * @throws HttpException If the response was unauthorised
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static JSONObject httpResponseJsonSync(URL url, Method method, Headers headers) throws IOException {
        return httpResponseJsonSync(url, method, headers, null, null);
    }

    /**
     * This method synchronously returns the entire resultText from a HTTP response.
     * @param url       The URL to fetch the HTTP response from.
     * @param method    http method
     * @return The contents of the HTTP response, or <code>null</code>
     * @throws IOException If the response was not successfully received, understood, and accepted.
     * @throws HttpException If the response was unauthorised
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static JSONObject httpResponseJsonSync(URL url, Method method) throws IOException {
        return httpResponseJsonSync(url, method, null);
    }

    /**
     * Log http response headers
     * @param response  Http response
     */
    private static void logHeaders(Response response) {
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            Timber.d("header - %s: %s", responseHeaders.name(i), responseHeaders.value(i));
        }
    }

    /**
     * This method asynchronously returns the entire resultText from a HTTP response.
     * @param url       The URL to fetch the HTTP response from.
     * @param method    http method
     * @param headers   request headers
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static void httpResponseStringAsync(URL url, Method method, Headers headers, Callback callback) {
        Request request = httpRequest(url, method, headers);
        client.newCall(request).enqueue(callback);
    }

    /**
     * This method asynchronously returns the entire resultText from a HTTP response.
     * @param url       The URL to fetch the HTTP response from.
     * @param method    http method
     * @param callback  Callback
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static void httpResponseStringAsync(URL url, Method method, Callback callback) {
        httpResponseStringAsync(url, method, null, callback);
    }

    /**
     * Check if an internet connection is available
     * @param context   The current context
     * @return  true if internet connection is available
     */
    public static boolean isInternetAvailable(Context context) {
        return NetworkStatusReceiver.isInternetAvailable(context);
    }

    /**
     * Convert a URI to a URL.
     * @param uri   URI to convert
     * @return The URL.
     */
    @Nullable public static URL convertUriToURL(Uri uri) {
        URL url = null;
        if (uri != null) {
            try {
                url = new URL(uri.toString());
                Timber.v("Built URL %s", url.toString());
            } catch (MalformedURLException e) {
                Timber.e(e);
            }
        }
        return url;
    }

    /**
     * Convert a URL to a Uri.
     * @param url   URI to convert
     * @return The URL.
     */
    @Nullable public static Uri convertURLToUri(URL url) {
        Uri uri = null;
        if (url != null) {
            uri = Uri.parse(url.toString());
            Timber.v("Built Uri %s", uri.toString());
        }
        return uri;
    }

    /**
     * Get a mDataMap of the key/value pairs in the specified string
     * @param str               String to parse
     * @param paramDelimiter    key/value pair delimiter
     * @param keyDelimiter      key value delimiter
     * @return  Key/value mDataMap
     */
    public static Map<String, String> parseKeyValuePairString(String str, String paramDelimiter, String keyDelimiter) {
        Map<String, String> map = new ArrayMap<>();
        if (!TextUtils.isEmpty(str)) {
            String[] splits = str.split(paramDelimiter);
            int keyDelimLen = keyDelimiter.length();
            for (String split : splits) {
                if (!TextUtils.isEmpty(split)) {
                    int index = split.indexOf(keyDelimiter);
                    String key;
                    String value;
                    if (index > 0) {
                        key = split.substring(0, index);
                        value = split.substring(index + keyDelimLen);
                    } else if (index == 0) {
                        key = split.substring(keyDelimLen);
                        value = key;
                    } else {
                        key = split;
                        value = key;
                    }
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    /**
     * Generate a key/value pairs string
     * @param map               mDataMap of the key/value pairs
     * @param paramDelimiter    key/value pair delimiter
     * @param keyDelimiter      key value delimiter
     * @return  Key/value mDataMap
     */
    public static String makeKeyValuePairString(Map<String, String> map, String paramDelimiter, String keyDelimiter) {
        StringBuilder sb = new StringBuilder();
        if ((map != null) && !map.isEmpty()) {
            for (String key : map.keySet()) {
                if (sb.length() > 0) {
                    sb.append(paramDelimiter);
                }
                sb.append(key);
                sb.append(keyDelimiter);
                sb.append(map.get(key));
            }
        }
        return sb.toString();
    }

    /**
     * Trim the start of a URL path
     * @param path      Path to trim
     * @return  Trimmed URL path
     */
    public static String trimUrlPathStart(String path) {
        String trimmed = path;
        if (!TextUtils.isEmpty(trimmed)) {
            if (path.startsWith(PATH_JOIN)) {
                trimmed = path.substring(PATH_JOIN.length());
            }
        }
        return trimmed;
    }

    /**
     * Trim the end of a URL path
     * @param path      Path to trim
     * @return  Trimmed URL path
     */
    public static String trimUrlPathEnd(String path) {
        String trimmed = path;
        if (!TextUtils.isEmpty(trimmed)) {
            if (path.endsWith(PATH_JOIN)) {
                trimmed = path.substring(0, trimmed.length() - PATH_JOIN.length());
            }
        }
        return trimmed;
    }

    /**
     * Trim the start and end of a URL path
     * @param path      Path to trim
     * @return  Trimmed URL path
     */
    public static String trimUrlPath(String path) {
        return trimUrlPathStart(trimUrlPathEnd(path));
    }

    /**
     * Join to parts of a URL path
     * @param part1     First part of path
     * @param part2     Second part of path
     * @return  Joined URL path
     */
    public static String joinUrlPaths(String part1, String part2) {
        String joined;
        boolean p1Ends = part1.endsWith(PATH_JOIN);
        boolean p2Starts = part2.startsWith(PATH_JOIN);
        if (p1Ends && p2Starts) {
            joined = part1 + part2.substring(1);
        } else if (!p1Ends && !p2Starts) {
            joined = part1 + PATH_JOIN + part2;
        } else {
            joined = part1 + part2;
        }
        return joined;
    }

    /**
     * Join to parts of a URL path
     * @param parts     Part of path
     * @return  Joined URL path
     */
    public static String joinUrlPaths(String[] parts) {
        String joined = parts[0];
        for (int i = 1, ll = parts.length; i < ll; i++) {
            joined = joinUrlPaths(joined, parts[i]);
        }
        return joined;
    }

}
