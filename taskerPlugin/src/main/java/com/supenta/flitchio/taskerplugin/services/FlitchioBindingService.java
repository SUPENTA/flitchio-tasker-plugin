package com.supenta.flitchio.taskerplugin.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.supenta.flitchio.sdk.ButtonEvent;
import com.supenta.flitchio.sdk.FlitchioController;
import com.supenta.flitchio.sdk.FlitchioListener;
import com.supenta.flitchio.sdk.FlitchioManagerDependencyException;
import com.supenta.flitchio.sdk.InputElement;
import com.supenta.flitchio.sdk.JoystickEvent;
import com.supenta.flitchio.taskerplugin.activities.EditActivity;
import com.supenta.flitchio.taskerplugin.utils.TaskerPlugin;

import timber.log.Timber;

/**
 * This Service is used in order to bind to the Flitchio SDK since we want constant binding to
 * execute Tasker tasks. When a Flitchio Button is triggered, we send that over to Tasker and "ask"
 * it to make a query using {@link com.supenta.flitchio.taskerplugin.receiver.QueryReceiver}.
 * <p/>
 * We use a {@link ServiceNotification} in order to keep the Service from being killed by the system.
 */
public class FlitchioBindingService extends Service implements FlitchioListener {

    public static final String ACTION_SERVICE_STARTED
            = "com.supenta.flitchio.taskerplugin.ACTION_SERVICE_STARTED";
    public static final String ACTION_SERVICE_STOPPED
            = "com.supenta.flitchio.taskerplugin.ACTION_SERVICE_STOPPED";

    public static final String ACTION_STOP_SERVICE
            = "com.supenta.flitchio.taskerplugin.ACTION_STOP_SERVICE";

    /**
     * Intent used for the request of the query on Tasker.
     */
    private static final Intent INTENT_REQUEST_REQUERY =
            new Intent(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY)
                    .putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY, EditActivity.class.getName());

    private DisconnectReceiver disconnectReceiver;
    private ServiceNotification notification;
    private FlitchioController flitchio;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.v("onCreate");

        disconnectReceiver = new DisconnectReceiver();
        notification = new ServiceNotification(this);
        flitchio = FlitchioController.getInstance(this);
        try {
            Timber.v("Creating Flitchio");

            flitchio.onCreate();
        } catch (FlitchioManagerDependencyException e) {
            Timber.e(e, "Flitchio could not be created");
        }

        sendBroadcast(new Intent(ACTION_SERVICE_STARTED));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.v("onStartCommand: " + intent);

        registerReceiver(disconnectReceiver, disconnectReceiver.getIntentFilter());

        notification.showDisconnectedNotification();
        flitchio.onResume(this);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.v("onDestroy");

        unregisterReceiver(disconnectReceiver);

        notification.stop();

        flitchio.onPause();
        flitchio.onDestroy();

        sendBroadcast(new Intent(ACTION_SERVICE_STOPPED));

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Timber.v("onBind: %s", intent);

        return null;
    }

    @Override
    public void onFlitchioButtonEvent(InputElement.Button button, ButtonEvent buttonEvent) {
        Timber.v("onFlitchioButtonEvent: %s", button.name);

        sendToTasker(button.name);
    }

    /**
     * Sending the Button's info to Tasker is accomplished with a bundle, we only need the button's
     * id in order to verify if it is the button that is set in {@link EditActivity} so we wrap that
     * in a bundle, add the data to Tasker and then signal Tasker that we want it to query us.
     *
     * @param buttonId The button id to send.
     */
    private void sendToTasker(final String buttonId) {
        Bundle bundle = new Bundle();
        bundle.putString(EditActivity.TASKER_BUNDLE_KEY_FLITCHIO_SELECTED_BUTTON, buttonId);
        TaskerPlugin.Event.addPassThroughData(INTENT_REQUEST_REQUERY, bundle);
        sendBroadcast(INTENT_REQUEST_REQUERY);
    }

    @Override
    public void onFlitchioJoystickEvent(InputElement.Joystick joystick, JoystickEvent joystickEvent) {

    }

    @Override
    public void onFlitchioStatusChanged(boolean isConnected) {
        Timber.i("Flitchio is now " + (isConnected ? "connected" : "disconnected"));

        if (isConnected) {
            notification.showConnectedNotification();
        } else {
            notification.showDisconnectedNotification();
        }
    }

    private class DisconnectReceiver extends BroadcastReceiver {

        IntentFilter getIntentFilter() {
            return new IntentFilter(ACTION_STOP_SERVICE);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.v("onReceive: " + intent);

            stopSelf();
        }
    }
}
