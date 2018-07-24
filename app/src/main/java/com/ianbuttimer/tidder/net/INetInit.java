package com.ianbuttimer.tidder.net;

import okhttp3.OkHttpClient;

/**
 * Variant specific network client initialisation interface
 */
public interface INetInit {

    /**
     * Configure builder
     * @param builder   Builder to configure
     * @return  Builder to enable chaining
     */
    OkHttpClient.Builder cfgBuilder(OkHttpClient.Builder builder);
}
