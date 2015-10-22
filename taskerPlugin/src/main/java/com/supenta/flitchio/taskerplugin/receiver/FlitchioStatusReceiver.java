package com.supenta.flitchio.taskerplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.supenta.flitchio.sdk.Status;
import com.supenta.flitchio.taskerplugin.fragments.SettingsFragment;
import com.supenta.flitchio.taskerplugin.services.FlitchioBindingService;
import com.supenta.flitchio.taskerplugin.utils.ServiceUtils;

import timber.log.Timber;

/**
 * Receiver for listening changes to the status of Flitchio
 */
public class FlitchioStatusReceiver extends BroadcastReceiver {
    private static final String FLITCHIO_MANAGER_PACKAGE = "com.supenta.flitchio.manager";

    /**
     * Broadcast notifying that Flitchio has connected or disconnected.
     * Always contains {@link #EXTRA_STATUS}.
     * KEEP IT SYNCED WITH THE VALUE IN FLITCHIO MANAGER.
     */
    static final String ACTION_FLITCHIO_STATUS_CHANGED =
            FLITCHIO_MANAGER_PACKAGE + ".ACTION_FLITCHIO_STATUS_CHANGED";

    /**
     * Current status of Flitchio passed with a {@link #ACTION_FLITCHIO_STATUS_CHANGED} broadcast.
     * The two possible values are {@link Status#CONNECTED} and {@link Status#DISCONNECTED}.
     * KEEP IT SYNCED WITH THE VALUE IN FLITCHIO MANAGER.
     */
    static final String EXTRA_STATUS =
            FLITCHIO_MANAGER_PACKAGE + ".EXTRA_STATUS";

    /**
     * Thanks to the permission passed when you register the receiver, this receiver will only
     * receive status from brodcasters who hold this permission (normally, only Flitchio Manager
     * should have it).
     * KEEP IT SYNCED WITH THE VALUE IN FLITCHIO MANAGER.
     */
    public static final String PERMISSION_BROADCAST_STATUS =
            FLITCHIO_MANAGER_PACKAGE + ".PERMISSION_BROADCAST_STATUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.v("onReceive: %s", intent);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(SettingsFragment.PREF_TOGGLE_WITH_FLITCHIO, true)) {
            Timber.i("Toggling with Flitchio is disabled");

            return;
        }

        final int statusCode = intent.getIntExtra(EXTRA_STATUS, Status.UNKNOWN);

        switch (statusCode) {
            case Status.CONNECTED:
                Timber.i("Flitchio has connected");

                if (!ServiceUtils.isServiceRunning(context, FlitchioBindingService.class)) {
                    context.startService(new Intent(context, FlitchioBindingService.class));
                }
                break;
            case Status.DISCONNECTED:
                Timber.i("Flitchio has disconnected");

                context.sendBroadcast(new Intent(FlitchioBindingService.ACTION_STOP_SERVICE));
                break;
            default:
                Timber.wtf("Invalid intent: " + intent);
        }
    }
}
