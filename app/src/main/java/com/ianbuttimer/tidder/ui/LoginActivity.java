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

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.net.NetworkUtils;
import com.ianbuttimer.tidder.net.RedditUriBuilder;
import com.ianbuttimer.tidder.reddit.RedditClient;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.utils.Dialog;
import com.ianbuttimer.tidder.utils.Utils;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.EXTRA_TITLE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.btn_login_loginA) Button mLoginBtn;
// FIXME currently not using temporary or device login
//    @BindView(R.id.btn_login_imp_loginA) Button mLoginImpBtn;
//    @BindView(R.id.btn_login_app_loginA) Button mLoginAppBtn;

    private AuthenticateDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Let's display the progress in the activity title bar, like the browser app does.
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        if (!TidderApplication.isConfigValid()) {
            Dialog.showAlertDialog(this, TidderApplication.getConfigErrorMsg());
            mLoginBtn.setEnabled(false);
        }

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TEXT) || intent.hasExtra(EXTRA_TITLE)) {
            Dialog.showAlertDialog(this, intent.getIntExtra(EXTRA_TITLE, 0),
                    intent.getIntExtra(EXTRA_TEXT, 0));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PostOffice.register(this);
    }

    @Override
    protected void onStop() {
        PostOffice.unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if((intent != null) && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri!= null) {
                if (uri.getQueryParameter("error") != null) {
                    String error = uri.getQueryParameter("error");
                    Timber.e("An error has occurred : %s", error);
                } else {
                    String state = uri.getQueryParameter("state");
                    if (RedditUriBuilder.isAppState(state)) {
                        String code = uri.getQueryParameter("code");
                        //                    getAccessToken(code);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean handled = Utils.onOptionsItemSelected(this, item);
        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

    @OnClick(R.id.btn_login_loginA)
    public void onLoginClick() {
        webviewLogin(RedditClient.getClient().loginHybrid(LoginActivity.this));
    }

    public void webviewLogin(final Uri uri) {
        mDialog = new AuthenticateDialog(this);

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                mDialog.loadUrl(NetworkUtils.convertUriToURL(uri));
            }
        });

        mDialog.setTitle(R.string.authorise_title);
        mDialog.show();

        mDialog.setWebViewClient(new LoginWebViewClient());
        mDialog.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                LoginActivity.this.setProgress(progress * 1000);
            }
        });
    }

//    @OnClick(R.id.btn_login_imp_loginA)
//    public void onLoginImplicitClick() {
//        webviewLogin(RedditClient.getClient().loginImplicit(LoginActivity.this));
//    }
//
//    @OnClick(R.id.btn_login_app_loginA)
//    public void onLoginAppClick() {
//        RedditClient.getClient().loginAppOnly(LoginActivity.this);
//    }


    class LoginWebViewClient extends WebViewClient {

        String redirectHost = RedditUriBuilder.getRedirectUri().getHost();

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean override = shouldOverrideUrlLoading(Uri.parse(url));
            if (override) {
                override = super.shouldOverrideUrlLoading(view, url);
            }
            return override;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            boolean override = shouldOverrideUrlLoading(request.getUrl());
            if (override) {
                override = super.shouldOverrideUrlLoading(view, request);
            }
            return override;
        }

        /**
         * Give the host application a chance to take over the control when a new url is about to be loaded
         * @param uri   Uri to load
         * @return  <code>true</code> if the host application will handle the url, while <code>false</code> means the current WebView handles the url.
         */
        private boolean shouldOverrideUrlLoading(Uri uri) {
            boolean appWillHandle = true;
            if (isRedirectHost(uri)) {
                // This is my web site, so do not override; let my WebView load the page

                mDialog.stopLoading();
                mDialog.dismiss();

                RedditClient.getClient().processAuthorisation(LoginActivity.this, uri);

                appWillHandle = false;
            }
            return appWillHandle;
        }

        /**
         * Check if Uri matches host of redirect url
         * @param uri   Uri to check
         * @return  <code>true</code> if host matches
         */
        private boolean isRedirectHost(Uri uri) {
            return uri.getHost().equals(redirectHost);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    
    @Subscribe
    public void onMessageEvent(RedditClientEvent event) {

        if (PostOffice.deliverEventOrBroadcast(event, TAG)) {
            boolean handled = true;

            if (event.isLoginEvent()) {
                Intent intent = new Intent(this, PostListActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Utils.startActivity(this, intent);
            } else if (event.isAuthErrorEvent()) {
                Dialog.showAlertDialog(this, event.getErrorMessage());
            } else {
                handled = false;
            }

            PostOffice.logHandled(event, TAG, handled);
        }
    }
}
