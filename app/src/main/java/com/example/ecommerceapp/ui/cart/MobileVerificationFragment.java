package com.example.ecommerceapp.ui.cart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.BackPressedListener;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.SnackBarActionListener;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentMobileVerificationBinding;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.example.ecommerceapp.ui.search.SearchableActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.android.volley.VolleyLog.TAG;

public class MobileVerificationFragment extends Fragment implements View.OnClickListener, BackPressedListener,
        SnackBarActionListener, ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private CartViewModel cartViewModel;
    private FragmentMobileVerificationBinding binding;
    private View root;
    private Utils utils;
    private Context context;
    private Activity activity;
    private UserModel user;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser;
    private String mobileNumber, keyCountryCode, verifyId, mobileNumberEdited, keyCountryCodeEdited, countryCodeName;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private Thread thread;
    private boolean stop = false;
    private Animation animationEnter, animationExit;
    private AddressModel addressModel;
    private Bundle bundleProducts;
    private Dialog dialog;
    private Button verifyBt, cancelVerifyBt;

    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final String PREFERENCES_ADDRESSES_SAVED = "ADDRESSES_SAVED";

    public MobileVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();
        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            bundleProducts = getArguments();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_verification, container, false);
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

        binding.toolbar.setTitle(getString(R.string.title_verification_mobile));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);
        binding.contentSendVerification.setVisible(true);
        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        selectActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        getSharedPreferences();

        updateUI();

        GlideClient.loadResourceImage(context, R.drawable.message_verification, binding.contentSendVerification.imageView);
        GlideClient.loadResourceImage(context, R.drawable.verification_code, binding.contentEnterVerificationCode.imageView2);

        setOnClickListener();

        // connect to Authentication of firebase
        firebaseAuth = FirebaseAuth.getInstance();

        loadAnimation();
        addCallback();
        addTextChangedListener();
        setAlertDialog();
        observeLiveData();


        binding.contentEditMobileNumber.keyCountryCp3.
                registerCarrierNumberEditText(binding.contentEditMobileNumber.mobileNumberEt);
    }

    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
            ((HomeActivity) activity).setOnBackPressedListener(this::OnBackPressed);
        }else if(activity.getClass().getSimpleName().contains("SearchableActivity")) {
            ((SearchableActivity)activity).setOnBackPressedListener(this::OnBackPressed);
        }
    }

    private void getSharedPreferences(){
        addressModel = Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).getAddressSavedSelected();
        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();
    }

    private void setOnClickListener(){
        binding.contentSendVerification.editTv.setOnClickListener(this::onClick);
        binding.contentSendVerification.sendVerificationBt.setOnClickListener(this::onClick);
        binding.contentEnterVerificationCode.editTv2.setOnClickListener(this::onClick);
        binding.contentEnterVerificationCode.resendCodeTv.setOnClickListener(this::onClick);
        binding.contentEditMobileNumber.sendVerificationBt2.setOnClickListener(this::onClick);
    }

    private void loadAnimation(){
        animationEnter = AnimationUtils.loadAnimation(context,R.anim.slide_in_top);
        animationExit = AnimationUtils.loadAnimation(context,R.anim.slide_in_bottom);
    }

    private void observeLiveData(){
        cartViewModel.validateMobileNumber().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);

                if(s.equals(getString(R.string.success))){
                    Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).validateMobileNumber(mobileNumber, countryCodeName);
                    stopBackPressedListener();
                    navController.navigate(R.id.action_mobileVerificationFragment_to_orderFragment, bundleProducts);
                }else if (s.equals(getString(R.string.failed))){
                    failedAddress(getString(R.string.addressField));
                }
            }
        });
        cartViewModel.editAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);

                if(s.equals(getString(R.string.success))){
                    Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).editAddressSaved(addressModel);
                    updateUI();
                }else if (s.equals(getString(R.string.failed))){
                    failedAddress(getString(R.string.addressField));

                }

            }
        });
        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, R.string.ok, 0, null);
            }
        });
    }
    private void failedAddress(String stringResource){
        displaySnackBar(true, stringResource, R.string.retry, 0, this::onActionListener);
    }

    private void addCallback(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                stopThread();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    displaySnackBar(true, e.getMessage(), R.string.ok, -2, null);
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    displaySnackBar(true, e.getMessage(), R.string.ok, -2, null);
                    // The SMS quota for the project has been exceeded
                    // ...
                }else {
                    displaySnackBar(true, null, R.string.ok, 0, null);
                }
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                stopThread();
                verifyId = verificationId;
                resendToken = token;
            }
        };
    }
    private void addTextChangedListener() {
        StringBuilder stringBuilder = new StringBuilder();

        binding.contentEnterVerificationCode.digitOneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 0){
                    stringBuilder.append(charSequence);
                    binding.contentEnterVerificationCode.digitOneEt.clearFocus();
                    binding.contentEnterVerificationCode.digitTwoEt.requestFocus();
                    binding.contentEnterVerificationCode.digitTwoEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(0);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.contentEnterVerificationCode.digitTwoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 1){
                    stringBuilder.append(charSequence);
                    binding.contentEnterVerificationCode.digitTwoEt.clearFocus();
                    binding.contentEnterVerificationCode.digitThreeEt.requestFocus();
                    binding.contentEnterVerificationCode.digitThreeEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(1);
                    binding.contentEnterVerificationCode.digitTwoEt.clearFocus();
                    binding.contentEnterVerificationCode.digitOneEt.requestFocus();
                    binding.contentEnterVerificationCode.digitOneEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.contentEnterVerificationCode.digitThreeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 2){
                    stringBuilder.append(charSequence);
                    binding.contentEnterVerificationCode.digitThreeEt.clearFocus();
                    binding.contentEnterVerificationCode.digitFourEt.requestFocus();
                    binding.contentEnterVerificationCode.digitFourEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(2);
                    binding.contentEnterVerificationCode.digitThreeEt.clearFocus();
                    binding.contentEnterVerificationCode.digitTwoEt.requestFocus();
                    binding.contentEnterVerificationCode.digitTwoEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.contentEnterVerificationCode.digitFourEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 3){
                    stringBuilder.append(charSequence);
                    binding.contentEnterVerificationCode.digitFourEt.clearFocus();
                    binding.contentEnterVerificationCode.digitFiveEt.requestFocus();
                    binding.contentEnterVerificationCode.digitFiveEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(3);
                    binding.contentEnterVerificationCode.digitFourEt.clearFocus();
                    binding.contentEnterVerificationCode.digitThreeEt.requestFocus();
                    binding.contentEnterVerificationCode.digitThreeEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.contentEnterVerificationCode.digitFiveEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 4){
                    stringBuilder.append(charSequence);
                    binding.contentEnterVerificationCode.digitFiveEt.clearFocus();
                    binding.contentEnterVerificationCode.digitSixEt.requestFocus();
                    binding.contentEnterVerificationCode.digitSixEt.setCursorVisible(true);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(4);
                    binding.contentEnterVerificationCode.digitFiveEt.clearFocus();
                    binding.contentEnterVerificationCode.digitFourEt.requestFocus();
                    binding.contentEnterVerificationCode.digitFourEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.contentEnterVerificationCode.digitSixEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != " " && stringBuilder.length() == 5){
                    stringBuilder.append(charSequence);
                }else if(i2 == 0){
                    stringBuilder.deleteCharAt(5);
                    binding.contentEnterVerificationCode.digitSixEt.clearFocus();
                    binding.contentEnterVerificationCode.digitFiveEt.requestFocus();
                    binding.contentEnterVerificationCode.digitFiveEt.setCursorVisible(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(stringBuilder.length() == 6){
                    if(verifyId != null) {
                        verifyCode(stringBuilder.toString());
                    }
                }
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

    private void updateUI(){
        mobileNumber = addressModel.getMobileNumber();
        countryCodeName = addressModel.getCountryCodeName();

        binding.contentSendVerification.mobileNumberTv.setText(mobileNumber);
        binding.contentEnterVerificationCode.mobileNumberTv2.setText(mobileNumber);
        binding.contentEditMobileNumber.mobileNumberEt.setText(mobileNumber);

        binding.contentSendVerification.keyCountryCp.setCountryForNameCode(countryCodeName);
        binding.contentEnterVerificationCode.keyCountryCp2.setCountryForNameCode(countryCodeName);
        binding.contentEditMobileNumber.keyCountryCp3.setCountryForNameCode(countryCodeName);

        keyCountryCode = binding.contentSendVerification.keyCountryCp.getSelectedCountryCodeWithPlus();
    }


    private void startThread(){
        stop = false;
        binding.contentEnterVerificationCode.resendCodeTv.setEnabled(false);
        thread = new Thread(new Runnable() {
            int i = 59;
            @Override
            public void run() {
                while (i >= 0){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.contentEnterVerificationCode.timeOutTv.setText(String.format(Locale.getDefault(),"00:%02d", i));
                            if(i == 0){
                                stopThread();
                            }
                        }
                    });
                    if(i != 0 && !stop) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(i == 0 || stop){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                interruptThread();
                            }
                        });
                    }
                    i--;
                }
            }
        });
        thread.start();
    }

    private void stopThread(){
        stop = true;
    }

    private void interruptThread(){
        thread.interrupt();
        binding.contentEnterVerificationCode.timeOutTv.setText(String.format(Locale.getDefault(), "00:%02d", 0));
        binding.contentEnterVerificationCode.resendCodeTv.setEnabled(true);
    }

    private void displayContent(int id){
        switch (id){
            case R.id.edit_tv:
                binding.contentSendVerification.setVisible(false);
                binding.contentEditMobileNumber.setVisible(true);
                binding.contentEditMobileNumber.editMobileNumberCl.setAnimation(animationEnter);
                break;
            case R.id.send_verification_bt:
                binding.contentSendVerification.setVisible(false);
                binding.contentEnterVerificationCode.setVisibleText(false);
                binding.contentEnterVerificationCode.setVisible(true);
                binding.contentEnterVerificationCode.enterVerificationCodeCl.setAnimation(animationEnter);
                break;
            case R.id.edit_tv2:
                binding.contentEnterVerificationCode.setVisible(false);
                binding.contentEditMobileNumber.setVisible(true);
                binding.contentEditMobileNumber.editMobileNumberCl.setAnimation(animationExit);
                break;
            case R.id.send_verification_bt2:
                binding.contentEditMobileNumber.setVisible(false);
                binding.contentEnterVerificationCode.setVisibleText(false);
                binding.contentEnterVerificationCode.setVisible(true);
                binding.contentEnterVerificationCode.enterVerificationCodeCl.setAnimation(animationExit);
                break;

            default:

                break;
        }
    }

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg, int actionResource, int duration, SnackBarActionListener listener){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        if(listener != null) {
            utils = new Utils(context, this::onActionListener);
        }else {
            utils = new Utils(context);
        }
        utils.snackBar(root.findViewById(R.id.containerMobileVerification), msg, actionResource, duration);
        utils.displaySnackBar(show);
    }

    private void sendVerificationCode(){
        startThread();

        firebaseAuth.setLanguageCode("ar");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                keyCountryCode + mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }
    private void reSendVerificationCode(){
        startThread();
        firebaseAuth.setLanguageCode("ar");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                keyCountryCode + mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                mCallbacks,
                resendToken);
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        displayProgressDialog(true);
        try {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            displayProgressDialog(false);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                firebaseUser = firebaseAuth.getCurrentUser();
                                firebaseUser.delete();
                                if (ConnectivityReceiver.isConnected()) {
                                    displayProgressDialog(true);
                                    cartViewModel.validateMobileNumber(countryCodeName, mobileNumber, user.getId());
                                }else {
                                    failedAddress(null);
                                }
                                Log.d(TAG, "MEME isSuccessful: ");

                                // ...
                            } else {
                                // Sign in failed, display a message and update the UI
                                Log.d(TAG, "MEME noSuccessful: ");
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    displaySnackBar(true, task.getException().getMessage(), R.string.ok, -2, null);
                                    binding.contentEnterVerificationCode.setVisibleText(true);
                                    // The verification code entered was invalid
                                    Log.d(TAG, "MEME getException: ");
                                }else {
                                    displaySnackBar(true, null, R.string.ok, 0, null);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void editMobileNumber(){
        if(validateMobileNumber()){
            if(!mobileNumber.equals(mobileNumberEdited) || !keyCountryCode.equals(keyCountryCodeEdited)){
                addressModel.setMobileNumber(mobileNumberEdited);
                addressModel.setCountryCodeName(countryCodeName);

                if (ConnectivityReceiver.isConnected()) {
                    displayProgressDialog(true);
                    cartViewModel.editAddress(addressModel);
                }else {
                    displaySnackBar(true, null, R.string.ok, 0, null);
                }
            }

            if (ConnectivityReceiver.isConnected()) {
                sendVerificationCode();
            }

            displayContent(R.id.send_verification_bt2);
        }
    }
    private void getData(){
        binding.contentEditMobileNumber.mobileNumberEt.setError(null);

        mobileNumberEdited = binding.contentEditMobileNumber.mobileNumberEt.getText().toString().trim();
        keyCountryCodeEdited = binding.contentEditMobileNumber.keyCountryCp3.getSelectedCountryCodeWithPlus();
        countryCodeName = binding.contentEditMobileNumber.keyCountryCp3.getSelectedCountryNameCode();
    }

    private boolean validateMobileNumber() {
        boolean cancel = false;
        View focusView = null;

        getData();

        if(TextUtils.isEmpty(mobileNumberEdited)){
            binding.contentEditMobileNumber.mobileNumberEt.setError(getString(R.string.mobileNumberField));
            focusView = binding.contentEditMobileNumber.mobileNumberEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(mobileNumberEdited) < 10){
            binding.contentEditMobileNumber.mobileNumberEt.setError(getString(R.string.mobileNumberIncorrect));
            focusView = binding.contentEditMobileNumber.mobileNumberEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }


    private void setAlertDialog(){
        View view = getLayoutInflater().inflate(R.layout.layout_mobile_verification, null);
        verifyBt = view.findViewById(R.id.verify_bt);
        cancelVerifyBt = view.findViewById(R.id.cancel_verify_bt);

        verifyBt.setOnClickListener(this);
        cancelVerifyBt.setOnClickListener(this);

        dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void displayDialog(boolean show){
        if(show){
            dialog.show();
        }else {
            dialog.dismiss();
        }
    }

    private void stopBackPressedListener(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            ((HomeActivity) activity).setOnBackPressedListener(null);
        }else if(activity.getClass().getSimpleName().contains("SearchableActivity")) {
            ((SearchableActivity)activity).setOnBackPressedListener(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_tv:
                displayContent(R.id.edit_tv);
                break;
            case R.id.send_verification_bt:
                if (ConnectivityReceiver.isConnected()) {
                    sendVerificationCode();
                    displayContent(R.id.send_verification_bt);
                }
                break;
            case R.id.edit_tv2:
                stopThread();
                displayContent(R.id.edit_tv2);
                break;
            case R.id.resend_code_tv:
                if (ConnectivityReceiver.isConnected()) {
                    reSendVerificationCode();
                }
                break;
            case R.id.send_verification_bt2:
                editMobileNumber();
                break;
            case R.id.verify_bt:
                displayDialog(false);
                break;
            case R.id.cancel_verify_bt:
                displayDialog(false);
                stopBackPressedListener();
                navController.navigate(R.id.action_mobileVerificationFragment_to_orderFragment_back, bundleProducts);
                break;

            default:
                displayDialog(true);
                Log.d(TAG, "MEME: OnBackPressed");
                break;
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, R.string.ok, 0, null);
        }
    }

    @Override
    public void OnBackPressed() {
        displayDialog(true);
    }

    @Override
    public void onActionListener(View view, int id) {
        if(id == R.string.retry){
            if (ConnectivityReceiver.isConnected()) {
                displayProgressDialog(true);
                cartViewModel.validateMobileNumber(countryCodeName, mobileNumber, user.getId());
            }else {
                failedAddress(null);
            }

        }
    }
}
