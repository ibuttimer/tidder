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

package com.ianbuttimer.tidder.data;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.event.EventType;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.utils.Dialog;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.Date;


/**
 * Abstract class to handle AsyncCallback responses<br>
 * The class <code>T</code> specifies the type of object to be processed by the application.<br>
 */
@SuppressWarnings("unused")
public abstract class ResponseHandler<T> implements Runnable {

    private T mResponse;
    @StringRes private int mErrorId;
    private String mErrorMsg;
    private Date cacheDate;
    private WeakReference<Activity> activity;   // weak ref so won't prevent activity being garbage collected

    /**
     * Constructor
     * @param activity  The current activity
     * @param response  Response object
     */
    public ResponseHandler(Activity activity, T response) {
        this(activity, response, 0, null);
    }

    /**
     * Constructor
     * @param activity  The current activity
     * @param response  Response object
     * @param errorId   Resource id of error message
     */
    public ResponseHandler(Activity activity, T response, @StringRes int errorId, String errorMsg) {
        this(activity, response, errorId, errorMsg, null);
    }

    /**
     * Constructor
     * @param activity  The current activity
     * @param response  Response object
     * @param errorId   Resource id of error message
     * @param cacheDate Date mResponse was cached
     */
    public ResponseHandler(Activity activity, T response, @StringRes int errorId, String errorMsg, Date cacheDate) {
        this.activity = new WeakReference<>(activity);
        this.mResponse = response;
        if (response == null) {
            this.mErrorId = R.string.no_response;
        } else {
            this.mErrorId = errorId;
        }
        this.mErrorMsg = errorMsg;
//        if (cacheDate == null) {
//            this.cacheDate = INVALID_DATE;
//        } else {
            this.cacheDate = cacheDate;
//        }
    }

    @Override
    public void run() {
        if (hasDialog()) {
            RedditClientEvent.Builder builder =
                    RedditClientEvent.getBuilder(EventType.COMMS_ERROR);

            // display error, string takes precedence over resource
            if (!TextUtils.isEmpty(mErrorMsg)) {
                Dialog.showAlertDialog(activity.get(), mErrorMsg);
                builder.message(mErrorMsg);
            } else {
                Dialog.showAlertDialog(activity.get(), mErrorId);
                builder.messageRes(mErrorId);
            }
            EventBus.getDefault().post(builder.build());
        }
    }

    /**
     * Check if this object has a dialog to display
     * @return  <code>true</code> if has dialog, <code>false</code> otherwise
     */
    protected boolean hasDialog() {
        return ((mErrorId != 0) || !TextUtils.isEmpty(mErrorMsg));
    }

    public T getResponse() {
        return mResponse;
    }

    public void setResponse(T response) {
        this.mResponse = response;
    }

    @StringRes protected int getErrorId() {
        return mErrorId;
    }

    public void setErrorId(@StringRes int errorId) {
        this.mErrorId = errorId;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.mErrorMsg = errorMsg;
    }

    public Date getCacheDate() {
        return cacheDate;
    }

    public void setCacheDate(Date cacheDate) {
        this.cacheDate = cacheDate;
    }
}