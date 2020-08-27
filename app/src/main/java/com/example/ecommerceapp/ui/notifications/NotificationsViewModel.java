package com.example.ecommerceapp.ui.notifications;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.EcoDatabase;
import com.example.ecommerceapp.pojo.NotificationModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.login.widget.ProfilePictureView.TAG;


public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<List<NotificationModel>> mutableLiveNotifications;
    private MutableLiveData<Boolean> mutableLiveNotificationsUpdated;
    private MutableLiveData<Boolean> mutableLiveNotificationsDeleted;
    private MutableLiveData<String> mutableLiveErrorMessage;
    private EcoDatabase ecoDatabase;

    public NotificationsViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveNotificationsUpdated = new MutableLiveData<>();
        mutableLiveNotificationsDeleted = new MutableLiveData<>();
        mutableLiveNotifications = new MutableLiveData<>();
    }

    public void getNotifications(Context context, String userId){
        ecoDatabase = EcoDatabase.getInstance(context);
        ecoDatabase.dao().getNotifications(userId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<NotificationModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<NotificationModel> notificationModels) {
                        mutableLiveNotifications.setValue(notificationModels);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mutableLiveErrorMessage.setValue(e.getMessage());
                    }
                });
    }

    public void getNewNotifications(Context context, String userId){
        ecoDatabase = EcoDatabase.getInstance(context);
        ecoDatabase.dao().getNewNotifications(userId, false)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<NotificationModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<NotificationModel> notificationModels) {
                        mutableLiveNotifications.setValue(notificationModels);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mutableLiveErrorMessage.setValue(e.getMessage());
                    }
                });
    }

    public void updateDisplayNotifications(Context context, String userId){
        ecoDatabase = EcoDatabase.getInstance(context);
        ecoDatabase.dao().updateDisplayNotifications(userId, false)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mutableLiveNotificationsUpdated.setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mutableLiveErrorMessage.setValue(e.getMessage());
                    }
                });
    }
    public void deleteNotification(Context context, int notificationId){
        ecoDatabase = EcoDatabase.getInstance(context);
        ecoDatabase.dao().deleteNotification(notificationId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mutableLiveNotificationsDeleted.setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mutableLiveErrorMessage.setValue(e.getMessage());
                    }
                });
    }

    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public LiveData<Boolean> updateDisplayNotifications() {
        return mutableLiveNotificationsUpdated;
    }
    public LiveData<Boolean> deleteNotification() {
        return mutableLiveNotificationsDeleted;
    }

    public LiveData<List<NotificationModel>> getNotifications() {

        return mutableLiveNotifications;
    }
}