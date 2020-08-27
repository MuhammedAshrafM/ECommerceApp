package com.example.ecommerceapp.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentRegisterBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import static com.facebook.internal.FacebookDialogFragment.TAG;

public class RegisterFragment extends Fragment implements View.OnClickListener, AccountInterface,
        ConnectivityReceiver.ConnectivityReceiveListener {

    private FragmentRegisterBinding binding;
    private View root;
    private String name, userName, email, password, imageProfilePath, namePattern, userNamePattern, passwordPattern;
    private UserViewModel viewModel;
    private Utils utils;
    private Context context;
    private Activity activity;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        context = getContext();
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
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    @Override
    public void onStart() {
        super.onStart();

        namePattern = "[a-z\\sA-Z]*";
        userNamePattern = "(\\w)*";
        passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=-_])(?=\\S).*";
        binding.registerBt.setOnClickListener(this);

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
                displaySnackBar(true, null, 0);
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
        context.registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityReceiveListener(this);

    }

    private void responseData(String response){
        String message = "";

        if(response.equals(getString(R.string.success))){
            message = getString(R.string.singUpSuccess);
        }
        else if(response.equals(getString(R.string.failed))){
            message = getString(R.string.singUpField);
        }
        else if(response.equals(getString(R.string.repeatedEmail))){
            message = getString(R.string.singUpRepeated);
        }
        else if(response.equals(getString(R.string.repeatedAccount))){
            message = getString(R.string.createAccountRepeated);
        }

        displaySnackBar(true, message, -2);
    }
    private void getFacebookData(JSONObject object){
        if(object != null) {
            try {
            imageProfilePath = new URL("https://graph.facebook.com/" + object.getString("id") +
                    "/picture?width=250&height=250").toString();
                name = object.getString("first_name") + " " + object.getString("last_name");
                email = object.getString("email");
                subscribeToTopic(name, null, null, email, imageProfilePath);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            displayProgressDialog(false);
            displaySnackBar(true,null, 0);
        }
    }

    private void signUp(String name, String email, String imageProfilePath, String token){
        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        user.setImagePath(imageProfilePath);
        user.setToken(token);
        viewModel.signUp(user);
    }
    private void createAccount(String name, String userName, String password, String token){
        UserModel user = new UserModel();
        user.setName(name);
        user.setUserName(userName);
        user.setPassword(password);
        user.setToken(token);
        viewModel.createAccount(user);
    }

    private void subscribeToTopic(String name, String userName, String password, String email, String imageProfilePath){
        displayProgressDialog(true);
        FirebaseMessaging.getInstance().subscribeToTopic("User")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (ConnectivityReceiver.isConnected()) {
                            getToken(name, userName, password, email, imageProfilePath);
                        }else {
                            displayProgressDialog(false);
                            displaySnackBar(true, null, 0);
                        }
                        if (!task.isSuccessful()) {
                            displayProgressDialog(false);
                            displaySnackBar(true, null, 0);
                        }
                    }
                });
    }

    private void getToken(String name, String userName, String password, String email, String imageProfilePath){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            displayProgressDialog(false);
                            displaySnackBar(true, null, 0);
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Gson gson = new Gson();
                        token = gson.toJson(new String[]{token});
                        if (ConnectivityReceiver.isConnected()) {
                            if (email != null) {
                                signUp(name, email, imageProfilePath, token);
                            } else {
                                createAccount(name, userName, password, token);
                            }
                        }else {
                            displayProgressDialog(false);
                            displaySnackBar(true, null, 0);
                        }

                    }
                });
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
        else if(!name.matches(namePattern)){
            binding.registerNameEt.setError(getString(R.string.characterPatternField));
            focusView = binding.registerNameEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(userName)){
            binding.registerUsernameEt.setError(getString(R.string.userNameField));
            focusView = binding.registerUsernameEt;
            cancel = true;
        }
        else if(!userName.matches(userNamePattern)){
            binding.registerUsernameEt.setError(getString(R.string.userNamePatternField));
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
        else if(!password.matches(passwordPattern)){
            binding.registerPasswordEt.setError(getString(R.string.passwordPatternField));
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
        binding.setVisibleProgress(show);
    }
    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null){
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(activity.findViewById(R.id.container), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.register_bt){
            if(validateRegister()) {
                subscribeToTopic(name, userName, password, null, null);
            }
        }
    }

    @Override
    public void onGoogleListener(GoogleSignInAccount account) {
        subscribeToTopic(account.getDisplayName(), null, null, account.getEmail(), account.getPhotoUrl().toString());
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
            displaySnackBar(true, null, 0);
        }
    }
}
