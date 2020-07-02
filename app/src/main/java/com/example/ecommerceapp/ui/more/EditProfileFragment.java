package com.example.ecommerceapp.ui.more;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
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
import androidx.lifecycle.ViewModelProviders;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener{

    private MoreViewModel moreViewModel;
    private FragmentEditProfileBinding binding;
    private View root;
    private UserModel user, userUpdated;
    private String id, name, userName, email, passwordOld, passwordNew, imagePath, imageProfilePath;
    private int validated;
    private Utils utils;
    private AlertDialog alertDialog;
    private TextInputEditText oldPasswordEt, newPasswordEt;
    private Button editPasswordBt;
    private Uri imageUri;

    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final int SELECTED_PICTURE = 22, CROP_PICTURE = 55;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
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

        moreViewModel =
                ViewModelProviders.of(this).get(MoreViewModel.class);

        user = Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).getDataUser();

        id = user.getId();
        name = user.getName();
        userName = user.getUserName();
        email = user.getEmail();
        passwordOld = user.getPassword();
        imagePath = user.getImagePath();
        validated = user.getValidated();

        binding.editNameEt.setText(name);
        binding.editUsernameEt.setText(userName);
        binding.editEmailEt.setText(email);
        binding.editPasswordEt.setText(passwordOld);
        GlideClient.loadProfileImage(getContext(), imagePath, binding.editUserPictureIv);

        binding.editEmailEt.setFocusable(false);
        binding.editPasswordEt.setFocusable(false);

        if(!userName.isEmpty()){
            binding.editUsernameEt.setFocusable(false);
            binding.textInputAddPassword.setVisibility(View.GONE);
            binding.textInputEditPassword.setVisibility(View.VISIBLE);
        }

        binding.editBt.setOnClickListener(this);
        binding.editPasswordEt.setOnClickListener(this);
        binding.editUserPictureIv.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    private void responseData(ArrayList<UserModel> userModels){
        if(userModels.size() > 0){

            Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).setDataUser(userModels.get(0));
            user = Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).getDataUser();
            displaySnackBar(true, getString(R.string.editAccountSuccess), 0);
        }
        else {
            displaySnackBar(true, getString(R.string.editAccountRepeated), 0);
        }
    }

    private void setViewEditPasswordDialog(){
        View view = getLayoutInflater().inflate(R.layout.layout_edit_password, null);
        oldPasswordEt = view.findViewById(R.id.old_password_et);
        newPasswordEt = view.findViewById(R.id.new_password_et);
        editPasswordBt = view.findViewById(R.id.edit_password_bt);
        editPasswordBt.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        alertDialog = builder.create();

        alertDialog.show();
    }

    private void editPassword(){
        if(validateEditPassword()){
            alertDialog.dismiss();
            binding.editPasswordEt.setText(passwordNew);
        }else {
            passwordOld = user.getPassword();
        }
    }
    private void getPasswordData(){
        oldPasswordEt.setError(null);
        newPasswordEt.setError(null);

        passwordOld = oldPasswordEt.getText().toString().trim();
        passwordNew = newPasswordEt.getText().toString().trim();
    }

    private boolean validateEditPassword() {
        boolean cancel = false;
        View focusView = null;

        getPasswordData();

        if(TextUtils.isEmpty(passwordOld)){
            oldPasswordEt.setError(getString(R.string.passwordField));
            focusView = oldPasswordEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(passwordNew)){
            newPasswordEt.setError(getString(R.string.passwordField));
            focusView = newPasswordEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(passwordNew) < 6){
            newPasswordEt.setError(getString(R.string.infoPassword));
            focusView = newPasswordEt;
            cancel = true;
        }
        else if(!passwordOld.equals(user.getPassword())){
            oldPasswordEt.setError(getString(R.string.incorrectPassword));
            focusView = oldPasswordEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void editProfile(){
        if(validateEditProfile()){
            if (ConnectivityReceiver.isConnected()) {
                if(!TextUtils.isEmpty(imageProfilePath)){
                    imagePath = imageProfilePath;
                }
                UserModel user = new UserModel(id, name, userName, email, passwordOld, imagePath, validated);
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
        if(binding.textInputAddPassword.getVisibility() == View.VISIBLE){
            passwordOld = binding.addPasswordEt.getText().toString().trim();

        }else {

            passwordOld = binding.editPasswordEt.getText().toString().trim();

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
        else if(TextUtils.isEmpty(passwordOld)){
            binding.addPasswordEt.setError(getString(R.string.passwordField));
            focusView = binding.addPasswordEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(passwordOld) < 6){
            binding.addPasswordEt.setError(getString(R.string.infoPassword));
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
        if(show){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.GONE);
        }

    }
    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null){
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(getContext());
        utils.snackBar(getActivity().findViewById(R.id.containerEditProfile), msg, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_bt:
                editProfile();
                break;

            case R.id.edit_userPicture_iv:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,getString(R.string.selectImage)), SELECTED_PICTURE);
                break;

            case R.id.edit_password_et:
                setViewEditPasswordDialog();
                break;

            case R.id.edit_password_bt:
                editPassword();
                break;

            default:
                getActivity().onBackPressed();
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
//            else {
//                Toast.makeText(getContext(),  getString(R.string.notSelectedPicture), Toast.LENGTH_LONG).show();
//            }
        }

        else if(requestCode == CROP_PICTURE){
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap selectedImage = bundle.getParcelable("data");
                binding.editUserPictureIv.setImageBitmap(selectedImage);
                compressImage(selectedImage);

            }
//            else {
//                Toast.makeText(EditProfileActivity.this,  getString(R.string.notCropPicture), Toast.LENGTH_LONG).show();
//            }
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }
}
