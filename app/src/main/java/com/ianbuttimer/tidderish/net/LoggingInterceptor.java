package com.ianbuttimer.tidderish.net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import timber.log.Timber;

/**
 * Logging interceptor based on <a href="https://github.com/square/okhttp/wiki/Interceptors">Interceptors</a>
 */
public class LoggingInterceptor implements Interceptor {

    private static final double NANOSEC_PER_MSEC = 1e6d;

    private boolean mLogging;

    public LoggingInterceptor(boolean log) {
        this.mLogging = log;
    }

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = 0;
        if (mLogging) {
            t1 = System.nanoTime();
            Timber.i("Sending %s request %s on %s%n%s%n%s",
                    request.method(), request.url(), chain.connection(), request.headers(),
                    stringifyRequestBody(request));
        }

        Response response = chain.proceed(request);

        if (mLogging) {
            long t2 = System.nanoTime();
            Timber.i("Received response for %s in %.1fms%n%s",
                    response.request().url(), ((t2 - t1) / NANOSEC_PER_MSEC), response.headers());

            // response body is logged in ClientService as body is a one-shot stream!
        }
        return response;
    }

    public void setLogging(boolean logging) {
        this.mLogging = logging;
    }

    private static String stringifyRequestBody(Request request) {
        String bodyStr = "";
        try {
            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                bodyStr = buffer.readUtf8();
            }
        } catch (IOException e) {
            Timber.e(e);
        }
        return bodyStr;
    }
}
