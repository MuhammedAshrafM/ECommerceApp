package com.example.ecommerceapp.ui.orders;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentOrderDetailsBinding;
import com.example.ecommerceapp.databinding.FragmentOrdersBinding;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.OrderModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailsFragment#} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailsFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener{


    private NavController navController;
    private OrdersViewModel ordersViewModel;
    private FragmentOrderDetailsBinding binding;
    private ProductOrderAdapter adapter;
    private ArrayList<ProductModel> products;
    private ArrayList<String> productsCartedId;
    private View root;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private Context context;
    private Activity activity;
    private Utils utils;
    private String id, orderCode, orderTime, receiptTime, paymentOption, orderStatus;
    private int received, shippingFee;
    private Double subTotal;
    private DecimalFormat decimalFormat = new DecimalFormat();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "order";
    private static final String ARG_PARAM2 = "orderId";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";

    // TODO: Rename and change types of parameters
    private OrderModel order;
    private String orderId;

    public OrderDetailsFragment() {
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
            order = getArguments().getParcelable(ARG_PARAM1);
            orderId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false);
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

        binding.toolbar.setTitle(getString(R.string.title_order_details));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        menu = null;
        decimalFormat.applyPattern("#,###,###,###.##");

        handleUi();

        if(order != null){
            setOrderData();
            if (ConnectivityReceiver.isConnected()) {
                displayProgressDialog(true);
                ordersViewModel.getOrderDetails(id);
            } else {
                displaySnackBar(true, null);
            }
        }else {
            if (ConnectivityReceiver.isConnected()) {
                displayProgressDialog(true);
                ordersViewModel.getOrder(orderId);
            } else {
                displaySnackBar(true, null);
            }
        }
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

    private void handleUi(){
        binding.recyclerViewProductsOrder.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ProductOrderAdapter(context);
        binding.recyclerViewProductsOrder.setAdapter(adapter);

        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,0,0));
    }

    private void setOrderData(){
        id = order.getId();
        orderCode = id.substring(3);
        orderTime = order.getOrderTime();
        received = order.getReceived();
        paymentOption = order.getPaymentOption();
        subTotal = order.getSubTotal();
        shippingFee = order.getShippingFee();

        if(received == 0){
            binding.setOrderPrepared(true);
            orderStatus = getString(R.string.orderPrepared);
        }else {
            binding.setOrderPrepared(false);
            receiptTime = order.getReceiptTime();
            binding.orderReceiptTimeTv.setText(String.format(Locale.getDefault(),"%s %s", getString(R.string.receiptTime), receiptTime));
            orderStatus = String.format(Locale.getDefault(),"%s %s", getString(R.string.orderDelivered), "Muhammed Ashraf");
        }

        binding.orderStatusTv.setText(orderStatus);
        binding.orderCodeTv.setText(String.format(Locale.getDefault(),"%s %s", getString(R.string.orderCode), orderCode));
        binding.orderTimeTv.setText(orderTime);

        binding.totalPriceTv.setText(decimalFormat.format(subTotal) + " " + getString(R.string.egp));
        binding.paymentOptionTv.setText(paymentOption);

        if(shippingFee == 0){
            binding.shippingFeeTv.setText(getString(R.string.freeShipping));
        }else {
            binding.shippingFeeTv.setText(decimalFormat.format(shippingFee) + " " + getString(R.string.egp));
        }

    }

    private void observeLiveData(){
        ordersViewModel.getOrder().observe(getViewLifecycleOwner(), new Observer<OrderModel>() {
            @Override
            public void onChanged(OrderModel orderModel) {
                if(orderModel != null){
                    order = orderModel;
                    setOrderData();
                }
            }
        });

        ordersViewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null && productModels.size() > 0) {
                    products = productModels;
                    binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,products.size(),products.size()));
                    adapter.setList(products);
                }
            }
        });

        ordersViewModel.getAddress().observe(getViewLifecycleOwner(), new Observer<AddressModel>() {
            @Override
            public void onChanged(AddressModel addressModel) {
                if(addressModel != null) {
                    setAddressData(addressModel);
                }
            }
        });

        ordersViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null);
            }
        });
    }

    private void setAddressData(AddressModel address){
        binding.addressTv.setText(address.getAddress());
        binding.mobileNumberTv.setText(address.getMobileNumber());
        binding.keyCountryCp.setCountryForNameCode(address.getCountryCodeName());
    }
    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerOrderDetails), msg, R.string.ok, 0);
        utils.displaySnackBar(show);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(this.menu == null) {
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myCart){
            navController.navigate(R.id.action_orderDetailsFragment_to_navigation_cart);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        activity.onBackPressed();
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
            displaySnackBar(true, null);
        }
    }
}