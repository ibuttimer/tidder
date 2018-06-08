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

package com.ianbuttimer.tidder;

import android.content.Context;
import android.content.IntentFilter;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.ianbuttimer.tidder.net.NetworkStatusReceiver;
import com.ianbuttimer.tidder.reddit.Api;
import com.ianbuttimer.tidder.utils.DebugTree;

import java.lang.ref.WeakReference;

import timber.log.Timber;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Application class
 */

public class TidderApplication extends MultiDexApplication {

    private static WeakReference<Context> mAppContext;

    private static String mConfigErrorMsg;

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        mAppContext = new WeakReference<>(context);

        String mode;
        int logLevel;
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            mode = "debug";
            logLevel = Log.DEBUG;
        } else {
            mode = "release";
            logLevel = Log.INFO;
        }

        Timber.plant(new DebugTree(logLevel));
        Timber.i("Application launched in " + mode + " mode");

        // register broadcast receivers
        context.registerReceiver(new NetworkStatusReceiver(), new IntentFilter(CONNECTIVITY_ACTION));


        // check the app has been configured correctly
        mConfigErrorMsg = Api.isConfigValid(context);
    }

    /**
     * Provide a weak reference to the application context for use by non-context classes<br>
     * @return  weak context reference
     */
    public static WeakReference<Context> getWeakApplicationContext() {
        return mAppContext;
    }

    /**
     * Check if the application configuration is valid
     * @return  <code>true</code> if the application configuration is valid
     */
    public static boolean isConfigValid() {
        return TextUtils.isEmpty(mConfigErrorMsg);
    }

    /**
     * Get the config error message
     * @return  error message
     */
    public static String getConfigErrorMsg() {
        return mConfigErrorMsg;
    }

}
