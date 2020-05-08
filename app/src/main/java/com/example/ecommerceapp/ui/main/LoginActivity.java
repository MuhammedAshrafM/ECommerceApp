package com.example.ecommerceapp.ui.main;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.databinding.ActivityLoginBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AccountInterface{

    private ActivityLoginBinding binding;
    private String name, userName, email, password;
    private UserViewModel viewModel;
    private Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.loginBt.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.mutableLiveData2.observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                displayProgressDialog(false);
                responseData(userModels);
            }
        });
    }
    private void responseData(ArrayList<UserModel> userModels){
        if(userModels.size() > 0){

            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

        }
        else {
            String message = getString(R.string.logInField);
            snackbar = Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();
        }
    }
    private void getFacebookData(JSONObject object){
        try {
//            id = object.getString("id");
//            imageProfilePath = new URL("https://graph.facebook.com/" + object.getString("id") +
//                    "/picture?width=250&height=250").toString();
            name = object.getString("first_name") + " " + object.getString("last_name");
            email = object.getString("email");
            logIn(email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void logIn(String email){
        UserModel user = new UserModel(email);
        displayProgressDialog(true);
        viewModel.logIn(user);
    }
    private void logInAccount(){
        if(validateRegister()){
            UserModel user = new UserModel(userName,password,false);
            displayProgressDialog(true);
            viewModel.logInAccount(user);
        }
    }
    private void getData(){
        binding.loginUsernameEt.setError(null);
        binding.loginPasswordEt.setError(null);

        userName = binding.loginUsernameEt.getText().toString().trim();
        password = binding.loginPasswordEt.getText().toString().trim();
    }

    private boolean validateRegister() {
        boolean cancel = false;
        View focusView = null;

        getData();

        if(TextUtils.isEmpty(userName)){
            binding.loginUsernameEt.setError(getString(R.string.userNameField));
            focusView = binding.loginUsernameEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(password)){
            binding.loginPasswordEt.setError(getString(R.string.passwordField));
            focusView = binding.loginPasswordEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(password) < 6){
            binding.loginPasswordEt.setError(getString(R.string.infoPassword));
            focusView = binding.loginPasswordEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void displayProgressDialog(boolean show){
        if(show){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.GONE);
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_bt:
                logInAccount();
                break;

            default:
                break;
        }
    }
    @Override
    public void onGoogleListener(GoogleSignInAccount account) {
        logIn(account.getEmail());
    }

    @Override
    public void onFacebookListener(JSONObject object) {
        getFacebookData(object);
    }

    @Override
    public void onProgressDialogListener(boolean show) {
        displayProgressDialog(show);
    }
}
