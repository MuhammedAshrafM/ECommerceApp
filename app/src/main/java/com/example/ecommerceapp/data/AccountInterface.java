package com.example.ecommerceapp.data;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

public interface AccountInterface {

    public void onGoogleListener(GoogleSignInAccount account);

    public void onFacebookListener(JSONObject object);

    public void onProgressDialogListener(boolean show);
}
