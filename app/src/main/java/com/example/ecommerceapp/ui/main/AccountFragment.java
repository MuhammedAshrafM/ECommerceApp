package com.example.ecommerceapp.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.databinding.FragmentAccountBinding;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_UP = 99;
    private CallbackManager callbackManager;
    private AccountInterface accountInterface;
    private FragmentAccountBinding binding;
    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_account,container,false);

        root = binding.getRoot();
        binding.signUpGoogleBt.setOnClickListener(this);
        binding.signUpFacebookBt.setFragment(this);

        binding.signUpGoogleBt.setSize(SignInButton.SIZE_STANDARD);
        TextView textView = (TextView) binding.signUpGoogleBt.getChildAt(0);

        textView.setText(getString(R.string.continueGoogle));

        textView.setTextColor(Color.rgb(3,169,244));

        return root;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        accountInterface = (AccountInterface) getParentFragment();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        callbackManager = CallbackManager.Factory.create();

        binding.signUpFacebookBt.setPermissions(Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accountInterface.onProgressDialogListener(true);

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        accountInterface.onFacebookListener(object);
                        LoginManager.getInstance().logOut();
                    }
                });
                Bundle bundleParameters = new Bundle();
                bundleParameters.putString("fields","id,email,first_name,last_name");
                graphRequest.setParameters(bundleParameters);
                graphRequest.executeAsync();
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
                accountInterface.onFacebookListener(null);
            }
        });

    }

    private void handleSignUpResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            accountInterface.onGoogleListener(account);
            mGoogleSignInClient.signOut();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signUpResult:failed code=" + e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        accountInterface.onProgressDialogListener(false);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_UP) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignUpResult(task);
        }


    }


    private void selectGoogleAccount(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        accountInterface.onProgressDialogListener(true);
        startActivityForResult(signInIntent, RC_SIGN_UP);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_up_google_bt:
                selectGoogleAccount();
                break;

            default:

                break;
        }
    }
}
