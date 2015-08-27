package com.supenta.flitchio.taskerplugin;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class FlitchioTaskerPluginApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsTree());
        }
    }

    private class CrashlyticsTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return;
            }

            Crashlytics.log(message);
            if (t != null) {
                Crashlytics.logException(t);
            }
        }
    }
}
