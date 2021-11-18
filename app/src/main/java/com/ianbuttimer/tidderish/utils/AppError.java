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

package com.ianbuttimer.tidderish.utils;

import android.content.Context;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.TidderApplication;

/**
 * Application error class
 */

public class AppError {

    public enum Codes {
        UNAUTHORISED,
        ACCESS_DENIED,
        INVALID_AUTH_REQUEST,
        INVALID_AUTH_RESPONSE,
        UNKNOWN_AUTH_ERROR,

        TOKEN_RETRIEVE_ERROR,
        LOGOUT_ERROR,

        USER_INFO_ERROR,
        SUBREDDIT_QUERY_ERROR,

        COMMS_ERROR
    }

    private static final String[] MESSAGES;

    static {
        Context context = TidderApplication.getWeakApplicationContext().get();
        MESSAGES = context.getResources().getStringArray(R.array.app_error_message);
    }

    public static String getMessage(Codes code) {
        String message = "";
        if (code != null) {
            int ordinal = code.ordinal();
            if (ordinal < MESSAGES.length) {
                message = MESSAGES[ordinal];
            }
        }
        return message;
    }

}
