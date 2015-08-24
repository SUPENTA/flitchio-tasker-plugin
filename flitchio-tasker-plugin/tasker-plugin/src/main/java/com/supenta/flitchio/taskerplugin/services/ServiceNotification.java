package com.supenta.flitchio.taskerplugin.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.supenta.flitchio.taskerplugin.R;

/**
 * A notification for keeping alive the {@link FlitchioBindingService}.
 */
class ServiceNotification {
    private static final int ID_NOTIFICATION = 4334;
    private static final int ID_INTENT = 8080;
    private final Service service;
    private final NotificationManagerCompat manager;
    private final NotificationCompat.Builder builder;

    ServiceNotification(final Service service) {
        this.service = service;
        this.manager = NotificationManagerCompat.from(service);

        this.builder = new NotificationCompat.Builder(service);
        this.builder.setContentTitle(service.getString(R.string.notification_flitchio_title));
        this.builder.setOngoing(true);

        Intent disableServiceIntent = new Intent(FlitchioBindingService.ACTION_STOP_SERVICE);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(
                service,
                ID_INTENT,
                disableServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        this.builder.addAction(
                R.drawable.ic_notif_power_grey600,
                service.getString(R.string.notification_action_disconnect),
                pendingIntentYes
        );
    }

    void showConnectedNotification() {
        builder.setSmallIcon(R.drawable.ic_notif_connected_white)
                .setContentText(service.getString(R.string.notification_flitchio_connected));

        manager.notify(ID_NOTIFICATION, builder.build());
    }

    void showDisconnectedNotification() {
        builder.setSmallIcon(R.drawable.ic_notif_disconnected_white)
                .setContentText(service.getString(R.string.notification_flitchio_disconnected));

        manager.notify(ID_NOTIFICATION, builder.build());
    }

    void stop() {
        manager.cancelAll();
    }
}