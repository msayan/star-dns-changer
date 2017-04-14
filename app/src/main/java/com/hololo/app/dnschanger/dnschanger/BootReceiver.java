package com.hololo.app.dnschanger.dnschanger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.hololo.app.dnschanger.R;


public class BootReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean autoStart = preferences.getBoolean("startBoot", true);
            boolean isStarted = preferences.getBoolean("isStarted", false);
            String dnsModelJSON = preferences.getString("dnsModel", "");

            if (autoStart && isStarted && !dnsModelJSON.isEmpty()) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                sendNotification(context, dnsModelJSON);
            }
        }
    }

    private void sendNotification(Context context, String dnsModel) {
        Intent intentAction = new Intent(context, MainActivity.class);

        intentAction.putExtra("dnsModel", dnsModel);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intentAction, PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.dns_changer_ico_inverse)
                .setContentTitle(context.getString(R.string.service_ready))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_vpn_key_black_24dp, context.getString(R.string.turn_on), pendingIntent)
                .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        notificationManager.notify(1903, notification);

    }
}
