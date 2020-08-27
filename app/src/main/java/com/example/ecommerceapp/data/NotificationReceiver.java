package com.example.ecommerceapp.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NotificationReceiver extends BroadcastReceiver {

    public static NotificationListener notificationListener;

    public NotificationReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean notify = intent.getBooleanExtra("newNotification", false);
        if(notify){
            if(notificationListener != null){
                notificationListener.onNotifyChange();
            }
        }
    }
}
