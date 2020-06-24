package com.example.ecommerceapp.ui.more;

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
import com.example.ecommerceapp.databinding.FragmentWishBinding;
import com.example.ecommerceapp.pojo.ProductModel;

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

public class WishFragment extends Fragment implements View.OnClickListener, ItemClickListener,
        SearchView.OnQueryTextListener, ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private MoreViewModel moreViewModel;
    private FragmentWishBinding binding;
    private ArrayList<ProductModel> productsWished;
    private ArrayList<String> productsCartedId, productsWishedId;
    private View root;
    private Menu menu;
    private SearchView searchView;
    private String[] productIds;
    private ProductWishedAdapter adapter;
    private Utils utils;

    private static final String PREFERENCES_PRODUCTS_WISHED = "PRODUCTS_WISHED";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private MenuItem searchMenuItem, cartMenuItem;

    public WishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setHasOptionsMenu(true);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_wish,container,false);
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

        binding.toolbar.setTitle(getString(R.string.title_wish));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        moreViewModel =
                ViewModelProviders.of(this).get(MoreViewModel.class);

        binding.recyclerViewProductsWished.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductWishedAdapter(getContext(), this);
        binding.recyclerViewProductsWished.setAdapter(adapter);

        productsWishedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_WISHED).getProductsWished();

        if (ConnectivityReceiver.isConnected()) {
            if(productsWishedId.size() > 0) {
                displayProgressDialog(true);
                productIds = new String[productsWishedId.size()];
                moreViewModel.getProductsWished(productsWishedId.toArray(productIds));
            }
        }else {
            displaySnackBar(true, null, 0);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        menu = null;

        moreViewModel.getProductsWished().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null && productModels.size() > 0) {
                    productsWished = productModels;
                    adapter.setList(productsWished);
                    displaySnackBar(false, null, 0);

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
        utils.snackBar(root.findViewById(R.id.containerProductsWished), msg, duration);
        utils.displaySnackBar(show);
    }


    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {
        if(carted){
            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).setProductCarted(productModel);

            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));


            displaySnackBar(true, getString(R.string.carted), -1);

        }else {
            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).removeProductCarted(productModel);
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));

            displaySnackBar(true, getString(R.string.unCarted), -1);


        }
    }

    @Override
    public void onCartView(double subTotal, boolean added) {

    }

    @Override
    public void onWishClick(View view, ProductModel productModel, boolean wished) {
        if(!wished){
            productsWishedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_WISHED).removeProductWished(productModel);
            productsWished.remove(productModel);
            adapter.setList(productsWished);
        }
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
            searchMenuItem = this.menu.findItem(R.id.search);

            SearchManager searchManager = (SearchManager)
                    getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) searchMenuItem.getActionView();

            searchView.setSearchableInfo(searchManager.
                    getSearchableInfo(getActivity().getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);

            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(), productsCartedId.size(),R.drawable.ic_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.myCart:
                navController.navigate(R.id.action_wishFragment_to_navigation_cart);
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
            displaySnackBar(true, null, 0);
        }
    }
}
