package com.ianbuttimer.tidderish.net;

import okhttp3.OkHttpClient;

public class NetInit implements INetInit {

    @Override
    public OkHttpClient.Builder cfgBuilder(OkHttpClient.Builder builder) {
        // no op
        return builder;
    }
}
