package com.example.ecommerceapp.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.FilterBrandDialog;
import com.example.ecommerceapp.data.FilterCategoryDialog;
import com.example.ecommerceapp.data.FilterPriceDialog;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.SortDialog;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentSellerInfoBinding;
import com.example.ecommerceapp.databinding.FragmentSellerProductsBinding;
import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.SellerModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;
import com.example.ecommerceapp.ui.search.ProductsSearchedAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.example.ecommerceapp.data.Utils.filterProducts;
import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerProductsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ItemClickListener, View.OnClickListener, ItemDialogClickListener {

    private HomeViewModel homeViewModel;
    private FragmentSellerProductsBinding binding;
    private SellerProductsAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<String> productsCartedId;
    private ArrayList<ProductModel> products, productsFiltered;
    private ArrayList<SubCategoryModel> subCategories;
    private ArrayList<BrandModel> brands;
    private Map<String, Double> infoProducts, priceRangeSelected;
    private ArrayList<String> categoriesSelected;
    private ArrayList<String> brandsSelected;
    private SortDialog sortDialog;
    private FilterCategoryDialog filterCategoryDialog;
    private FilterBrandDialog filterBrandDialog;
    private FilterPriceDialog filterPriceDialog;
    private Utils utils;
    private View root;
    private Context context;
    private Activity activity;
    private ItemClickListener listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "seller";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";

    // TODO: Rename and change types of parameters
    private SellerModel seller;
    private String mParam2;

    public SellerProductsFragment() {
        // Required empty public constructor
    }

    public SellerProductsFragment(ItemClickListener listener) {
        this.listener = listener;
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
    public static SellerProductsFragment newInstance(SellerModel param1, String param2, ItemClickListener listener) {
        SellerProductsFragment fragment = new SellerProductsFragment(listener);
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            seller = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_seller_products,container,false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding.priceRangeTv.setEnabled(false);
    }


    @Override
    public void onStart() {
        super.onStart();

        sortDialog = null;

        initialVariables();

        handleUi();

        setOnClickListener();
        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            homeViewModel.getSellerProducts(seller.getId());
        } else {
            displaySnackBar(true, null, 0);
        }

        observeLiveData();
    }

    private void initialVariables(){
        filterCategoryDialog = null;
        filterBrandDialog = null;
        filterPriceDialog = null;
        priceRangeSelected = new HashMap<>();
        categoriesSelected = new ArrayList<>();
        brandsSelected = new ArrayList<>();
        infoProducts = new HashMap<>();
    }

    private void handleUi(){

        gridLayoutManager = new GridLayoutManager(context,2);
        binding.recyclerViewSellerProducts.setHasFixedSize(true);
        binding.recyclerViewSellerProducts.setLayoutManager(gridLayoutManager);


        adapter = new SellerProductsAdapter(context, this, this);
        binding.recyclerViewSellerProducts.setAdapter(adapter);

        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,0,0));
        binding.sellerNameTv.setText(seller.getName());

        binding.swipeRefresh.setColorSchemeColors(Color.rgb(3, 169, 244),
                Color.rgb(3, 169, 244),
                Color.rgb(13, 179, 163));
        binding.swipeRefresh.setOnRefreshListener(this);
    }


    private void setOnClickListener(){
        binding.sortByBt.setOnClickListener(this);
        binding.sortByTv.setOnClickListener(this);
        binding.categoryTv.setOnClickListener(this);
        binding.priceRangeTv.setOnClickListener(this);
        binding.brandTv.setOnClickListener(this);
    }

    private void observeLiveData(){
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null) {
                    products = productModels;
                    productsFiltered = products;
                    binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,products.size(),products.size()));
                    adapter.setList(products);
                }
                if(productModels.size() > 0){
                    setViewSortDialog();
                }
            }
        });
        homeViewModel.getSubCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<SubCategoryModel>>() {
            @Override
            public void onChanged(ArrayList<SubCategoryModel> subCategoryModels) {
                if(subCategoryModels != null && subCategoryModels.size() > 0) {
                    subCategories = subCategoryModels;
                    binding.categoryTv.setEnabled(true);
                }else {
                    binding.categoryTv.setEnabled(false);
                }
            }
        });
        homeViewModel.getBrands().observe(getViewLifecycleOwner(), new Observer<ArrayList<BrandModel>>() {
            @Override
            public void onChanged(ArrayList<BrandModel> brandModels) {
                if(brandModels != null && brandModels.size() > 0) {
                    brands = brandModels;
                    if (brands.size() == 1) {
                        binding.setVisibleBrand(false);
                    } else {
                        binding.setVisibleBrand(true);
                        binding.brandTv.setEnabled(true);
                    }
                }else {
                    binding.brandTv.setEnabled(false);
                }
            }
        });
        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0);
            }
        });

        adapter.getProductsInfo().observe(getViewLifecycleOwner(), new Observer<ArrayList<Double>>() {
            @Override
            public void onChanged(ArrayList<Double> doubles) {
                binding.priceRangeTv.setEnabled(true);
                infoProducts.put(getString(R.string.minPriceKey), Collections.min(doubles).doubleValue());
                infoProducts.put(getString(R.string.maxPriceKey), Collections.max(doubles).doubleValue());
            }
        });
    }

    private void setViewSortDialog() {
        if (sortDialog == null) {
            sortDialog = sortDialog.getINSTANCE(context, activity, R.style.MaterialDialogSheet, this, R.string.products);
        }
    }

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerSellerProducts), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }


    private void setViewFilterCategoryDialog(int typeFilter){
        if(filterCategoryDialog == null) {
            filterCategoryDialog = filterCategoryDialog.getINSTANCE(context, activity, R.style.MaterialDialogSheet, this, subCategories);
        }
        filterCategoryDialog.show();

    }
    private void setViewFilterBrandDialog(int typeFilter){
        if(filterBrandDialog == null) {
            filterBrandDialog = filterBrandDialog.getINSTANCE(context, activity, R.style.MaterialDialogSheet, this, brands);
        }
        filterBrandDialog.show();

    }

    private void setViewFilterPriceDialog(int typeFilter){
        if(filterPriceDialog == null) {
            filterPriceDialog = filterPriceDialog.getINSTANCE(context, activity, R.style.MaterialDialogSheet, this, infoProducts);
        }
        filterPriceDialog.show();

    }

    private void setSortProducts(int id, Comparator<ProductModel> comparator){
        Collections.sort(productsFiltered, comparator);
        adapter.setList(productsFiltered);
        binding.sortByTv.setText(getString(id));
    }


    private void filter(){
        if(!binding.getVisibleBrand()){
            brandsSelected = new ArrayList<>();
            brandsSelected.add(brands.get(0).getId());
        }
        if(priceRangeSelected == null || priceRangeSelected.size() == 0){
            priceRangeSelected = infoProducts;

        }
        productsFiltered = filterProducts(products,
                categoriesSelected,
                priceRangeSelected.get(getString(R.string.minPriceKey)),
                priceRangeSelected.get(getString(R.string.maxPriceKey)),
                brandsSelected);
        adapter.filter(productsFiltered);
        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,productsFiltered.size(),productsFiltered.size()));
    }

    @Override
    public void onRefresh() {
        if (ConnectivityReceiver.isConnected()) {
            binding.swipeRefresh.setRefreshing(true);
            initialVariables();
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefresh.setRefreshing(false);
                    displayProgressDialog(true);
                    homeViewModel.getSellerProducts(seller.getId());
                }}, 3000);

        }else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null, 0);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {
        if(carted){
            productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).setProductCarted(productModel);
            listener.onCartClick(view, productModel, true, productsCartedId.size());
            displaySnackBar(true, getString(R.string.carted), -1);

        }else {
            productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).removeProductCarted(productModel);
            listener.onCartClick(view, productModel, false, productsCartedId.size());
            displaySnackBar(true, getString(R.string.unCarted), -1);
        }
    }

    @Override
    public void onCartView(double subTotal, boolean added) {

    }

    @Override
    public void onWishClick(View view, ProductModel productModel, boolean wished) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sortBy_bt: case R.id.sortBy_tv:
                if (sortDialog != null) {
                    sortDialog.show();
                }
                break;

            case R.id.category_tv:
                if (subCategories != null) {
                    setViewFilterCategoryDialog(R.string.category);
                }
                break;

            case R.id.priceRange_tv:
                if (infoProducts != null) {
                    setViewFilterPriceDialog(R.string.priceRange);
                }
                break;

            case R.id.brand_tv:
                if (brands != null) {
                    setViewFilterBrandDialog(R.string.brand);
                }
                break;

            default:

                break;
        }
    }

    @Override
    public void onItemDialogClick(View view, int id) {
        switch (id){
            case R.string.bestMatches:
                setSortProducts(R.string.bestMatches, ProductModel.bestMatchesComparator);
                break;
            case R.string.lowToHighPrice:
                setSortProducts(R.string.lowToHighPrice, ProductModel.priceLowComparator);
                break;
            case R.string.highToLowPrice:
                setSortProducts(R.string.highToLowPrice, ProductModel.priceHighComparator);
                break;
            case R.string.lowToHighOffer:
                setSortProducts(R.string.lowToHighOffer, ProductModel.offerLowComparator);
                break;
            case R.string.highToLowOffer:
                setSortProducts(R.string.highToLowOffer, ProductModel.offerHighComparator);
                break;
            case R.string.topRated:
                setSortProducts(R.string.topRated, ProductModel.topRatedComparator);
                break;
            case R.string.newArrivals:
                setSortProducts(R.string.newArrivals, ProductModel.newComparator);
                break;

            case R.string.category:
                categoriesSelected = filterCategoryDialog.getSubCategoriesSelected();
                filter();
                break;

            case R.string.brand:
                brandsSelected = filterBrandDialog.getBrandsSelected();
                filter();
                break;

            case R.string.priceRange:
                priceRangeSelected = filterPriceDialog.getPriceRangeSelected();
                filter();
                break;

            default:

                break;
        }
    }
}