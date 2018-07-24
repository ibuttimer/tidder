package com.ianbuttimer.tidder;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class AppInit implements IAppInit {

    @Override
    public void onCreate(Application app) {
        Stetho.initializeWithDefaults(app);
    }
}
