package com.example.ecommerceapp.ui.orders;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentOrderBinding;
import com.example.ecommerceapp.databinding.FragmentOrdersBinding;
import com.example.ecommerceapp.databinding.FragmentReviewsBinding;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.OrderModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.home.HomeViewModel;
import com.example.ecommerceapp.ui.home.ReviewAdapter;
import com.example.ecommerceapp.ui.home.SubCategoryAdapter;

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

import java.util.ArrayList;
import java.util.Locale;

import static com.example.ecommerceapp.data.Utils.filter;

public class OrdersFragment extends Fragment implements SearchView.OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener, ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private OrdersViewModel ordersViewModel;
    private FragmentOrdersBinding binding;
    private ArrayList<OrderModel> orders;
    private OrdersAdapter adapter;
    private ArrayList<String> productsCartedId;
    private View root;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private UserModel user;
    private Context context;
    private Activity activity;
    private Utils utils;

    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);
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

        binding.toolbar.setTitle(getString(R.string.title_orders));
        ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        menu = null;

        handleUi();
        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            ordersViewModel.getOrders(user.getId());
        } else {
            displaySnackBar(true, null);
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
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(context));
        adapter = new OrdersAdapter(context);
        binding.recyclerViewOrders.setAdapter(adapter);

        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();
        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfOrdersAvailable,0,0));

        binding.swipeRefresh.setColorSchemeColors(Color.rgb(3,169,244),
                Color.rgb(3,169,244),
                Color.rgb(13,179,163));
        binding.swipeRefresh.setOnRefreshListener(this);
    }


    private void observeLiveData(){
        ordersViewModel.getOrders().observe(getViewLifecycleOwner(), new Observer<ArrayList<OrderModel>>() {
            @Override
            public void onChanged(ArrayList<OrderModel> orderModels) {
                displayProgressDialog(false);
                if (orderModels != null && orderModels.size() > 0) {
                    orders = orderModels;
                    binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfOrdersAvailable,orders.size(),orders.size()));
                    adapter.setList(orders);
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

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerOrders), msg, R.string.ok, 0);
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
            navController.navigate(R.id.action_navigation_orders_to_navigation_cart);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        if (ConnectivityReceiver.isConnected()) {
            binding.swipeRefresh.setRefreshing(true);
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefresh.setRefreshing(false);
                    displayProgressDialog(true);
                    ordersViewModel.getOrders(user.getId());
                }
            }, 3000);

        } else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
