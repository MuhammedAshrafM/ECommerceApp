package com.example.ecommerceapp.ui.main;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentRegisterBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class RegisterFragment extends Fragment implements View.OnClickListener, AccountInterface,
        ConnectivityReceiver.ConnectivityReceiveListener {

    private FragmentRegisterBinding binding;
    private View root;
    private String name, userName, email, password, imageProfilePath;
    private UserViewModel viewModel;
    private Snackbar snackbar;
    private Utils utils;

    public RegisterFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.registerBt.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getSignUpUser().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                if(s != null) {
                    responseData(s);
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
    }
    private void getFacebookData(JSONObject object){
        if(object != null) {
            try {
//            id = object.getString("id");
            imageProfilePath = new URL("https://graph.facebook.com/" + object.getString("id") +
                    "/picture?width=250&height=250").toString();
                name = object.getString("first_name") + " " + object.getString("last_name");
                email = object.getString("email");
                signUp(name, email, imageProfilePath);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            displaySnackBar(true,null);
        }
    }

    private void signUp(String name, String email, String imageProfilePath){
        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        user.setImagePath(imageProfilePath);
        displayProgressDialog(true);
        viewModel.signUp(user);
    }
    private void createAccount(){
        if(validateRegister()){
            if (ConnectivityReceiver.isConnected()) {
                UserModel user = new UserModel();
                user.setName(name);
                user.setUserName(userName);
                user.setPassword(password);
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
        utils = new Utils(getContext());
        utils.snackBar(getActivity().findViewById(R.id.container), msg, -2);
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
        signUp(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());
    }

    @Override
    public void onFacebookListener(JSONObject object) {
        getFacebookData(object);
    }

    @Override
    public void onProgressDialogListener(boolean show) {
        displayProgressDialog(show);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null);
        }
    }
}
