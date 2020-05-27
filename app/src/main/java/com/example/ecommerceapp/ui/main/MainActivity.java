package com.example.ecommerceapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.joinAppButton.setOnClickListener(this);
        binding.openAppButton.setOnClickListener(this);



    }

    @Override
    protected void onStart() {
        super.onStart();
        loginAuto();
    }

    private void loginAuto(){
        if(Preferences.getINSTANCE(MainActivity.this, PREFERENCES_DATA_USER).isDataUserExist()){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.joinAppButton:
                startActivity(new Intent(this,RegisterActivity.class));
                break;

            case R.id.openAppButton:
                startActivity(new Intent(this,LoginActivity.class));
                break;

            default:

                break;
        }
    }

}
