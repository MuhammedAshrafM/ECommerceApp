package com.example.ecommerceapp.ui.more;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.MyNotification;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentMoreBinding;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.main.MainActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MoreFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener,
        ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private MoreViewModel moreViewModel;
    private FragmentMoreBinding binding;
    private ArrayList<String> productsCartedId, productsWishedId;
    private View root;
    private Utils utils;
    private UserModel user;
    private Dialog dialog;
    private Button logOutBt, cancelLogOutBt;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private Context context;
    private Activity activity;

    private static final String PREFERENCES_PRODUCTS_WISHED = "PRODUCTS_WISHED";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final String PREFERENCES_ADDRESSES_SAVED = "ADDRESSES_SAVED";

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
        moreViewModel =
                new ViewModelProvider(this).get(MoreViewModel.class);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_more,container,false);
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

        binding.toolbar.setTitle(getString(R.string.title_menu));
    }


    @Override
    public void onStart() {
        super.onStart();

        productsWishedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_WISHED).getProductsWished();
        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();

        binding.userNameTv.setText(user.getName());
        GlideClient.loadProfileImage(context, user.getImagePath(), binding.userPictureIv);

        binding.userProfileLila.setOnClickListener(this);
        binding.wishListBt.setOnClickListener(this);
        binding.logOutBt.setOnClickListener(this);

        setAlertDialog();
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

    private void setAlertDialog(){
        View view = getLayoutInflater().inflate(R.layout.layout_log_out, null);
        logOutBt = view.findViewById(R.id.ok_bt);
        cancelLogOutBt = view.findViewById(R.id.cancel_bt);

        logOutBt.setOnClickListener(this);
        cancelLogOutBt.setOnClickListener(this);

        dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(false);
    }
    private void logOut(){
        Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).deleteSharedPreferencesData();
        MyNotification.getINSTANCE(context).cancelAllNotification();
        startActivity(new Intent(context, MainActivity.class));
        activity.finish();
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null){
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(activity.findViewById(R.id.containerMore), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.userProfile_lila:
                navController.navigate(R.id.action_navigation_more_to_profileFragment);
                break;

            case R.id.wishList_bt:
                navController.navigate(R.id.action_navigation_more_to_wishFragment);
                break;

            case R.id.logOut_bt:
                dialog.show();
                break;

            case R.id.ok_bt:
                logOut();
                break;

            case R.id.cancel_bt:
                dialog.dismiss();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        binding.toolbar.inflateMenu(R.menu.search_menu);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        this.menu = binding.toolbar.getMenu();
        cartMenuItem = this.menu.findItem(R.id.myCart);
        SearchManager searchManager = (SearchManager)
                activity.getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = this.menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(activity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
        cartMenuItem.setIcon(Utils.convertLayoutToImage(context,productsCartedId.size(),R.drawable.ic_cart));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.myCart){
            navController.navigate(R.id.action_navigation_more_to_navigation_cart);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }
}
