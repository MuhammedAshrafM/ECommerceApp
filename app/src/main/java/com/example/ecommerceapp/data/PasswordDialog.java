package com.example.ecommerceapp.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.databinding.LayoutEditPasswordBinding;
import com.example.ecommerceapp.databinding.LayoutFilterPriceBinding;
import com.example.ecommerceapp.pojo.UserModel;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.Map;

public class PasswordDialog extends Dialog implements View.OnClickListener {

    private LayoutEditPasswordBinding binding;
    private View view;
    private Activity activity;
    private Context context;
    private UserModel user;
    private String passwordOld, passwordNew, passwordPattern;
    private ItemDialogClickListener listener;
    private static PasswordDialog INSTANCE;

    private PasswordDialog(@NonNull Context context, Activity activity, int themeResId,
                           ItemDialogClickListener listener) {
        super(context, themeResId);
        this.context = context;
        this.activity = activity;
        this.listener = listener;
    }
    private PasswordDialog(@NonNull Context context, Activity activity, int themeResId,
                           ItemDialogClickListener listener, UserModel userModel) {
        super(context, themeResId);
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.user = userModel;
    }

    public synchronized static PasswordDialog getINSTANCE(Context context, Activity activity, int themeResId,
                                                          ItemDialogClickListener listener, UserModel userModel){

        if(userModel !=null) {
            INSTANCE = new PasswordDialog(context, activity, themeResId, listener, userModel);
        }else {
            INSTANCE = new PasswordDialog(context, activity, themeResId, listener);
        }
            INSTANCE.setCancelable(true);
            INSTANCE.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            INSTANCE.getWindow().setGravity(Gravity.TOP);
            INSTANCE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            INSTANCE.setCanceledOnTouchOutside(false);

        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = activity.getLayoutInflater().inflate(R.layout.layout_edit_password, null);
        binding = LayoutEditPasswordBinding.bind(view);
        setContentView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=-_])(?=\\S).*";

        if(user == null){
            binding.setVisible(false);
        }else {
            binding.setVisible(true);
        }
        binding.editPasswordBt.setOnClickListener(this);
        binding.cancelBt.setOnClickListener(this);
    }

    private void editPassword(View view){
        if(validateEditPassword()){
            setListen(view, R.string.editPassword);
        }
    }
    private void forgetPassword(View view){
        if(validateForgetPassword()){
            setListen(view, R.string.title_forget);
        }
    }

    private void getPasswordData(){
        binding.oldPasswordEt.setError(null);
        binding.newPasswordEt.setError(null);

        passwordOld = binding.oldPasswordEt.getText().toString().trim();
        passwordNew = binding.newPasswordEt.getText().toString().trim();
    }

    private boolean validateForgetPassword() {
        boolean cancel = false;
        View focusView = null;

        getPasswordData();

        if(TextUtils.isEmpty(passwordNew)){
            binding.newPasswordEt.setError(context.getString(R.string.passwordField));
            focusView = binding.newPasswordEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(passwordNew) < 6){
            binding.newPasswordEt.setError(context.getString(R.string.infoPassword));
            focusView = binding.newPasswordEt;
            cancel = true;
        }
        else if(!passwordNew.matches(passwordPattern)){
            binding.newPasswordEt.setError(context.getString(R.string.passwordPatternField));
            focusView = binding.newPasswordEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    private boolean validateEditPassword() {
        boolean cancel = false;
        View focusView = null;

        getPasswordData();

        if(TextUtils.isEmpty(passwordOld)){
            binding.oldPasswordEt.setError(context.getString(R.string.passwordField));
            focusView = binding.oldPasswordEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(passwordNew)){
            binding.newPasswordEt.setError(context.getString(R.string.passwordField));
            focusView = binding.newPasswordEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(passwordNew) < 6){
            binding.newPasswordEt.setError(context.getString(R.string.infoPassword));
            focusView = binding.newPasswordEt;
            cancel = true;
        }
        else if(!passwordNew.matches(passwordPattern)){
            binding.newPasswordEt.setError(context.getString(R.string.passwordPatternField));
            focusView = binding.newPasswordEt;
            cancel = true;
        }
        else if(!passwordOld.equals(user.getPassword())){
            binding.oldPasswordEt.setError(context.getString(R.string.incorrectPassword));
            focusView = binding.oldPasswordEt;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    public String getPassword() {
        return passwordNew;
    }

    private void setListen(View view, int id){
        dismiss();
        listener.onItemDialogClick(view, id);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_password_bt:
                if(user == null){
                    forgetPassword(view);
                }else {
                    editPassword(view);
                }
                break;

            case R.id.cancel_bt:
                dismiss();
                break;
        }
    }

}
