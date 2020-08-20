package com.example.ecommerceapp.data;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.example.ecommerceapp.ui.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import androidx.navigation.NavDeepLinkBuilder;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class NotificationService extends FirebaseMessagingService {

    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private boolean registered;
    private String body, title, summaryText;
    private PendingIntent pendingIntent = null;

    @Override
    public void onNewToken(String token) {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "MOCA From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        registered = Preferences.getINSTANCE(this, PREFERENCES_DATA_USER).isDataUserExist();

        if (remoteMessage.getData().size() > 0) {
            try {
                setNotification(remoteMessage);
            }catch (Exception e){
                Log.d(TAG, "MOKA onMessageReceived: " + e.toString());
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void setNotification(RemoteMessage remoteMessage){
        Map<String, String> data = remoteMessage.getData();
        title = data.get("title");
        body = data.get("body");

        String s = String.valueOf(System.currentTimeMillis()).substring(5);
        int idNotify = Integer.parseInt(s);

        switch (title){
            case "createAccount":
                register(false);
                break;

            case "signUp":
                register(true);
                break;

            case "sendOrder":
                sendOrder();
                break;

            case "addProduct":
                addProduct(data);
                break;

            case "editOfferProduct":
                editOfferProduct();
                break;

            default:
                break;
        }

        MyNotification.getINSTANCE(this).notify(idNotify, title, body, "ticker", summaryText, pendingIntent);
    }

    private void register(boolean validated){
        title = getString(R.string.registrationSuccessfully);
        body = String.format(Locale.getDefault(),"%s %s\n%s",
                getString(R.string.welcome), body,
                getString(R.string.singUpSuccess));
        if(validated){
            summaryText = getString(R.string.validateAccountSuccess);
        }else {
            summaryText = getString(R.string.user_account_not_validated_info);
        }
    }

    private void sendOrder(){
        title = getString(R.string.order_done);
        String orderId = body;
        body = String.format(Locale.getDefault(),"%s\n%s",
                getString(R.string.order_completed),
                getString(R.string.orderPrepared));

        if(registered) {
            Bundle bundle = new Bundle();
            bundle.putString("orderId", orderId);
            pendingIntent = new NavDeepLinkBuilder(this)
                    .setComponentName(HomeActivity.class)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.orderDetailsFragment)
                    .setArguments(bundle)
                    .createPendingIntent();
        }
    }
    private void addProduct(Map<String, String> data){
        String id, title, imagePath, descriptionPath, specificationPath, brandId, sellerId,
                subCategoryId;
        Double price;
        Float offer;
        int quantity, shippingFee, available;
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern("#,###,###,###.##");
        id = data.get("id");
        title = data.get("title");
        price = Double.valueOf(data.get("price"));
        offer = Float.valueOf(data.get("offer"));
        imagePath = data.get("imagePath");
        descriptionPath = data.get("descriptionPath");
        specificationPath = data.get("specificationPath");
        quantity = Integer.parseInt(data.get("quantity"));
        brandId = data.get("brandId");
        sellerId = data.get("sellerId");
        subCategoryId = data.get("subCategoryId");
        shippingFee = Integer.parseInt(data.get("shippingFee"));
        available = Integer.parseInt(data.get("available"));

        ProductModel product = new ProductModel(id, title, price, offer, imagePath, descriptionPath, specificationPath,
                quantity, brandId, sellerId, subCategoryId, shippingFee, available, 0, 0.0f, null);

        this.title = getString(R.string.newProduct);
        body = String.format(Locale.getDefault(),"%s\n%s:%s\n%s:%.0f%s",
                title,
                getString(R.string.price),
                decimalFormat.format(price) + " " + getString(R.string.egp),
                getString(R.string.offer),
                offer,
                getString(R.string.off_percent));
        summaryText = getString(R.string.newProductDetails);

        if(registered) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", product);
            pendingIntent = new NavDeepLinkBuilder(this)
                    .setComponentName(HomeActivity.class)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.productFragment)
                    .setArguments(bundle)
                    .createPendingIntent();
        }
    }
    private void editOfferProduct(){
//        title = getString(R.string.order_done);
//        String orderId = body;
//        body = String.format(Locale.getDefault(),"%s\n%s",
//                getString(R.string.order_completed),
//                getString(R.string.orderPrepared));
//
//        if(registered) {
//            Bundle bundle = new Bundle();
//            bundle.putString("orderId", orderId);
//            pendingIntent = new NavDeepLinkBuilder(this)
//                    .setComponentName(HomeActivity.class)
//                    .setGraph(R.navigation.mobile_navigation)
//                    .setDestination(R.id.orderDetailsFragment)
//                    .setArguments(bundle)
//                    .createPendingIntent();
//        }
    }
}
