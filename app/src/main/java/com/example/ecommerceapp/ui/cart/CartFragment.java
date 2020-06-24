package com.example.ecommerceapp.ui.cart;

import android.app.SearchManager;
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
import android.widget.SearchView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentCartBinding;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
import androidx.recyclerview.widget.LinearLayoutManager;

public class CartFragment extends Fragment implements View.OnClickListener, ItemClickListener,
        SearchView.OnQueryTextListener, ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private CartViewModel cartViewModel;
    private FragmentCartBinding binding;
    private View root;
    private ArrayList<String> productsCartedId;
    private ArrayList<ProductModel> productsCarted;
    private String[] productIds;
    private ProductCartedAdapter adapter;
    private Utils utils;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private double subTotal = 0;
    private DecimalFormat decimalFormat = new DecimalFormat();
    private UserModel user;

    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_cart,container,false);
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

        binding.toolbar.setTitle(getString(R.string.title_cart));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);

        decimalFormat.applyPattern("#,###,###,###.##");

        binding.recyclerViewProductsCarted.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductCartedAdapter(getContext(), this);
        binding.recyclerViewProductsCarted.setAdapter(adapter);

        productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
        if (ConnectivityReceiver.isConnected()) {
            if(productsCartedId.size() > 0) {
                displayProgressDialog(true);
                productIds = new String[productsCartedId.size()];
                cartViewModel.getProductsCarted(productsCartedId.toArray(productIds));
            }
        }else {
            displaySnackBar(true, null, 0);
        }

        binding.checkout.setOnClickListener(this);
        user = Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).getDataUser();

        binding.subTotalPrice.setText(0 + " " + getString(R.string.egp));
    }

    @Override
    public void onStart() {
        super.onStart();

        cartViewModel.getProductsCarted().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null && productModels.size() > 0) {
                    productsCarted = productModels;
                    subTotal = 0;
                    adapter.setList(productsCarted);
                    displaySnackBar(false, null, 0);

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

    private void displayProgressDialog(boolean show){
        if(show){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(getContext());
        utils.snackBar(root.findViewById(R.id.containerProductsCarted), msg, duration);
        utils.displaySnackBar(show);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.checkout:
                if(user.getValidated() == 0){
                    displaySnackBar(true, getString(R.string.buying_info), -2);
                    navController.navigate(R.id.action_navigation_cart_to_profileFragment);
                }else {
                    if(subTotal == 0){
                        displaySnackBar(true, getString(R.string.cartEmpty), -2);
                    }else {

                    }
                    // payment method
                }
                break;

            default:
                getActivity().onBackPressed();
                break;

        }

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {

        if(!carted){
            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).removeProductCarted(productModel);
            productsCarted.remove(productModel);
            adapter.setList(productsCarted);
            displaySnackBar(true, getString(R.string.unCarted), -1);
        }
    }

    @Override
    public void onCartView(double subTotal, boolean added) {
        if(subTotal == 0){
            this.subTotal = 0;
        }
        if(added){
            this.subTotal = this.subTotal + subTotal;
            binding.subTotalPrice.setText(decimalFormat.format(this.subTotal) + " " + getString(R.string.egp));
        }else {
            this.subTotal = this.subTotal - subTotal;
            binding.subTotalPrice.setText(decimalFormat.format(this.subTotal) + " " + getString(R.string.egp));
        }
    }

    @Override
    public void onWishClick(View view, ProductModel productModel, boolean wished) {

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
        cartMenuItem.setVisible(false);
        SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = this.menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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
