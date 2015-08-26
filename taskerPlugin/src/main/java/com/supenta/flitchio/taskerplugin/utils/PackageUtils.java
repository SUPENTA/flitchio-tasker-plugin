package com.supenta.flitchio.taskerplugin.utils;

import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class PackageUtils {

    public static void launchFromPackageName(final Context context, final String packageName) {
        final Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        if (launchIntent == null) {
            Timber.w("Package was not found: %s", packageName);
        } else {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Timber.i("Launching: " + launchIntent);

            context.startActivity(launchIntent);
        }
    }
}
