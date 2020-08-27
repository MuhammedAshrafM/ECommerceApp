package com.example.ecommerceapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ecommerceapp.pojo.NotificationModel;


@Database(entities = NotificationModel.class, version = 3)
abstract public class EcoDatabase extends RoomDatabase {

    private static EcoDatabase INSTANCE;
    public abstract NotificationDao dao();

    public static synchronized EcoDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    EcoDatabase.class, "EcoDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }
}
