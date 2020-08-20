package com.example.ecommerceapp.ui.home;

import android.app.Activity;
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

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentSubCategoryBinding;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.example.ecommerceapp.data.Utils.filter;

public class SubCategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, SearchView.OnQueryTextListener, ItemClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private HomeViewModel homeViewModel;
    private FragmentSubCategoryBinding binding;
    private ArrayList<String> productsCartedId;
    private ArrayList<SubCategoryModel> categories;
    private ArrayList<ProductModel> products;
    private SubCategoryAdapter adapter;
    private View root;
    private LinearLayoutManager layoutManagerRecycler;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Utils utils;
    private Menu menu;
    private SearchView searchView;
    private MenuItem searchMenuItem, cartMenuItem;
    private Context context;
    private Activity activity;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "categoryId";
    private static final String ARG_PARAM2 = "categoryName";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";

    // TODO: Rename and change types of parameters
    private String categoryId;
    private String categoryName;

    public SubCategoryFragment() {
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
            categoryId = getArguments().getString(ARG_PARAM1);
            categoryName = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sub_category,container,false);
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

        binding.toolbar.setTitle(categoryName);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
    }


    @Override
    public void onStart() {
        super.onStart();

        menu = null;
        manager = null;

        handleUi();

        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            if (manager == null) {
                homeViewModel.getProductsAndSubCategories(categoryId);
            }
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


    private void observeLiveData(){

        homeViewModel.getSubCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<SubCategoryModel>>() {
            @Override
            public void onChanged(ArrayList<SubCategoryModel> subCategoryModels) {
                if (subCategoryModels != null && subCategoryModels.size() > 0) {
                    categories = subCategoryModels;
                    adapter.setSubCategoryList(categories);
                }

            }
        });
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null && productModels.size() > 0) {
                    products = productModels;
                    displayProducts(products, false);
                }
            }
        });


        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null);
            }
        });
    }

    private void handleUi(){
        layoutManagerRecycler = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewSubCategories.setHasFixedSize(true);
        binding.recyclerViewSubCategories.setLayoutManager(layoutManagerRecycler);
        adapter = new SubCategoryAdapter(context);
        binding.recyclerViewSubCategories.setAdapter(adapter);

        binding.swipeRefresh.setColorSchemeColors(Color.rgb(3,169,244),
                Color.rgb(3,169,244),
                Color.rgb(13,179,163));
        binding.swipeRefresh.setOnRefreshListener(this);
    }
    private void displayProducts(ArrayList<ProductModel> productModels, boolean search){
        if(manager == null || search){
            manager = getChildFragmentManager();
            transaction = manager.beginTransaction();
            transaction.replace(R.id.container_fragment, ProductsFragment.newInstance(productModels, null, this));
            transaction.commit();
        }
    }

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerSubCategories), msg, R.string.ok, 0);
        utils.displaySnackBar(show);
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
                    homeViewModel.getProductsAndSubCategories(categoryId);
                    manager = null;
                }
            }, 3000);

        } else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null);
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(categories != null && this.menu == null) {
            binding.toolbar.inflateMenu(R.menu.search_menu);
            binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });

            this.menu = binding.toolbar.getMenu();
            cartMenuItem = this.menu.findItem(R.id.myCart);
            searchMenuItem = this.menu.findItem(R.id.search);

            searchView = (SearchView)searchMenuItem.getActionView();
            searchView.setQueryHint("");
            searchView.setOnQueryTextListener(this);

            productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(context,productsCartedId.size(),R.drawable.ic_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myCart){
            navController.navigate(R.id.action_subCategoryFragment_to_navigation_cart);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        activity.onBackPressed();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (products != null) {
            searchView.setQuery(query, false);
            ArrayList<ProductModel> productModels = filter(products, query);
            if(productModels != null){
                displayProducts(productModels, true);
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (products != null) {
            ArrayList<ProductModel> productModels = filter(products, newText);

            if(productModels != null){
                displayProducts(productModels, true);
            }
        }
        return true;
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null);
        }
    }
}
