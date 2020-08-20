package com.example.ecommerceapp.ui.cart;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
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
import com.example.ecommerceapp.data.MySuggestionProvider;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentCartBinding;
import com.example.ecommerceapp.pojo.AddressModel;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CartFragment extends Fragment implements View.OnClickListener, ItemClickListener,
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener, ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private CartViewModel cartViewModel;
    private FragmentCartBinding binding;
    private View root;
    private ArrayList<String> productsCartedId;
    private ArrayList<ProductModel> productsCarted;
    private ProductCartedAdapter adapter;
    private Utils utils;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private double subTotal = 0;
    private DecimalFormat decimalFormat = new DecimalFormat();
    private UserModel user;
    private Context context;
    private Activity activity;
    private boolean back = false;

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
                new ViewModelProvider(this).get(CartViewModel.class);

        decimalFormat.applyPattern("#,###,###,###.##");

        selectActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.recyclerViewProductsCarted.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ProductCartedAdapter(context, this);
        binding.recyclerViewProductsCarted.setAdapter(adapter);

        binding.swipeRefresh.setColorSchemeColors(Color.rgb(3,169,244),
                Color.rgb(3,169,244),
                Color.rgb(13,179,163));
        binding.swipeRefresh.setOnRefreshListener(this);

        getSharedPreferences();

        if (ConnectivityReceiver.isConnected()) {
            if(productsCartedId.size() > 0) {
                displayProgressDialog(true);
                cartViewModel.getProductsCarted(productsCartedId);
            }
        }else {
            displaySnackBar(true, null, 0);
        }

        binding.checkout.setOnClickListener(this);

        binding.subTotalPrice.setText(0 + " " + getString(R.string.egp));

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
        }else {
            binding.setPadding(false);
        }
    }

    private void observeLiveData(){
        cartViewModel.getProductsCarted().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null && productModels.size() > 0) {
                    productsCarted = productModels;
                    subTotal = 0;
                    adapter.setList(productsCarted);

                }
            }
        });

        cartViewModel.getAddresses().observe(getViewLifecycleOwner(), new Observer<ArrayList<AddressModel>>() {
            @Override
            public void onChanged(ArrayList<AddressModel> addressModels) {
                if(!back) {
                    displayProgressDialog(false);
                    if (addressModels != null && addressModels.size() > 0) {
                        Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).setAddressesSaved(addressModels);
                        navigateToOrderFragment();
                    } else {
                        navController.navigate(R.id.action_navigation_cart_to_addAddressFragment, getProductsCarted());
                    }
                    back = true;
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
    private void getSharedPreferences(){
        productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();
    }
    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }
    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerProductsCarted), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    private Bundle getProductsCarted() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("products", productsCarted);
        bundle.putStringArrayList("sellersId", adapter.getSellersId());
        bundle.putIntegerArrayList("counts", adapter.getCountProductsCarted());
        bundle.putDouble("subTotal", subTotal);

        return bundle;
    }

    private void navigateToOrderFragment(){
        navController.navigate(R.id.action_navigation_cart_to_orderFragment, getProductsCarted());
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
                        if(Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).getAddressesSaved().size() == 0) {
                            if (ConnectivityReceiver.isConnected()) {
                                back = false;
                                displayProgressDialog(true);
                                cartViewModel.getAddresses(user.getId());
                            }else {
                                displaySnackBar(true, null, 0);
                            }
                        }else {
                            navigateToOrderFragment();
                        }
                    }
                }
                break;

            default:
                activity.onBackPressed();
                break;

        }
    }


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {

        if(!carted){
            productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).removeProductCarted(productModel);
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
            if(subTotal != -1){
                this.subTotal = this.subTotal - subTotal;
                binding.subTotalPrice.setText(decimalFormat.format(this.subTotal) + " " + getString(R.string.egp));
            }else {
                displaySnackBar(true, getString(R.string.over_quantity), 0);
            }
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
                activity.getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = this.menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(activity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if(!binding.getPadding()) {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(s, null);
            Bundle bundle = new Bundle();
            bundle.putString("query", s);
            navController.navigate(R.id.action_navigation_cart_to_navigation_search, bundle);
        }
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

    @Override
    public void onRefresh() {

        if (ConnectivityReceiver.isConnected()) {
            if(productsCartedId.size() > 0) {
                binding.swipeRefresh.setRefreshing(true);
                getSharedPreferences();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.swipeRefresh.setRefreshing(false);
                        displayProgressDialog(true);
                        cartViewModel.getProductsCarted(productsCartedId);
                    }
                }, 3000);
            }
        }else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null, 0);
        }
    }
}
