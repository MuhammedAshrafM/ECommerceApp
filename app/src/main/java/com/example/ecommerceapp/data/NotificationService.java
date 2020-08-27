package com.example.ecommerceapp.data;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.pojo.NotificationModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.example.ecommerceapp.ui.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkBuilder;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class NotificationService extends FirebaseMessagingService {

    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private boolean registered;
    private String body, title, largeIcon, summaryText, time, userId, dataExtra;
    private PendingIntent pendingIntent;
    private NotificationModel notificationModel;
    private EcoDatabase ecoDatabase;
    private String pattern;
    private DateFormat dateFormat;
    private Date date = new Date();

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

        pattern = String.format("%s '%s' %s","EEE, d MMM yyyy", getString(R.string.at), "HH:mm");
        dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        registered = Preferences.getINSTANCE(this, PREFERENCES_DATA_USER).isDataUserExist();
        ecoDatabase = EcoDatabase.getInstance(this);
        pendingIntent = null;

        if (remoteMessage.getData().size() > 0) {
            try {
                Log.d(TAG, "Unique remoteMessage: " + remoteMessage.getData());
                setNotification(remoteMessage);
            }catch (Exception e){
                Log.d(TAG, "Unique Exception: " + e.toString());
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
        dataExtra = data.get("dataExtra");

        date = Calendar.getInstance(Locale.getDefault()).getTime();
        time = dateFormat.format(date);

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

        if(!(title.equals("sendOrder") || title.equals("receivedOrder")) || registered){
            setNotification();
        }
    }

    private void setNotification(){
        Intent intent = new Intent();
        intent.setAction("NEW_NOTIFICATION");
        intent.putExtra("newNotification", true);
        sendBroadcast(intent);
        String s = String.valueOf(System.currentTimeMillis()).substring(5);
        int idNotify = Integer.parseInt(s);
        MyNotification.getINSTANCE(this).notify(idNotify, title, body, largeIcon, summaryText, pendingIntent);
    }
    private void register(boolean validated){
        title = getString(R.string.registrationSuccessfully);
        body = String.format(Locale.getDefault(),"%s %s\n%s",
                getString(R.string.welcome), body,
                getString(R.string.singUpSuccess));

        userId = dataExtra;

        if(validated){
            summaryText = getString(R.string.validateAccountSuccess);
        }else {
            summaryText = getString(R.string.user_account_not_validated_info);
        }

        notificationModel = new NotificationModel(title, body, summaryText, null, 0, null,
                time, false, userId);
        insertNotification(notificationModel);
    }

    private void sendOrder(){
        title = getString(R.string.order_done);
        String orderId = body;
        body = String.format(Locale.getDefault(),"%s\n%s",
                getString(R.string.order_completed),
                getString(R.string.orderPrepared));

        userId = dataExtra;

        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        if(registered) {
            pendingIntent = new NavDeepLinkBuilder(this)
                    .setComponentName(HomeActivity.class)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.orderDetailsFragment)
                    .setArguments(bundle)
                    .createPendingIntent();
        }
        notificationModel = new NotificationModel(title, body, null, null, R.string.deepLinkOrderDetails,
                orderId, time, false, userId);
        insertNotification(notificationModel);
    }
    private void addProduct(Map<String, String> data){
        String id, title, sellerId,
                subCategoryId;
        Double price;
        Float offer;
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern("#,###,###,###.##");
        id = data.get("id");
        title = data.get("titleProduct");
        price = Double.valueOf(data.get("price"));
        offer = Float.valueOf(data.get("offer"));
        largeIcon = data.get("imagePath");
        sellerId = data.get("sellerId");
        subCategoryId = data.get("subCategoryId");

        ProductModel product = new ProductModel();
        product.setId(id);
        product.setTitle(title);
        product.setSellerId(sellerId);
        product.setSubCategoryId(subCategoryId);
        this.title = getString(R.string.newProduct);
        body = String.format(Locale.getDefault(),"%s\n%s:%s\n%s:%.0f%s",
                title,
                getString(R.string.price),
                decimalFormat.format(price) + " " + getString(R.string.egp),
                getString(R.string.offer),
                offer,
                getString(R.string.off_percent));
        summaryText = getString(R.string.newProductDetails);

        Bundle bundle = new Bundle();
        bundle.putParcelable("product", product);
        if(registered) {
            pendingIntent = new NavDeepLinkBuilder(this)
                    .setComponentName(HomeActivity.class)
                    .setGraph(R.navigation.mobile_navigation)
                    .setDestination(R.id.productFragment)
                    .setArguments(bundle)
                    .createPendingIntent();
            userId = Preferences.getINSTANCE(this, PREFERENCES_DATA_USER).getDataUser().getId();
        }else {
            userId = "userLogOuted";
        }

        Gson gson = new Gson();
        String json = gson.toJson(product);

        notificationModel = new NotificationModel(title, body, summaryText, largeIcon, R.string.deepLinkProduct, json,
                time, false, userId);
        insertNotification(notificationModel);
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

    private void insertNotification(NotificationModel notificationModel){

        ecoDatabase.dao().insertNotification(notificationModel)
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Unique onComplete: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Unique onError: " + e.getMessage());
                    }
                });

    }
}