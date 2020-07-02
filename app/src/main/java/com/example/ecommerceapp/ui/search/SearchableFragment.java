package com.example.ecommerceapp.ui.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.FilterBrandDialog;
import com.example.ecommerceapp.data.FilterCategoryDialog;
import com.example.ecommerceapp.data.FilterPriceDialog;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.MySuggestionProvider;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.SortDialog;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentSearchableBinding;
import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

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
import androidx.recyclerview.widget.GridLayoutManager;

import static com.facebook.GraphRequest.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchableFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener, ItemClickListener, ItemDialogClickListener {


    private NavController navController;
    private SearchViewModel searchViewModel;
    private ArrayList<ProductModel> products;
    private ArrayList<SubCategoryModel> subCategories;
    private ArrayList<BrandModel> brands;
    private Map<String, Double> infoProducts, priceRangeSelected;
    private ArrayList<String> categoriesSelected;
    private ArrayList<String> brandsSelected;
    private ProductsSearchedAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private FragmentSearchableBinding binding;
    private ArrayList<String> productsCartedId;
    private Utils utils;
    private View root;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private SortDialog sortDialog;
    private FilterCategoryDialog filterCategoryDialog;
    private FilterBrandDialog filterBrandDialog;
    private FilterPriceDialog filterPriceDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "query";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";

    // TODO: Rename and change types of parameters
    private String query;
    private String mParam2;

    public SearchableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchableFragment newInstance(String param1, String param2) {
        SearchableFragment fragment = new SearchableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            query = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_searchable, container, false);
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

//        binding.toolbar.setTitle(subCategoryName);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);


        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);

        gridLayoutManager = new GridLayoutManager(getContext(),2);
        binding.recyclerViewProductsSearched.setHasFixedSize(true);
        binding.recyclerViewProductsSearched.setLayoutManager(gridLayoutManager);

        adapter = new ProductsSearchedAdapter(getContext(), this);
        binding.recyclerViewProductsSearched.setAdapter(adapter);

        binding.sortByBt.setOnClickListener(this);
        binding.sortByTv.setOnClickListener(this);
        binding.categoryBt.setOnClickListener(this);
        binding.priceRangeBt.setOnClickListener(this);
        binding.brandBt.setOnClickListener(this);

        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,0,0));

//        searchProducts(query);

    }

    @Override
    public void onStart() {
        super.onStart();
        menu = null;
        sortDialog = null;
        filterCategoryDialog = null;
        filterBrandDialog = null;
        filterPriceDialog = null;

        searchViewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null) {
                    products = productModels;
                    binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,products.size(),products.size()));
                    adapter.setList(products);

                }
                if(productModels.size() > 0){
                    setViewSortDialog();
                }
            }
        });

        searchViewModel.getSubCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<SubCategoryModel>>() {
            @Override
            public void onChanged(ArrayList<SubCategoryModel> subCategoryModels) {
                if(subCategoryModels != null && subCategoryModels.size() > 0) {
                    subCategories = subCategoryModels;
                    binding.categoryBt.setEnabled(true);
                }else {
                    binding.categoryBt.setEnabled(false);
                }

            }
        });

        searchViewModel.getBrands().observe(getViewLifecycleOwner(), new Observer<ArrayList<BrandModel>>() {
            @Override
            public void onChanged(ArrayList<BrandModel> brandModels) {
                Log.d(TAG, "MERO: ArrayList " + brandModels);
                if(brandModels != null && brandModels.size() > 0) {
                    brands = brandModels;
                    if (brands.size() == 1) {
                        binding.brandBt.setVisibility(View.GONE);
                    } else {
                        binding.brandBt.setEnabled(true);
                        binding.brandBt.setVisibility(View.VISIBLE);
                    }
                }else {
                    binding.brandBt.setEnabled(false);
                }
            }
        });

        searchViewModel.getProductsInfo().observe(getViewLifecycleOwner(), new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> stringDoubleMap) {
                if(stringDoubleMap.get(getString(R.string.minPriceKey)) != null) {
                    infoProducts = stringDoubleMap;
                    binding.priceRangeBt.setEnabled(true);

                }else {
                    binding.priceRangeBt.setEnabled(false);

                }
            }
        });


        searchViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
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

    private void searchProducts(String query){
        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            searchViewModel.getProductsSearched(query);
            this.query = query;
        } else {
            displaySnackBar(true, null, 0);
        }
    }
    private void displayProgressDialog(boolean show) {
        if (show) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }

    }
    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(getContext());
        utils.snackBar(root.findViewById(R.id.containerSearchable), msg, duration);
        utils.displaySnackBar(show);
    }

    private void setViewSortDialog() {
        if (sortDialog == null) {
            sortDialog = sortDialog.getINSTANCE(getContext(), getActivity(), R.style.MaterialDialogSheet, this, R.string.products);
        }
    }

    private void setViewFilterCategoryDialog(int typeFilter){
        if(filterCategoryDialog == null) {
            filterCategoryDialog = filterCategoryDialog.getINSTANCE(getContext(), getActivity(), R.style.MaterialDialogSheet, this, subCategories);
        }
        filterCategoryDialog.show();

    }
    private void setViewFilterBrandDialog(int typeFilter){
        if(filterBrandDialog == null) {
            filterBrandDialog = filterBrandDialog.getINSTANCE(getContext(), getActivity(), R.style.MaterialDialogSheet, this, brands);
        }
        filterBrandDialog.show();

    }

    private void setViewFilterPriceDialog(int typeFilter){
        if(filterPriceDialog == null) {
            filterPriceDialog = filterPriceDialog.getINSTANCE(getContext(), getActivity(), R.style.MaterialDialogSheet, this, infoProducts);
        }
        filterPriceDialog.show();

    }

    private void setSortProducts(int id, Comparator<ProductModel> comparator){
        Collections.sort(products, comparator);
        adapter.setList(products);
        binding.sortByTv.setText(getString(id));
    }


    private void filterProducts(){
        if(categoriesSelected == null || categoriesSelected.size() == 0){
            categoriesSelected = new ArrayList<>();
            categoriesSelected.add("null");
        }
        if(brandsSelected == null || brandsSelected.size() == 0){
            brandsSelected = new ArrayList<>();
            brandsSelected.add("null");
        }
        if(binding.brandBt.getVisibility() == View.GONE){
            brandsSelected = new ArrayList<>();
            brandsSelected.add(brands.get(0).getId());
        }
        if(priceRangeSelected == null || priceRangeSelected.size() == 0){
            priceRangeSelected = infoProducts;
        }

        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            searchViewModel.getProductsFiltered(query, categoriesSelected, priceRangeSelected, brandsSelected);
        } else {
            displaySnackBar(true, null, 0);
        }

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
        searchView.setSubmitButtonEnabled(true);

        productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
        cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));


        searchView.setQuery(query, true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.myCart){
            navController.navigate(R.id.action_navigation_search_to_navigation_cart);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        filterCategoryDialog = null;
        filterBrandDialog = null;
        filterPriceDialog = null;
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(),
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.saveRecentQuery(s, null);
        searchProducts(s);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sortBy_bt: case R.id.sortBy_tv:
                if(sortDialog != null) {
                    sortDialog.show();
                }
                break;

            case R.id.category_bt:
                if(subCategories != null) {
                    setViewFilterCategoryDialog(R.string.category);
                }


                break;

            case R.id.priceRange_bt:
                if(infoProducts != null) {
                    setViewFilterPriceDialog(R.string.priceRange);
                }
                break;

            case R.id.brand_bt:
                if(brands != null) {
                    setViewFilterBrandDialog(R.string.brand);
                }

                break;

            default:
                getActivity().onBackPressed();
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
                filterProducts();
                break;

            case R.string.brand:
                brandsSelected = filterBrandDialog.getBrandsSelected();
                filterProducts();
                break;

            case R.string.priceRange:
                priceRangeSelected = filterPriceDialog.getPriceRangeSelected();
                filterProducts();
                break;

            default:

                break;
        }
    }
}
