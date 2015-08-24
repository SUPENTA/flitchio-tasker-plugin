package com.supenta.flitchio.taskerplugin;

import android.app.Application;

import timber.log.Timber;

public class FlitchioTaskerPluginApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO plant a crash reporting Tree
        }
    }
}
