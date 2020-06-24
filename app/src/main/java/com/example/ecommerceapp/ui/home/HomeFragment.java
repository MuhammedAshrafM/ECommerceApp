package com.example.ecommerceapp.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
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
import com.example.ecommerceapp.databinding.FragmentHomeBinding;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener,
        ConnectivityReceiver.ConnectivityReceiveListener, ItemClickListener, ViewPager.OnPageChangeListener{

    private NavController navController;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private CategoryAdapter adapter;
    private SubCategoryPagerAdapter sCPAdapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<CategoryModel> categories;
    private ArrayList<SubCategoryModel> subCategories;
    private ArrayList<String> productsCartedId;
    private Utils utils;
    private View root;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private UserModel user;
    private Handler handler;
    private Runnable runnable;
    private int pageNumber;
    private int delay = 2000;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
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

        binding.toolbar.setTitle(getString(R.string.title_home));
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);


        gridLayoutManager = new GridLayoutManager(getContext(),2);
        binding.recyclerViewCategories.setHasFixedSize(true);
        binding.recyclerViewCategories.setLayoutManager(gridLayoutManager);

        adapter = new CategoryAdapter(getContext());
        binding.recyclerViewCategories.setAdapter(adapter);

        user = Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).getDataUser();

        if(categories == null) {
            if (ConnectivityReceiver.isConnected()) {
                displayProgressDialog(true);
                homeViewModel.getMainCategories(user.getId());
                homeViewModel.getSubCategoriesOffer();
            } else {
                displaySnackBar(true);
            }
        }

        binding.viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        handler = new Handler();
        pageNumber = 0;
        manager = null;

        runnable = new Runnable() {
            public void run() {
                if (sCPAdapter.getCount() == pageNumber) {
                    pageNumber = 0;
                }

                binding.viewPager.setCurrentItem(pageNumber++, true);
                handler.postDelayed(this, delay);
            }
        };

        subCategories = new ArrayList<>();
        sCPAdapter = new SubCategoryPagerAdapter(getContext(), subCategories);
        binding.viewPager.setAdapter(sCPAdapter);

        homeViewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {

                if(productModels != null && productModels.size() > 0){
                    displayProducts(productModels);
                }
            }
        });

        homeViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<CategoryModel>>() {
            @Override
            public void onChanged(ArrayList<CategoryModel> categoryModels) {
                if (categoryModels != null && categoryModels.size() > 0) {
                    categories = categoryModels;
                    adapter.setList(categoryModels);
                }
            }
        });

        homeViewModel.getSubCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<SubCategoryModel>>() {
            @Override
            public void onChanged(ArrayList<SubCategoryModel> subCategoryModels) {
                if (subCategoryModels != null && subCategoryModels.size() > 0) {
                    subCategories = subCategoryModels;
                }
            }
        });

        homeViewModel.getInfoSubCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<Map<String, Float>>>() {
            @Override
            public void onChanged(ArrayList<Map<String, Float>> maps) {
                displayProgressDialog(false);
                if (maps != null && maps.size() > 0) {
                    binding.constraintLayout.setVisibility(View.VISIBLE);
                    sCPAdapter.setList(subCategories, maps);
                }else {
                    handler.removeCallbacks(runnable);
                }
            }
        });

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true);
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

        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void displayProducts(ArrayList<ProductModel> productModels){
        if(manager == null){
            manager = getChildFragmentManager();
            transaction = manager.beginTransaction();
            transaction.replace(R.id.container_fragment, SpecialProductsFragment.newInstance(productModels, getString(R.string.recentlyViewed), this));
            transaction.commit();
        }
    }

    private void displayProgressDialog(boolean show) {
        if (show) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }

    }
    private void displaySnackBar(boolean show){
        if(utils == null){
            String message = getString(R.string.checkConnection);
            utils = new Utils(getContext());
            utils.snackBar(root.findViewById(R.id.containerHome), message, 0);
        }
        utils.displaySnackBar(show);
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
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = this.menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
        cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.myCart:
                navController.navigate(R.id.action_navigation_home_to_navigation_cart);
                break;
            default:

                break;
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
            displaySnackBar(true);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {
        cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(), newSize, R.drawable.ic_cart));
    }

    @Override
    public void onCartView(double subTotal, boolean added) {

    }

    @Override
    public void onWishClick(View view, ProductModel productModel, boolean wished) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pageNumber = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
