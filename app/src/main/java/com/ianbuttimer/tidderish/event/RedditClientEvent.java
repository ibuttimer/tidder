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

package com.ianbuttimer.tidderish.event;

import androidx.annotation.StringRes;

import com.ianbuttimer.tidderish.reddit.RedditClient;
import com.ianbuttimer.tidderish.utils.AppError;

import static com.ianbuttimer.tidderish.reddit.RedditClient.ClientStatus.AUTHORISATION_IN_PROGRESS;
import static com.ianbuttimer.tidderish.reddit.RedditClient.ClientStatus.AUTHORISED;
import static com.ianbuttimer.tidderish.reddit.RedditClient.ClientStatus.LOGOUT_IN_PROGRESS;
import static com.ianbuttimer.tidderish.reddit.RedditClient.ClientStatus.UNAUTHORISED;

/**
 * RedditClient status change event class
 */

public class RedditClientEvent extends AbstractEvent<RedditClientEvent> {

    private RedditClient.ClientStatus mOldStatus;
    private RedditClient.ClientStatus mNewStatus;

    private AppError.Codes mError;
    private int mHttpCode;
    private String mMessage;
    @StringRes private int mMessageRes;

    public RedditClientEvent(@EventType int event) {
        super(event);
    }

    public RedditClientEvent(@EventType int event, @EventMode int mode) {
        super(event, mode);
    }

    protected void init() {
        super.init();
        mError = null;
        mHttpCode = 0;
        mMessage = "";
        mMessageRes = 0;
    }

    public static RedditClientEvent newStatusEvent(RedditClient.ClientStatus oldStatus, RedditClient.ClientStatus newStatus) {
        return getBuilder(EventType.STATUS_CHANGE)
                .oldStatus(oldStatus).newStatus(newStatus).build();
    }

    public static RedditClientEvent newUserValidEvent() {
        return new RedditClientEvent(EventType.USER_VALID);
    }

    public static RedditClientEvent newAuthErrorEvent(AppError.Codes error, int httpCode, String message) {
        return getBuilder(EventType.AUTH_ERROR)
                .errorCode(error).httpCode(httpCode).message(message).build();
    }


    @Override
    protected RedditClientEvent getThis() {
        return this;
    }

    public boolean isUserValidEvent() {
        return isEvent(EventType.USER_VALID);
    }
    public boolean isStatusEvent() {
        return isEvent(EventType.STATUS_CHANGE);
    }

    public boolean isAuthErrorEvent() {
        return isEvent(EventType.AUTH_ERROR);
    }

    public boolean isCommsErrorEvent() {
        return isEvent(EventType.COMMS_ERROR);
    }

    public RedditClient.ClientStatus getOldStatus() {
        return mOldStatus;
    }

    public RedditClient.ClientStatus getNewStatus() {
        return mNewStatus;
    }

    public boolean isLoginEvent() {
        return (isStatusEvent() && (mNewStatus == AUTHORISED) && (mOldStatus == AUTHORISATION_IN_PROGRESS));
    }

    public boolean isLogoutEvent() {
        return (isStatusEvent() && (mNewStatus == UNAUTHORISED) && (mOldStatus == LOGOUT_IN_PROGRESS));
    }

    public AppError.Codes getError() {
        return mError;
    }

    public String getErrorMessage() {
        String msg;
        if (mError != null) {
            msg = AppError.getMessage(mError);
        } else {
            msg = "";
        }
        return msg;
    }

    @StringRes public int getMessageRes() {
        @StringRes int messageRes;
        if (mError != null) {
            messageRes = mMessageRes;
        } else {
            messageRes = 0;
        }
        return messageRes;
    }

    public int getHttpCode() {
        return mHttpCode;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    protected String toStringExtra() {
        StringBuilder sb = new StringBuilder();
        switch (getEvent()) {
            case EventType.STATUS_CHANGE:
                sb.append(mOldStatus).append(" -> ").append(mNewStatus);
                break;
            case EventType.AUTH_ERROR:
                sb.append(mError).append(" http ").append(mHttpCode);
                // fall through
            case EventType.COMMS_ERROR:
                sb.append(" ").append(mMessage).append(" ").append(mMessageRes);
                break;
        }
        return sb.toString();
    }

    public static Builder getBuilder(@EventType int type) {
        return new Builder(type);
    }


    public static class Builder {

        private final RedditClientEvent mEvent;

        public Builder(@EventType int type) {
            mEvent = new RedditClientEvent(type);
        }

        public Builder oldStatus(RedditClient.ClientStatus oldStatus) {
            mEvent.mOldStatus = oldStatus;
            return this;
        }

        public Builder newStatus(RedditClient.ClientStatus newStatus) {
            mEvent.mNewStatus = newStatus;
            return this;
        }

        public Builder errorCode(AppError.Codes error) {
            mEvent.mError = error;
            return this;
        }

        public Builder httpCode(int httpCode) {
            mEvent.mHttpCode = httpCode;
            return this;
        }

        public Builder message(String message) {
            mEvent.mMessage = message;
            return this;
        }

        public Builder messageRes(@StringRes int messageRes) {
            mEvent.mMessageRes = messageRes;
            return this;
        }

        public Builder clear() {
            mEvent.init();
            return this;
        }

        public RedditClientEvent build() {
            return mEvent;
        }
    }
}
