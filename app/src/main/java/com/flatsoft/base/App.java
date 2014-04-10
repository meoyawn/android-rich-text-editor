package com.flatsoft.base;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by adelnizamutdinov on 10/04/2014
 */
public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
