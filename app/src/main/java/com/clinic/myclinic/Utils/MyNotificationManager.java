package com.clinic.myclinic.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.clinic.myclinic.R;

public class MyNotificationManager {
    private Context ctx;

    public static final int NOTIFICATION_ID = 2099;

    public MyNotificationManager(Context ctx) {
        this.ctx = ctx;
    }

    public void showNotification(String from, String notification, Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(
                 ctx
                ,NOTIFICATION_ID
                ,intent
                ,PendingIntent.FLAG_UPDATE_CURRENT
        );

        //TODO(programmer): fix deprecated NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        //Создаем уведомление с помощью builder. То, что я делаю - считается устаревшим(deprecated), при том с недавних пор, но, другой вариант я не знаю...
        Notification mNotification = builder.setSmallIcon(R.drawable.heart)
                                            .setAutoCancel(true)
                                            .setTicker("myClinic")
                                            .setContentIntent(pendingIntent)
                                            .setContentTitle(from)
                                            .setContentText(notification)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.heart))
                                            .setPriority(2)
                                            .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, mNotification);

    }
}
