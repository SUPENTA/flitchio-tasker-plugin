package com.supenta.flitchio.taskerplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.supenta.flitchio.taskerplugin.services.FlitchioBindingService;

import timber.log.Timber;

public abstract class ServiceStatusReceiverTemplate extends BroadcastReceiver {

    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(FlitchioBindingService.ACTION_SERVICE_STARTED);
        filter.addAction(FlitchioBindingService.ACTION_SERVICE_STOPPED);

        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.v("onReceive: %s", intent);

        String action = intent.getAction();

        if (action.equals(FlitchioBindingService.ACTION_SERVICE_STARTED)) {
            onServiceStarted();
        } else if (action.equals(FlitchioBindingService.ACTION_SERVICE_STOPPED)) {
            onServiceStopped();
        }
    }

    protected abstract void onServiceStarted();

    protected abstract void onServiceStopped();
}
