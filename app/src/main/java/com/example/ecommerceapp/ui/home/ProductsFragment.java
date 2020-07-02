package com.example.ecommerceapp.ui.home;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.SortDialog;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentProductsBinding;
import com.example.ecommerceapp.pojo.ProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

import static com.example.ecommerceapp.data.Utils.filter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment implements View.OnClickListener, ItemDialogClickListener,
        ItemClickListener, SearchView.OnQueryTextListener, ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private HomeViewModel homeViewModel;
    private FragmentProductsBinding binding;
    private ArrayList<String> productsCartedId;
    private ArrayList<ProductModel> products;
    private ProductsAdapter adapter;
    private Utils utils;
    private View root;
    private Menu menu;
    private SearchView searchView;
    private SortDialog dialog = null;
    private MenuItem searchMenuItem, cartMenuItem;
    private ItemClickListener listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "products";
    private static final String ARG_PARAM2 = "subCategoryId";
    private static final String ARG_PARAM3 = "subCategoryName";
    private static final String ARG_PARAM4 = "offer";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";

    // TODO: Rename and change types of parameters
    private String subCategoryId;
    private String subCategoryName;
    private boolean offer;

    public ProductsFragment() {
        // Required empty public constructor
    }

    public ProductsFragment(ItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param param1
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductsFragment newInstance(ArrayList<ProductModel> param1, String param2, ItemClickListener listener) {
        ProductsFragment fragment = new ProductsFragment(listener);
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, (ArrayList<? extends Parcelable>) param1);
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
            products = getArguments().getParcelableArrayList(ARG_PARAM1);
            subCategoryId = getArguments().getString(ARG_PARAM2);
            subCategoryName = getArguments().getString(ARG_PARAM3);
            offer = getArguments().getBoolean(ARG_PARAM4);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_products,container,false);
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

        binding.toolbar.setTitle(subCategoryName);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductsAdapter(getContext(), this);
        binding.recyclerViewProducts.setAdapter(adapter);
        binding.sortByBt.setOnClickListener(this);
        binding.sortByTv.setOnClickListener(this);

        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,0,0));

        if (subCategoryId != null) {

            products = new ArrayList<>();
//            binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,products.size(),products.size()));
//            adapter.setList(products, false);
            if (ConnectivityReceiver.isConnected()) {
                displayProgressDialog(true);
                if(!offer) {
                    homeViewModel.getProducts(subCategoryId);
                }else {
                    homeViewModel.getProductsOffer(subCategoryId);
                }
            } else {
                displaySnackBar(true, null, 0);
            }
        }
        else{
            binding.toolbar.setVisibility(View.GONE);
            binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,products.size(),products.size()));
            adapter.setList(products, true);
            setViewSortDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        menu = null;

        homeViewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if (productModels != null && productModels.size() > 0) {
                    products = productModels;
                    binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,products.size(),products.size()));
                    adapter.setList(products, false);
                    setViewSortDialog();
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
        utils.snackBar(root.findViewById(R.id.containerProducts), msg, duration);
        utils.displaySnackBar(show);
    }

    private void setViewSortDialog(){
        dialog = dialog.getINSTANCE(getContext(), getActivity(), R.style.MaterialDialogSheet, this, R.string.products);
    }

    private void setSortProducts(int id, Comparator<ProductModel> comparator){
        Collections.sort(products, comparator);
        if (subCategoryId != null) {
            adapter.setList(products, false);
        }else {
            adapter.setList(products, true);
        }
        binding.sortByTv.setText(getString(id));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sortBy_bt: case R.id.sortBy_tv:
                if(dialog != null) {
                    dialog.show();
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
        if(carted){
            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).setProductCarted(productModel);

            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));

            if (subCategoryId == null) {
                listener.onCartClick(view, productModel, true, productsCartedId.size());
            }
            displaySnackBar(true, getString(R.string.carted), -1);

        }else {
            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).removeProductCarted(productModel);
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));
            if (subCategoryId == null) {
                listener.onCartClick(view, productModel, false, productsCartedId.size());
            }
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

            searchView = (SearchView)searchMenuItem.getActionView();
            searchView.setQueryHint("");
            searchView.setOnQueryTextListener(this);

            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(), productsCartedId.size(),R.drawable.ic_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myCart){
            navController.navigate(R.id.action_productsFragment_to_navigation_cart);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (products != null) {
            searchView.setQuery(query, false);
            ArrayList<ProductModel> productModels = filter(products, query);
            adapter.setList(productModels, false);
            binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,productModels.size(),productModels.size()));
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (products != null) {
            ArrayList<ProductModel> productModels = filter(products, newText);
            if(productModels != null){
                adapter.setList(productModels, false);
                binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,productModels.size(),productModels.size()));
            }
        }

        return true;
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
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

            default:

                break;
        }
    }
}
