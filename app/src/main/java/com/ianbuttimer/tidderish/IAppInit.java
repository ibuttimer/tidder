package com.ianbuttimer.tidderish;

import android.app.Application;

/**
 * Variant specific application initialisation interface
 */
public interface IAppInit {

    /**
     * Configure application
     * @param app   Application
     */
    void onCreate(Application app);
}
