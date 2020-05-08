package com.example.ecommerceapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.joinAppButton.setOnClickListener(this);
        binding.openAppButton.setOnClickListener(this);

    }

    private void loginAuto(){

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
