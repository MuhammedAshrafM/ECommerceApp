package com.example.ecommerceapp.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.InternetConnection;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentProductBinding;
import com.example.ecommerceapp.pojo.MergeModel;
import com.example.ecommerceapp.ui.main.ProductAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment implements View.OnClickListener,
        ItemClickListener, SearchView.OnQueryTextListener {


    private HomeViewModel homeViewModel;
    private FragmentProductBinding binding;
    private ArrayList<MergeModel> products;
    private ProductAdapter adapter;
    private Utils utils;
    private View root;
    private Menu menu;
    private SearchView searchView;
    private AlertDialog alertDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "products";
    private static final String ARG_PARAM2 = "subCategoryId";
    private static final String ARG_PARAM3 = "subCategoryName";

    // TODO: Rename and change types of parameters
    private ArrayList<MergeModel> mergeModels;
    private String subCategoryId;
    private String subCategoryName;

    public ProductsFragment() {
        // Required empty public constructor
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
    public static ProductsFragment newInstance(ArrayList<MergeModel> param1, String param2) {
        ProductsFragment fragment = new ProductsFragment();
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
            mergeModels = getArguments().getParcelableArrayList(ARG_PARAM1);
            subCategoryId = getArguments().getString(ARG_PARAM2);
            subCategoryName = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_product,container,false);
        root = binding.getRoot();

        return root;
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
        adapter = new ProductAdapter(getContext(), this);
        binding.recyclerViewProducts.setAdapter(adapter);
        binding.sortByBt.setOnClickListener(this);

        setViewSortDialog();

        if (subCategoryId != null) {
            products = filterProducts(mergeModels, subCategoryId);
            binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,products.size(),products.size()));
            adapter.setList(products);
            if (!InternetConnection.isNetworkOnline(getContext())) {
                displaySnackBar(true);
            }

        }
        else{
            binding.toolbar.setVisibility(View.GONE);
            binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfProductsAvailable,mergeModels.size(),mergeModels.size()));
            adapter.setList(mergeModels);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        menu = null;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LIFE CYCLE: onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    private void displayProgressDialog(boolean show){
        if(show){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.GONE);
        }

    }
    private void displaySnackBar(boolean show){
        if(utils == null){
            String message = getString(R.string.checkConnection);
            utils = new Utils(getContext());
            utils.snackBar(root.findViewById(R.id.containerSubCategories), message);
        }
        utils.displaySnackBar(show);
    }

    private ArrayList<MergeModel> filterProducts(ArrayList<MergeModel> mergeModels, String subCategoryId){
        final ArrayList<MergeModel> filterModelsList = new ArrayList<>();
        for(MergeModel model: mergeModels){
            final String id = model.getSubCategoryId();
            if(id.equals(subCategoryId)){
                filterModelsList.add(model);
            }
        }

        return filterModelsList;
    }


    private ArrayList<MergeModel> filter(ArrayList<MergeModel> mergeModels, String query){
        while (query.startsWith(" ")){

            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return null;
            }
        }
        query = query.toLowerCase();
        ArrayList<MergeModel> filterModelsList = new ArrayList<>();
        for(MergeModel model: mergeModels){
            final String title = model.getTitle().toLowerCase();
            if(title.contains(query)){

                Log.d(TAG, "RRRRR: " +title.toString());
                try {
                    filterModelsList.add(model);

                }catch (Exception e){
                    Log.d(TAG, "RRRRR: " +e.toString());
                }
            }
        }

        return filterModelsList;
    }

    private void setViewSortDialog(){
        View view = getLayoutInflater().inflate(R.layout.layout_sort, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        alertDialog = builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sortBy_bt:
                alertDialog.show();
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
    public void onCartClick(View view, String productId) {
//        SubCategoryFragment.onCartClick(view, productId);
        menu.getItem(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_carted));
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
            MenuItem menuItem = this.menu.findItem(R.id.search);

            searchView = (SearchView)menuItem.getActionView();
            searchView.setQueryHint("");
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

//        menu.getItem(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_carted));
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (products != null) {
            searchView.setQuery(query, false);
            ArrayList<MergeModel> mergeModels = filter(products, query);
            adapter.setList(mergeModels);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (products != null) {
            ArrayList<MergeModel> mergeModels = filter(products, newText);
            if(mergeModels != null){
                adapter.setList(mergeModels);
            }
        }
        return true;
    }
}
