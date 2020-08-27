package com.example.ecommerceapp.data;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = this;
    }

    public static synchronized MyApplication getInstance(){
        return myApplication;
    }

    public void setConnectivityReceiveListener(ConnectivityReceiver.ConnectivityReceiveListener listener){
        ConnectivityReceiver.connectivityReceiveListener = listener;
    }
    public void setNotificationReceiveListener(NotificationListener listener){
        NotificationReceiver.notificationListener = listener;
    }
}
