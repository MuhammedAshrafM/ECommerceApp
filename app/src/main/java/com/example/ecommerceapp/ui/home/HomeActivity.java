package com.example.ecommerceapp.ui.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.BackPressedListener;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.NotificationListener;
import com.example.ecommerceapp.data.NotificationReceiver;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.pojo.NotificationModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.notifications.NotificationsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import static com.facebook.internal.FacebookDialogFragment.TAG;

public class HomeActivity extends AppCompatActivity implements NotificationListener{

    private BackPressedListener listener;
    private BottomNavigationView navView;
    private NotificationsViewModel notificationsViewModel;
    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_orders,
                R.id.navigation_notifications, R.id.navigation_more)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        getSupportFragmentManager().popBackStack();

        navView.setItemIconTintList(null);
    }

    public void setOnBackPressedListener(BackPressedListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onStart() {
        super.onStart();

        user = Preferences.getINSTANCE(this, PREFERENCES_DATA_USER).getDataUser();

        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        getNewNotifications();

        notificationsViewModel.getNotifications().observe(this, new Observer<List<NotificationModel>>() {
            @Override
            public void onChanged(List<NotificationModel> notificationModels) {
                int size = notificationModels.size();
                navView.getMenu().getItem(2).setIcon(Utils.convertLayoutToImage(HomeActivity.this, size, R.drawable.ic_notifications_black_24dp));
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        // register intent filter
        final IntentFilter intentFilter = new IntentFilter("NEW_NOTIFICATION");

        NotificationReceiver notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, intentFilter);

        MyApplication.getInstance().setNotificationReceiveListener(this::onNotifyChange);
    }

    // call this method when listen home activity to edit on icon
    private void getNewNotifications(){
        notificationsViewModel.getNewNotifications(this, user.getId());
    }
    @Override
    public void onBackPressed() {
        if (listener != null) {
            listener.OnBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNotifyChange() {
        getNewNotifications();
    }
}
