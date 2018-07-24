package com.ianbuttimer.tidder.net;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class NetInit implements INetInit {

    @Override
    public OkHttpClient.Builder cfgBuilder(OkHttpClient.Builder builder) {
        builder.addNetworkInterceptor(new StethoInterceptor());
        return builder;
    }
}
