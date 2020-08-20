package com.example.ecommerceapp.ui.more;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.AccountInterface;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentProfileBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ProfileFragment extends Fragment implements View.OnClickListener, AccountInterface,
        ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private MoreViewModel moreViewModel;
    private FragmentProfileBinding binding;
    private View root;
    private UserModel user, userUpdated;
    private String id, name, userName, email, imagePath;
    private int validated;
    private Utils utils;
    private Context context;
    private Activity activity;

    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();

        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
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

        binding.toolbar.setTitle(getString(R.string.title_profile));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);
        binding.setVisibleUnvalidated(true);

        moreViewModel =
                new ViewModelProvider(this).get(MoreViewModel.class);
        selectActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        updateUI();

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


    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
        }
    }

    private void observeLiveData(){
        moreViewModel.getValidateAccount().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                if(s != null) {
                    responseData(s);
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


    private void responseData(String response){
        String message = "";
        switch (response){
            case "Success":
                message = getString(R.string.validateAccountSuccess);
                Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).validateAccountUser(userUpdated);
                updateUI();
                break;
            case "Failed":
                message = getString(R.string.validateAccountField);
                break;
            case "RepeatedEmail":
                message = getString(R.string.validateAccountRepeated);
                break;

            default:

                break;
        }
        displaySnackBar(true, message, -2);
    }

    private void updateUI(){
        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();

        id = user.getId();
        name = user.getName();
        userName = user.getUserName();
        email = user.getEmail();
        imagePath = user.getImagePath();
        validated = user.getValidated();

        binding.nameTv.setText(name);
        binding.userNameTv.setText(userName);
        binding.emailTv.setText(email);
        GlideClient.loadProfileImage(context, imagePath, binding.userPictureIv);

        if(validated == 1){
            binding.setVisibleUnvalidated(false);
        }

        binding.editProfileIbt.setOnClickListener(this::onClick);
    }

    private void editProfile(){
        if(!binding.getVisibleUnvalidated()) {
            navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
        }else {
            displaySnackBar(true, getString(R.string.account_not_validated_info),-2);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.edit_profile_ibt){
            editProfile();
        }else {
            activity.onBackPressed();
        }
    }

    private void getFacebookData(JSONObject object){
        String name, email, imageProfilePath;
        if(object != null) {
            try {
                imageProfilePath = new URL("https://graph.facebook.com/" + object.getString("id") +
                        "/picture?width=250&height=250").toString();
                name = object.getString("first_name") + " " + object.getString("last_name");
                email = object.getString("email");
                validateAccount(name, email, imageProfilePath);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            displaySnackBar(true,null, 0);
        }
    }

    private void validateAccount(String name, String email, String imageProfilePath){
        userUpdated = new UserModel();
        userUpdated.setId(id);
        userUpdated.setEmail(email);
        userUpdated.setValidated(1);

        if (imagePath.isEmpty() ) {
            userUpdated.setImagePath(imageProfilePath);
        }else {
            userUpdated.setImagePath(imagePath);
        }

        displayProgressDialog(true);
        moreViewModel.validateAccount(userUpdated);
    }

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }
    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null){
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(activity.findViewById(R.id.containerProfile), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onGoogleListener(GoogleSignInAccount account) {
        validateAccount(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());
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
