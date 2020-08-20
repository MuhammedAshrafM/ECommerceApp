package com.example.ecommerceapp.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.JavaMailAPI;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.PasswordDialog;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentEditProfileBinding;
import com.example.ecommerceapp.databinding.FragmentResetPasswordBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.more.MoreViewModel;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment implements View.OnClickListener, ItemDialogClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener {


    private UserViewModel userViewModel;
    private FragmentResetPasswordBinding binding;
    private View root;
    private Utils utils;
    private PasswordDialog dialog;
    private Context context;
    private Activity activity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "verificationCode";

    // TODO: Rename and change types of parameters
    private String email;
    private int verificationCode;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();

//        ((AppCompatActivity) activity).getSupportActionBar().hide();
//        setHasOptionsMenu(true);

        if (getArguments() != null) {
            email = getArguments().getString(ARG_PARAM1);
            verificationCode = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reset_password,container,false);
        root = binding.getRoot();

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(getString(R.string.title_reset_password));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.resendCodeTv.setOnClickListener(this::onClick);
        binding.emailAddressTv.setText(email);

        observeLiveData();

        setViewEditPasswordDialog();

        addTextChangedListener();
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
        userViewModel.getEditPassword().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                if(s != null) {
                    responseData(s);
                }
            }
        });

        userViewModel.getMessageResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    displayProgressDialog(false);
                    displaySnackBar(true, getString(R.string.message_sent), 0);
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

    private void responseData(String response){
        String message = "";

        if(response.equals(getString(R.string.success))){
            message = getString(R.string.resetPasswordSuccess);
            activity.onBackPressed();
        }
        else if(response.equals(getString(R.string.failed))){
            message = getString(R.string.resetPasswordField);
        }
        displaySnackBar(true, message, -2);
    }

    private void addTextChangedListener() {
        StringBuilder stringBuilder = new StringBuilder();

        binding.digitOneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 0){
                    stringBuilder.append(charSequence);
                    binding.digitOneEt.clearFocus();
                    binding.digitTwoEt.requestFocus();
                    binding.digitTwoEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(0);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.digitTwoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 1){
                    stringBuilder.append(charSequence);
                    binding.digitTwoEt.clearFocus();
                    binding.digitThreeEt.requestFocus();
                    binding.digitThreeEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(1);
                    binding.digitTwoEt.clearFocus();
                    binding.digitOneEt.requestFocus();
                    binding.digitOneEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.digitThreeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 2){
                    stringBuilder.append(charSequence);
                    binding.digitThreeEt.clearFocus();
                    binding.digitFourEt.requestFocus();
                    binding.digitFourEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(2);
                    binding.digitThreeEt.clearFocus();
                    binding.digitTwoEt.requestFocus();
                    binding.digitTwoEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.digitFourEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 3){
                    stringBuilder.append(charSequence);
                    binding.digitFourEt.clearFocus();
                    binding.digitFiveEt.requestFocus();
                    binding.digitFiveEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(3);
                    binding.digitFourEt.clearFocus();
                    binding.digitThreeEt.requestFocus();
                    binding.digitThreeEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.digitFiveEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 4){
                    stringBuilder.append(charSequence);
                    binding.digitFiveEt.clearFocus();
                    binding.digitSixEt.requestFocus();
                    binding.digitSixEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(4);
                    binding.digitFiveEt.clearFocus();
                    binding.digitFourEt.requestFocus();
                    binding.digitFourEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.digitSixEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 5){
                    stringBuilder.append(charSequence);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(5);
                    binding.digitSixEt.clearFocus();
                    binding.digitFiveEt.requestFocus();
                    binding.digitFiveEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(stringBuilder.length() == 6){
                    verifyCode(stringBuilder.toString());
                }
            }
        });
    }

    private void verifyCode(String code){
        if(code.equals(String.valueOf(verificationCode))){
            dialog.show();
        }else {
            binding.setVisibleText(true);
        }
    }

    private void setViewEditPasswordDialog(){
        dialog = dialog.getINSTANCE(context, activity, R.style.MaterialDialogSheet, this::onItemDialogClick, null);
    }
    private void reSendVerificationCode(){
        displayProgressDialog(true);

        Random random = new Random();
        verificationCode = 100000 + random.nextInt(899999);

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
        utils.snackBar(activity.findViewById(R.id.containerResetPassword), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.resend_code_tv){
            reSendVerificationCode();
        }else {
            activity.onBackPressed();
        }
    }

    @Override
    public void onItemDialogClick(View view, int id) {
        if(id == R.string.title_forget){
            userViewModel.editPassword(email, dialog.getPassword());
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }

}