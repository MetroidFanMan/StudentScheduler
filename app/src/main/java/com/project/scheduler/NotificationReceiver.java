package com.project.scheduler;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    static int notifyId;
    String channel_1 = "channel1";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context, channel_1);

        Notification notification = new NotificationCompat.Builder(context, channel_1)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Student Scheduler")
                .setContentText(intent.getStringExtra("title") + intent.getStringExtra("ending")).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyId++, notification);
    }

    private void createNotificationChannel(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel_1 = new NotificationChannel(channelId, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel_1.setDescription("This is channel 1");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel_1);
        }
    }
}
