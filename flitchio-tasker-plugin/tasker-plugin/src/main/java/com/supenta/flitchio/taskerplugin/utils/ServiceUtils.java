package com.supenta.flitchio.taskerplugin.utils;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceUtils {

    /**
     * By looking through the system's running services we can determine if a specific service is
     * running or not.
     *
     * @param context
     * @param serviceClass
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        final String serviceName = serviceClass.getName();
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            final String runningServiceName = service.service.getClassName();
            if (serviceName.equals(runningServiceName)) {
                return true;
            }
        }

        return false;
    }
}
