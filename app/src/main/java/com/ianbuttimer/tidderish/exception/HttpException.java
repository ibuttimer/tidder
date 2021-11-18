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

package com.ianbuttimer.tidderish.exception;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Response;

/**
 * A http exception class
 */
@SuppressWarnings("unused")
public class HttpException extends IOException {

    private int code;
    private String responseMessage;

    /**
     * Default constructor
     */
    public HttpException() {
    }

    /**
     * Constructor
     * @param message   The detail message
     */
    public HttpException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param message   The detail message
     * @param cause     The cause
     */
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     * @param cause     The cause
     */
    public HttpException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param message   The detail message
     * @param response  okhttp3 response to create exception from
     */
    public HttpException(String message, @NonNull Response response) {
        super(message);
        setCode(response.code());
        setResponseMessage(response.message());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * Check if this object represents an unauthorised http exception
     * @return <code>true</code> if this is an unauthorised http exception
     */
    public boolean isUnauthorised() {
        return isUnauthorised(code);
    }

    /**
     * Check if response code represents an forbidden http exception
     * @return <code>true</code> if it is an unauthorised http exception
     */
    public boolean isForbidden() {
        return isForbidden(code);
    }

    /**
     * Check if response code represents an unauthorised http exception
     * @return <code>true</code> if it is an unauthorised http exception
     */
    public static boolean isUnauthorised(int code) {
        return (code == HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    /**
     * Check if response code represents an forbidden http exception
     * @return <code>true</code> if it is an unauthorised http exception
     */
    public static boolean isForbidden(int code) {
        return (code == HttpURLConnection.HTTP_FORBIDDEN);
    }





}
