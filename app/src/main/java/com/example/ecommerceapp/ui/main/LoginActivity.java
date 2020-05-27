package com.example.ecommerceapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.data.InternetConnection;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.ActivityLoginBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AccountInterface{

    private ActivityLoginBinding binding;
    private String name, userName, email, password;
    private UserViewModel viewModel;
    private Utils utils;
    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.loginBt.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();

        loginAuto();

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getLogInUser().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                displayProgressDialog(false);
                if(userModels != null) {
                    responseData(userModels);
                }else {
                    displaySnackBar(true, null);
                }
            }
        });
    }
    private void loginAuto(){
        if(Preferences.getINSTANCE(LoginActivity.this, PREFERENCES_DATA_USER).isDataUserExist()){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void responseData(ArrayList<UserModel> userModels){
        if(userModels.size() > 0){

            if(Preferences.getINSTANCE(LoginActivity.this, PREFERENCES_DATA_USER).isDataUserExist()){

            }else {
                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();

                Preferences.getINSTANCE(this, PREFERENCES_DATA_USER).setDataUser(userModels.get(0));
                loginAuto();
            }
        }
        else {
           displaySnackBar(true, getString(R.string.logInField));
        }
    }
    private void getFacebookData(JSONObject object){
        if(object != null) {
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
        }else {
            displaySnackBar(true, null);
        }
    }
    private void logIn(String email){
        UserModel user = new UserModel(email);
        displayProgressDialog(true);
        viewModel.logIn(user);
    }
    private void logInAccount(){
        if(validateRegister()){
            if (InternetConnection.isNetworkOnline(this)) {
                UserModel user = new UserModel(userName, password, false);
                displayProgressDialog(true);
                viewModel.logInAccount(user);
            }else {
                displaySnackBar(true, null);
            }
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

    private void displaySnackBar(boolean show, String msg){
        if(msg == null){
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(this);
        utils.snackBar(findViewById(R.id.container), msg);
        utils.displaySnackBar(show);
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



    // Check Login Status
    private void checkLogin(){

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }
}
