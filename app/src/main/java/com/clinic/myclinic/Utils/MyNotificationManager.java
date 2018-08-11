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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        //Создаем уведомление с помощью builder. То что я делаю - считается устаревшим, при том с недавних пор, но, другой вариант я не знаю...
        Notification mNotification = builder.setSmallIcon(R.drawable.heart)
                                            .setAutoCancel(true)
                                            .setContentIntent(pendingIntent)
                                            .setContentTitle(from)
                                            .setContentText(notification)
                //TODO: не работает звук уведомления.
                                            .setDefaults(Notification.DEFAULT_SOUND)
                                            .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.heart))
                                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                                            .setLights(Color.RED, 3000, 3000)
                                            .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, mNotification);

    }
}
