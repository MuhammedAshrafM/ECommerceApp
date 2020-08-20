package com.example.ecommerceapp.ui.more;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.PasswordDialog;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.SortDialog;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentEditProfileBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment implements View.OnClickListener, ItemDialogClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener{

    private MoreViewModel moreViewModel;
    private FragmentEditProfileBinding binding;
    private View root;
    private UserModel user;
    private String id, name, userName, email, password, imagePath, imageProfilePath, token, userNamePattern, passwordPattern;
    private int validated;
    private Utils utils;
    private PasswordDialog dialog;
    private Uri imageUri;
    private Context context;
    private Activity activity;

    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final int SELECTED_PICTURE = 22, CROP_PICTURE = 55;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();

        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_edit_profile,container,false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(getString(R.string.title_profile_edit));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);
        binding.setVisibleEditPassword(false);
        moreViewModel =
                new ViewModelProvider(this).get(MoreViewModel.class);
        selectActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        userNamePattern = "(\\w)*";
        passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=-_])(?=\\S).*";

        updateUi();

        setOnClickListener();

        observeLiveData();

        setViewEditPasswordDialog();
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

    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
        }
    }

    private void observeLiveData(){
        moreViewModel.getEditAccount().observe(this, new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                displayProgressDialog(false);
                if(userModels != null && userModels.size() > 0) {
                    responseData(userModels);
                }
            }
        });

        moreViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0);
            }
        });
    }

    private void setOnClickListener(){
        binding.editBt.setOnClickListener(this);
        binding.editPasswordIbt.setOnClickListener(this);
        binding.editUserPictureIv.setOnClickListener(this);
        binding.editUserPictureIbt.setOnClickListener(this);
    }

    private void updateUi(){
        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();

        id = user.getId();
        name = user.getName();
        userName = user.getUserName();
        email = user.getEmail();
        password = user.getPassword();
        imagePath = user.getImagePath();
        token = user.getToken();
        validated = user.getValidated();

        binding.editNameEt.setText(name);
        binding.editUsernameEt.setText(userName);
        binding.editEmailEt.setText(email);
        binding.editPasswordEt.setText(password);
        GlideClient.loadProfileImage(context, imagePath, binding.editUserPictureIv);

        binding.editEmailEt.setFocusable(false);
        binding.editPasswordEt.setFocusable(false);

        if(!userName.isEmpty()){
            binding.editUsernameEt.setFocusable(false);
            binding.setVisibleEditPassword(true);
        }
    }
    private void responseData(ArrayList<UserModel> userModels){
        if(userModels.size() > 0){

            Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).setDataUser(userModels.get(0));
            user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();
            displaySnackBar(true, getString(R.string.editAccountSuccess), 0);
        }
        else {
            displaySnackBar(true, getString(R.string.editAccountRepeated), 0);
        }
    }


    private void setViewEditPasswordDialog(){
        dialog = dialog.getINSTANCE(context, activity, R.style.MaterialDialogSheet, this::onItemDialogClick, user);
    }

    private void editProfile(){
        if(validateEditProfile()){
            if (ConnectivityReceiver.isConnected()) {
                if(!TextUtils.isEmpty(imageProfilePath)){
                    imagePath = imageProfilePath;
                }
                UserModel user = new UserModel(id, name, userName, email, password, imagePath, token, validated);
                displayProgressDialog(true);
                moreViewModel.editAccount(user);
            }else {
                displaySnackBar(true, null, 0);
            }
        }
    }

    private void getData(){
        binding.editNameEt.setError(null);
        binding.editUsernameEt.setError(null);
        binding.addPasswordEt.setError(null);

        name = binding.editNameEt.getText().toString().trim();
        userName = binding.editUsernameEt.getText().toString().trim();
        if(binding.getVisibleEditPassword()){
            password = binding.editPasswordEt.getText().toString().trim();
        }else {
            password = binding.addPasswordEt.getText().toString().trim();
        }
    }

    private boolean validateEditProfile() {
        boolean cancel = false;
        View focusView = null;

        getData();

        if(TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(imageProfilePath)){
            displaySnackBar(true, getString(R.string.pictureField), -2);
        }
        else if(TextUtils.isEmpty(name)){
            binding.editNameEt.setError(getString(R.string.nameField));
            focusView = binding.editNameEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(userName)){
            binding.editUsernameEt.setError(getString(R.string.userNameField));
            focusView = binding.editUsernameEt;
            cancel = true;
        }
        else if(!userName.matches(userNamePattern)){
            binding.editUsernameEt.setError(getString(R.string.userNamePatternField));
            focusView = binding.editUsernameEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(password)){
            binding.addPasswordEt.setError(getString(R.string.passwordField));
            focusView = binding.addPasswordEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(password) < 6){
            binding.addPasswordEt.setError(getString(R.string.infoPassword));
            focusView = binding.addPasswordEt;
            cancel = true;
        }
        else if(!password.matches(passwordPattern)){
            binding.addPasswordEt.setError(getString(R.string.passwordPatternField));
            focusView = binding.addPasswordEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }


    private void compressImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStreamObject;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        imageProfilePath = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
    }

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);

    }
    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null){
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(activity.findViewById(R.id.containerEditProfile), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_bt:
                editProfile();
                break;

            case R.id.edit_userPicture_iv: case R.id.edit_userPicture_ibt:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,getString(R.string.selectImage)), SELECTED_PICTURE);
                break;

            case R.id.edit_password_ibt:
                dialog.show();
                break;

            default:
                activity.onBackPressed();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == SELECTED_PICTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    imageUri = data.getData();

                    Intent photoPickerCrop = new Intent("com.android.camera.action.CROP");
                    photoPickerCrop.setDataAndType(imageUri, "image/*");
                    photoPickerCrop.putExtra("crop", "true");
                    // indicate aspect of desired crop
                    photoPickerCrop.putExtra("aspectX", 1);
                    photoPickerCrop.putExtra("aspectY", 1);
                    // indicate output X and Y
                    photoPickerCrop.putExtra("outputX", 360);
                    photoPickerCrop.putExtra("outputY", 360);
                    // retrieve data on return
                    photoPickerCrop.putExtra("scaleUpIfNeeded", true);
                    photoPickerCrop.putExtra("return-data", true);

                    startActivityForResult(photoPickerCrop, CROP_PICTURE);
                } catch (ActivityNotFoundException ex) {

                }
            }
            else {
                displaySnackBar(true, getString(R.string.pictureField),-2);
            }
        }

        else if(requestCode == CROP_PICTURE){
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap selectedImage = bundle.getParcelable("data");
                binding.editUserPictureIv.setImageBitmap(selectedImage);
                compressImage(selectedImage);

            }
            else {
                displaySnackBar(true, getString(R.string.cropPictureField),-2);
            }
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }

    @Override
    public void onItemDialogClick(View view, int id) {
        if(id == R.string.editPassword){
            binding.editPasswordEt.setText(dialog.getPassword());
        }
    }
}
