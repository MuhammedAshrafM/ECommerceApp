package com.example.ecommerceapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ecommerceapp.pojo.NotificationModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNotification(NotificationModel notificationModel);

    @Query("Select * from notification where userId == :userId or userId == 'userLogOuted'")
    Single<List<NotificationModel>> getNotifications(String userId);

    @Query("Select * from notification where (userId == :userId or userId == 'userLogOuted') and displayed == :aBoolean")
    Single<List<NotificationModel>> getNewNotifications(String userId, Boolean aBoolean);


    @Query("Update notification set displayed ='true' where (userId == :userId or userId == 'userLogOuted') and displayed == :aBoolean")
    Completable updateDisplayNotifications(String userId, Boolean aBoolean);

    @Query("Delete from notification where id == :notificationId")
    Completable deleteNotification(int notificationId);
}
