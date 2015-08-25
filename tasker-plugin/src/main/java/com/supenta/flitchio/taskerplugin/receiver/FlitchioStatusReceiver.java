package com.supenta.flitchio.taskerplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.supenta.flitchio.taskerplugin.services.FlitchioBindingService;
import com.supenta.flitchio.taskerplugin.utils.ServiceUtils;

import timber.log.Timber;

/**
 * Receiver for listening changes to the status of Flitchio
 */
public class FlitchioStatusReceiver extends BroadcastReceiver {

    public static final String ACTION_FLITCHIO_CONNECTED =
            "com.supenta.flitchio.manager.communication.ACTION_FLITCHIO_CONNECTED";

    public static final String ACTION_FLITCHIO_DISCONNECTED =
            "com.supenta.flitchio.manager.communication.ACTION_FLITCHIO_DISCONNECTED";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        Timber.d("Received with action: " + action);

        switch (action) {
            case ACTION_FLITCHIO_CONNECTED:
                Timber.i("Flitchio has connected");

                if (!ServiceUtils.isServiceRunning(context, FlitchioBindingService.class)) {
                    context.startService(new Intent(context, FlitchioBindingService.class));
                }
                break;
            case ACTION_FLITCHIO_DISCONNECTED:
                Timber.i("Flitchio has disconnected");

                context.sendBroadcast(new Intent(FlitchioBindingService.ACTION_STOP_SERVICE));
                break;
            default:
                Timber.wtf("Invalid intent: " + intent);
                break;
        }
    }
}
