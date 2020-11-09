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

package com.ianbuttimer.tidder.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ianbuttimer.tidder.R;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * WebView dialog used for authentication purposes
 */
@SuppressWarnings("unused")
public class AuthenticateDialog extends Dialog {

    @BindView(R.id.web_authD) WebView webView;

    public AuthenticateDialog(@NonNull Context context) {
        super(context);
    }

    public AuthenticateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public AuthenticateDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_authenticate);

        ButterKnife.bind(this);

        setJavaScriptEnabled(true);
    }


    /**
     * Loads the given URL.
     * @param url   the URL of the resource to load
     */
    void loadUrl(String url) {
        webView.loadUrl(url);
    }

    /**
     * Loads the given URL.
     * @param url   the URL of the resource to load
     */
    void loadUrl(URL url) {
        loadUrl(url.toString());
    }

    /**
     * Loads the given data into this WebView, using baseUrl as the base URL for the content.
     * @param baseUrl       URL to use as the page's base URL. If null defaults to 'about:blank'.
     * @param data          String of data in the given encoding mimeType
     * @param mimeType      MIME type of the data, e.g. 'text/html'. This value may be null.
     * @param encoding      encoding of the data This value may be null.
     * @param historyUrl    URL to use as the history entry. If null defaults to 'about:blank'. If non-null, this must be a valid URL.
     */
    void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    /**
     * Invalidate the whole webview
     */
    void invalidate() {
        webView.invalidate();
    }

    /**
     * Stops the current loading
     */
    void stopLoading() {
        webView.stopLoading();
    }

    /**
     * Sets the WebViewClient that will receive various notifications and requests. This will replace the current handler.
     * @param client     an implementation of WebViewClient
     */
    void setWebViewClient(WebViewClient client) {
        webView.setWebViewClient(client);
    }

    /**
     * Sets the chrome handler. This is an implementation of WebChromeClient for use in handling JavaScript dialogs, favicons, titles, and the progress.
     * @param client    an implementation of WebChromeClient
     */
    void setWebChromeClient(WebChromeClient client) {
        webView.setWebChromeClient(client);
    }

    /**
     * Enable/disable javascript execution
     * @param enabled   Enable/disable flag
     */
    public void setJavaScriptEnabled(boolean enabled) {
        webView.getSettings().setJavaScriptEnabled(enabled);
    }



}
