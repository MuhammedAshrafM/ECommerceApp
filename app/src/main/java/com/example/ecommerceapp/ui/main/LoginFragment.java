package com.example.ecommerceapp.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.ecommerceapp.data.JavaMailAPI;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentLoginBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.facebook.internal.FacebookDialogFragment.TAG;

public class LoginFragment extends Fragment implements View.OnClickListener, AccountInterface,
        ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private FragmentLoginBinding binding;
    private View root;
    private String name, userName, email, password;
    private UserViewModel userViewModel;
    private Utils utils;
    private Context context;
    private Activity activity;
    private boolean back = false;
    private int verificationCode;
    private String tokenJson;
    private UserModel user;

    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    public LoginFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        root = binding.getRoot();

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding.loginBt.setOnClickListener(this);
        binding.forgetTv.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        observeLiveData();
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


    private void observeLiveData(){

        userViewModel.getLogInUser().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                responseData(userModels);
            }
        });

        userViewModel.getTokenResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                responseTokenData(s);
            }
        });

        userViewModel.getEmail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!back) {
                    displayProgressDialog(false);
                    responseData(s);
                }
            }
        });


        userViewModel.getMessageResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!back) {
                    if (aBoolean) {
                        displayProgressDialog(false);
                        displaySnackBar(true, getString(R.string.message_sent), 0);
                        Bundle bundle = new Bundle();
                        bundle.putString("email", email);
                        bundle.putInt("verificationCode", verificationCode);
                        navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment, bundle);
                    }
                    back = true;
                }
            }
        });


        userViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0);
            }
        });
    }
    private void login(){
        Toast.makeText(context, getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
        Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).setDataUser(user);
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        activity.finish();
    }
    private void responseData(ArrayList<UserModel> userModels){

        if(userModels != null && userModels.size() > 0){

            if(Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).isDataUserExist()){

            }else {
                user = userModels.get(0);
                subscribeToTopic();
            }
        }
        else {
            displayProgressDialog(false);
            displaySnackBar(true, getString(R.string.logInField), -2);
        }
    }

    private void responseData(String response){
        String message = "";
        if(response.equals(getString(R.string.failed))){
            message = getString(R.string.emailFailed);
        }else if(response.equals(getString(R.string.unvalidatedAccount))){
            message = getString(R.string.account_not_validated);
        }
        else if(response.startsWith("email_")){
            message = getString(R.string.send_code);
            displaySnackBar(true, message, 0);

            email = response.substring(6);
            if (ConnectivityReceiver.isConnected()) {
                sendVerificationCode();
            }else {
                displaySnackBar(true, null, 0);
            }

            return;
        }
        displaySnackBar(true, message, -2);
    }

    private void responseTokenData(String result){
        if (result.equals(getString(R.string.failed))){
            displaySnackBar(true, getString(R.string.addTokenFailed), -2);
        }else {
            user.setToken(result);
            login();
        }
    }

    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("User")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (ConnectivityReceiver.isConnected()) {
                            getToken();
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

    private void getToken(){
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
                        tokenJson = user.getToken();
                        if(tokenJson.contains(token)){
                            displayProgressDialog(false);
                            login();
                        }else {
                            ArrayList<String> tokens = new ArrayList<>();
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<String>>(){}.getType();
                            tokens = gson.fromJson(tokenJson, type);
                            tokens.add(token);
                            token = gson.toJson(tokens);
                            if(token.length() > 1000){
                                tokens.remove(0);
                                token = gson.toJson(tokens);
                            }
                            addToken(token);
                        }
                    }
                });
    }
    private void getFacebookData(JSONObject object){
        if(object != null) {
            try {
                name = object.getString("first_name") + " " + object.getString("last_name");
                email = object.getString("email");
                logIn(email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            displaySnackBar(true, null, 0);
        }
    }
    private void logIn(String email){
        UserModel user = new UserModel();
        user.setEmail(email);
        displayProgressDialog(true);
        userViewModel.logIn(user);
    }
    private void logInAccount(){
        if(validateRegister()){
            if (ConnectivityReceiver.isConnected()) {
                UserModel user = new UserModel();
                user.setUserName(userName);
                user.setPassword(password);
                displayProgressDialog(true);
                userViewModel.logInAccount(user);
            }else {
                displaySnackBar(true, null, 0);
            }
        }
    }

    private void forgetPassword(){
        if(validateForgetPassword()){
            if (ConnectivityReceiver.isConnected()) {
                back = false;
                displayProgressDialog(true);
                userViewModel.getEmail(userName);
            }else {
                displaySnackBar(true, null, 0);
            }
        }
    }
    private void addToken(String token){
        if (ConnectivityReceiver.isConnected()) {
            userViewModel.addToken(token, user.getId());
        }else {
            displaySnackBar(true, null, 0);
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
            binding.loginUsernameEt.setError(getString(R.string.userName_emailField));
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
    private boolean validateForgetPassword() {
        boolean cancel = false;
        View focusView = null;

        getData();

        if (TextUtils.isEmpty(userName)) {
            binding.loginUsernameEt.setError(getString(R.string.userName_emailField));
            focusView = binding.loginUsernameEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void sendVerificationCode(){
        displayProgressDialog(true);
        Random random = new Random();
        verificationCode = 100000 + random.nextInt(899999);
        back = false;
        userViewModel.sendMessage(email, getString(R.string.subject_email),
                String.format(Locale.getDefault(),"%s %d", getString(R.string.message_email), verificationCode));
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
        switch (view.getId()){
            case R.id.login_bt:
                logInAccount();
                break;

            case R.id.forget_tv:
                forgetPassword();
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
            displaySnackBar(true, null, 0);
        }
    }

}
