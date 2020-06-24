package com.example.ecommerceapp.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiveListener connectivityReceiveListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo!=null && networkInfo.isConnectedOrConnecting();

        if(connectivityReceiveListener != null){
            connectivityReceiveListener.onNetworkConnectionChanged(isConnected);
        }
    }

    // create method to check manually like click on button
    public static boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication
                .getInstance()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo!=null && networkInfo.isConnectedOrConnecting();

        return isConnected;
    }

    public interface ConnectivityReceiveListener{
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
