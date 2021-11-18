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

package com.ianbuttimer.tidderish.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;

import com.ianbuttimer.tidderish.reddit.Request;
import com.ianbuttimer.tidderish.reddit.post.PostRequest;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Class providing basic handling of content provider responses.<br>
 * The class <code>T</code> specifies the type of object to be processed by the application.<br>
 * <br>
 * Requests may be initiated in a number of ways
 * @see AsyncCallback
 */

public abstract class AbstractContentProviderCallback<T> extends AsyncCallback<T>
        implements ICallback<T>, LoaderManager.LoaderCallbacks<AbstractResultWrapper> {


    //////// okhttp3.Callback portion of ICallback implementation ////////

    /**
     * Called when the HTTP request could not be executed due to cancellation, a connectivity problem or timeout.
     * @param call  Original request that has been prepared for execution
     * @param e     Failure reason
     * @see <a href="https://square.github.io/okhttp/3.x/okhttp/okhttp3/Callback.html">okhttp3.Callback</a>
     */
    @Override
    public void onFailure(@Nullable Call call, @Nullable IOException e) {
        throw new UnsupportedOperationException("http method");
    }

    /**
     * Called when the HTTP response was successfully returned by the remote server.
     * @param call      Original request that has been prepared for execution
     * @param response  HTTP response
     * @throws IOException if response was unsuccessful
     * @see <a href="https://square.github.io/okhttp/3.x/okhttp/okhttp3/Callback.html">okhttp3.Callback</a>
     */
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        //  must close the response body to prevent resource leaks
        response.close();
        throw new UnsupportedOperationException("http method");
    }

    //////// ICallback implementation ////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestGet(@NonNull URL url) {
        throw new UnsupportedOperationException("http method");
    }

    @Override
    public void requestGetService(@NonNull Request request) {
        throw new UnsupportedOperationException("http method");
    }

    @Override
    public void requestPostService(@NonNull PostRequest request) {
        throw new UnsupportedOperationException("http method");
    }

    @Override
    public T processUrlResponse(@NonNull URL request, @NonNull Response response) {
        //  must close the response body to prevent resource leaks
        response.close();
        throw new UnsupportedOperationException("http method");
    }
}
