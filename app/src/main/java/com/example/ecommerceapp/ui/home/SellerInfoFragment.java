package com.example.ecommerceapp.ui.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
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
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentProductsBinding;
import com.example.ecommerceapp.databinding.FragmentSellerInfoBinding;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.SellerModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerInfoFragment extends Fragment implements View.OnClickListener, ItemClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private FragmentSellerInfoBinding binding;
    private PagerTabAdapter adapter;
    private ArrayList<String> productsCartedId;
    private Utils utils;
    private View root;
    private Menu menu;
    private MenuItem cartMenuItem;
    private Dialog dialog;
    private Button mapBt, cancelBt;
    private TextView sellerNameTv, sellerAddressTv;
    private Context context;
    private Activity activity;
    private double latitude, longitude;
    private boolean resume = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String ARG_PARAM1 = "seller";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private SellerModel seller;
    private String mParam2;

    public SellerInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerInfoFragment newInstance(String param1, String param2) {
        SellerInfoFragment fragment = new SellerInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MMMM onCreate: ");
        activity = getActivity();
        context = getContext();

        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            seller = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_seller_info,container,false);
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

        binding.toolbar.setTitle(getString(R.string.title_seller));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);
        menu = null;
        selectActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new PagerTabAdapter(getChildFragmentManager(), seller, this);
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

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

    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
        }
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerSellerInfo), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    private void setAlertDialog(){
        View view = getLayoutInflater().inflate(R.layout.layout_seller_info, null);
        sellerNameTv = view.findViewById(R.id.sellerName_tv);
        sellerAddressTv = view.findViewById(R.id.sellerAddress_tv);
        mapBt = view.findViewById(R.id.map_bt);
        cancelBt = view.findViewById(R.id.cancel_bt);

        mapBt.setOnClickListener(this);
        cancelBt.setOnClickListener(this);

        latitude = Double.valueOf(seller.getLocationLatitude());
        longitude = Double.valueOf(seller.getLocationLongitude());

        sellerNameTv.setText(seller.getName());
        sellerAddressTv.setText(getAddress(latitude, longitude));


        dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(false);
    }

    private String getAddress(double latitude, double longitude){
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude,longitude, 1);
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                return address;
            }

        } catch (Exception e) {

        }

        return "";
    }

    private void openMap(){
        dialog.dismiss();
        String uri = String.format(Locale.getDefault(), "geo:%f,%f", latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.map_bt:
                openMap();
                break;

            case R.id.cancel_bt:
                dialog.dismiss();
                break;

            default:
                activity.onBackPressed();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(this.menu == null) {
            binding.toolbar.inflateMenu(R.menu.info_menu);
            binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });

            this.menu = binding.toolbar.getMenu();
            cartMenuItem = this.menu.findItem(R.id.myCart);

            productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(context, productsCartedId.size(),R.drawable.ic_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.myCart:
                navController.navigate(R.id.action_sellerInfoFragment_to_navigation_cart);
                break;

            case R.id.info:
                dialog.show();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {
        cartMenuItem.setIcon(Utils.convertLayoutToImage(context, newSize, R.drawable.ic_cart));
    }

    @Override
    public void onCartView(double subTotal, boolean added) {

    }

    @Override
    public void onWishClick(View view, ProductModel productModel, boolean wished) {

    }

}