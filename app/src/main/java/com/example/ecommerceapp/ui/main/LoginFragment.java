package com.example.ecommerceapp.ui.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentLoginBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LoginFragment extends Fragment implements View.OnClickListener, AccountInterface,
        ConnectivityReceiver.ConnectivityReceiveListener {

    private FragmentLoginBinding binding;
    private View root;
    private String name, userName, email, password;
    private UserViewModel viewModel;
    private Utils utils;
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.loginBt.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getLogInUser().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                displayProgressDialog(false);
                if(userModels != null && userModels.size() > 0) {
                    responseData(userModels);
                }
            }
        });


        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        getContext().registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityReceiveListener(this);

    }


    private void loginAuto(){
        if(Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).isDataUserExist()){
            Intent intent = new Intent(getContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void responseData(ArrayList<UserModel> userModels){
        if(userModels.size() > 0){

            if(Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).isDataUserExist()){

            }else {
                Toast.makeText(getContext(), getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();

                Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).setDataUser(userModels.get(0));
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
        UserModel user = new UserModel();
        user.setEmail(email);
        displayProgressDialog(true);
        viewModel.logIn(user);
    }
    private void logInAccount(){
        if(validateRegister()){
            if (ConnectivityReceiver.isConnected()) {
                UserModel user = new UserModel();
                user.setUserName(userName);
                user.setPassword(password);
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
        utils = new Utils(getContext());
        utils.snackBar(getActivity().findViewById(R.id.container), msg, -2);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null);
        }
    }
}
