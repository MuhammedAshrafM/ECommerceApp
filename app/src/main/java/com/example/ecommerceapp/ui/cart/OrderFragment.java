package com.example.ecommerceapp.ui.cart;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentOrderBinding;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SellerModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements View.OnClickListener, ItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, ConnectivityReceiver.ConnectivityReceiveListener{


    private NavController navController;
    private CartViewModel cartViewModel;
    private FragmentOrderBinding binding;
    private View root;
    private Utils utils;
    private Context context;
    private Activity activity;
    private AddressModel addressModel;
    private DecimalFormat decimalFormat = new DecimalFormat();
    private ProductOrderedAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "products";
    private static final String ARG_PARAM2 = "sellersId";
    private static final String ARG_PARAM3 = "counts";
    private static final String ARG_PARAM4 = "subTotal";
    private static final String PREFERENCES_ADDRESSES_SAVED = "ADDRESSES_SAVED";

    // TODO: Rename and change types of parameters
    private ArrayList<ProductModel> products;
    private ArrayList<String> sellersId;
    private ArrayList<Integer> counts;
    private ArrayList<SellerModel> sellers;
    private double subTotal;
    private double shippingFee;
    private double grandTotal;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressSelectedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(ArrayList<ProductModel> param1, ArrayList<String> param2,
                                            ArrayList<Integer> param3, double param4) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, param1);
        args.putStringArrayList(ARG_PARAM2, param2);
        args.putIntegerArrayList(ARG_PARAM3, param3);
        args.putDouble(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();
        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            products = getArguments().getParcelableArrayList(ARG_PARAM1);
            sellersId = getArguments().getStringArrayList(ARG_PARAM2);
            counts = getArguments().getIntegerArrayList(ARG_PARAM3);
            subTotal = getArguments().getDouble(ARG_PARAM4);
            grandTotal = subTotal;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);
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

        binding.toolbar.setTitle(getString(R.string.title_order));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        decimalFormat.applyPattern("#,###,###,###.##");

        selectActivity();
    }


    @Override
    public void onStart() {
        super.onStart();
        getSharedPreferences();

        binding.recyclerViewProductsOrder.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ProductOrderedAdapter(context, this);
        binding.recyclerViewProductsOrder.setAdapter(adapter);

        binding.swipeRefresh.setColorSchemeColors(Color.rgb(3,169,244),
                Color.rgb(3,169,244),
                Color.rgb(13,179,163));
        binding.swipeRefresh.setOnRefreshListener(this);


        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            cartViewModel.getSelectedSellers(sellersId);
        }else {
            displaySnackBar(true, null, 0);
        }

        setOnClickListener();

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
        cartViewModel.getSelectedSellers().observe(getViewLifecycleOwner(), new Observer<ArrayList<SellerModel>>() {
            @Override
            public void onChanged(ArrayList<SellerModel> sellerModels) {
                displayProgressDialog(false);
                if (sellerModels != null && sellerModels.size() > 0) {
                    shippingFee = 0;
                    grandTotal = subTotal;
                    sellers = sellerModels;
                    adapter.setList(products, counts, sellers);
                }
            }
        });

        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0);
            }
        });
    }

    private void setOnClickListener(){
        binding.changeAddressTv.setOnClickListener(this::onClick);
        binding.paymentMethod.setOnClickListener(this::onClick);
        binding.addressSelectedCola.setOnClickListener(this::onClick);
    }

    private void getSharedPreferences(){
        addressModel = Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).getAddressSavedSelected();

        binding.addressTv.setText(addressModel.getAddress());
        binding.mobileNumberTv.setText(addressModel.getMobileNumber());
        binding.keyCountryCp.setCountryForNameCode(addressModel.getCountryCodeName());
    }
    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerOrder), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    private Bundle getBundleValues() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("products", products);
        bundle.putStringArrayList("sellersId", sellersId);
        bundle.putIntegerArrayList("counts", counts);
        bundle.putDouble("subTotal", subTotal);

        return bundle;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.change_address_tv:
                navController.navigate(R.id.action_orderFragment_to_addressesSavedFragment, getBundleValues());
                break;

            case R.id.paymentMethod:
                if(addressModel.getMobileNumberValidated() == 1){
                    Bundle bundle = new Bundle();
                    bundle.putDouble(getString(R.string.subTotal), subTotal);
                    bundle.putDouble(getString(R.string.shippingPlus), shippingFee);
                    bundle.putDouble(getString(R.string.grandTotal), grandTotal);
                    bundle.putIntegerArrayList("counts", counts);
                    navController.navigate(R.id.action_orderFragment_to_paymentMethodFragment, bundle);
                }else {
                    displaySnackBar(true, getString(R.string.verificationRequireMessage), -2);
                    navController.navigate(R.id.action_orderFragment_to_mobileVerificationFragment, getBundleValues());
                }

                break;

            case R.id.address_selected_cola:
                Bundle bundle = getBundleValues();
                bundle.putParcelable("address", addressModel);
                navController.navigate(R.id.action_orderFragment_to_addAddressFragment, bundle);
                break;

            default:
                activity.onBackPressed();
                break;
        }
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

    }

    @Override
    public void onCartView(double subTotal, boolean added) {

        shippingFee += subTotal;
        grandTotal += subTotal;
        binding.subTotalPrice.setText(decimalFormat.format(this.subTotal) + " " + getString(R.string.egp));
        binding.shippingPrice.setText(decimalFormat.format(shippingFee) + " " + getString(R.string.egp));
        binding.grandTotalPrice.setText(decimalFormat.format(this.grandTotal) + " " + getString(R.string.egp));
    }

    @Override
    public void onWishClick(View view, ProductModel productModel, boolean wished) {

    }

    @Override
    public void onRefresh() {
        if (ConnectivityReceiver.isConnected()) {
                binding.swipeRefresh.setRefreshing(true);
                getSharedPreferences();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.swipeRefresh.setRefreshing(false);
                        displayProgressDialog(true);
                        cartViewModel.getSelectedSellers(sellersId);
                    }
                }, 3000);

        }else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null, 0);
        }
    }
}
