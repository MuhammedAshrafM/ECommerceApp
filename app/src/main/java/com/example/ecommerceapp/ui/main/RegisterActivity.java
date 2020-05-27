package com.example.ecommerceapp.ui.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.data.InternetConnection;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.ActivityRegisterBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AccountInterface {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private String name, userName, email, password;
    private UserViewModel viewModel;
    private Snackbar snackbar;
    private Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        binding.registerBt.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getSignUpUser().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                if(s != null) {
                    responseData(s);
                }else {
                    displaySnackBar(true, null);
                }
            }
        });
    }

    private void responseData(String response){
        String message = "";
        if(response.equals("Success")){
            message = getString(R.string.singUpSuccess);
        }
        else if(response.equals("Failed")){
            message = getString(R.string.singUpField);
        }
        else if(response.equals("RepeatedEmail")){
            message = getString(R.string.singUpRepeated);
        }
        else if(response.equals("RepeatedAccount")){
            message = getString(R.string.createAccountRepeated);
        }

        displaySnackBar(true, message);
//        snackbar = Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_INDEFINITE);
//
//        snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
//            @Override
//            @TargetApi(Build.VERSION_CODES.M)
//            public void onClick(View v) {
//                snackbar.dismiss();
//            }
//        });
//
//        snackbar.show();
    }
    private void getFacebookData(JSONObject object){
        if(object != null) {
            try {
//            id = object.getString("id");
//            imageProfilePath = new URL("https://graph.facebook.com/" + object.getString("id") +
//                    "/picture?width=250&height=250").toString();
                name = object.getString("first_name") + " " + object.getString("last_name");
                email = object.getString("email");
                signUp(name, email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            displaySnackBar(true,null);
        }
    }

    private void signUp(String name, String email){
        UserModel user = new UserModel(name,email);
        displayProgressDialog(true);
        viewModel.signUp(user);
    }
    private void createAccount(){
        if(validateRegister()){
            if (InternetConnection.isNetworkOnline(this)) {
                UserModel user = new UserModel(name, userName, password);
                displayProgressDialog(true);
                viewModel.createAccount(user);
            }else {
                displaySnackBar(true, null);
            }
        }
    }

    private void getData(){
        binding.registerNameEt.setError(null);
        binding.registerUsernameEt.setError(null);
        binding.registerPasswordEt.setError(null);

        name = binding.registerNameEt.getText().toString().trim();
        userName = binding.registerUsernameEt.getText().toString().trim();
        password = binding.registerPasswordEt.getText().toString().trim();
    }

    private boolean validateRegister() {
        boolean cancel = false;
        View focusView = null;

        getData();

        if(TextUtils.isEmpty(name)){
            binding.registerNameEt.setError(getString(R.string.nameField));
            focusView = binding.registerNameEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(userName)){
            binding.registerUsernameEt.setError(getString(R.string.userNameField));
            focusView = binding.registerUsernameEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(password)){
            binding.registerPasswordEt.setError(getString(R.string.passwordField));
            focusView = binding.registerPasswordEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(password) < 6){
            binding.registerPasswordEt.setError(getString(R.string.infoPassword));
            focusView = binding.registerPasswordEt;
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
            case R.id.register_bt:
                createAccount();
                break;

            default:
                break;
        }
    }

    @Override
    public void onGoogleListener(GoogleSignInAccount account) {
        signUp(account.getDisplayName(), account.getEmail());
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
