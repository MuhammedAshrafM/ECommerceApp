package com.example.ecommerceapp.data;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecommerceapp.R;

import java.io.IOException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class MyNotification {

    private Context context;
    private static MyNotification INSTANCE;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    public MyNotification(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public synchronized static MyNotification getINSTANCE(Context context){
        if(INSTANCE == null) {
            INSTANCE = new MyNotification(context);
        }
        return INSTANCE;
    }

    public void notify(int idNotify, String title, String body, String largeIcon, String summaryText, PendingIntent pendingIntent){

        String notificationChannelId = "EcoNotify";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId,
                    "Eco Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Eco channel for app");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        builder = new NotificationCompat.Builder(context, notificationChannelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setColorized(true)
                .setColor(context.getResources().getColor(R.color.basicColor))
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_app_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setBigContentTitle(title)
                        .setSummaryText(summaryText));

        if(pendingIntent != null){
            builder.setContentIntent(pendingIntent);
        }

        if (largeIcon != null){
            try {
                URL url = new URL(largeIcon);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                builder.setLargeIcon(bitmap);
            } catch(IOException e) {
                Log.d(TAG, "Unique notify: " + e.toString());
            }

        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(context.getApplicationContext());
        manager.notify(idNotify, builder.build());
    }

    public void cancelNotification(int id){
        notificationManager.cancel(id);
    }
    public void cancelAllNotification(){
        notificationManager.cancelAll();
    }
}
