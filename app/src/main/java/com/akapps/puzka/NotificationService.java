package com.akapps.puzka;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends Service {
    public void startNotificationListener() {
        new Thread(this::ShowNotification).start();
    }

    @Override
    public void onCreate()
    {
        startNotificationListener();
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void ShowNotification()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_bar);
        notificationLayout.setTextViewCompoundDrawables(R.id.textView142, R.drawable.ic_iconpuzka_, 0,0,0);
        notificationLayout.setTextViewCompoundDrawables(R.id.textView144, R.drawable.ic_lifestyle, 0,0,0);
        notificationLayout.setTextViewCompoundDrawables(R.id.textView145, R.drawable.ic_transaction_noti, 0,0,0);
        Notification customNotification = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(23, customNotification);

    }


}
