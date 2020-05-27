package com.example.ecommerceapp.ui.home;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentSubCategoryBinding;
import com.example.ecommerceapp.pojo.MergeModel;
import com.example.ecommerceapp.ui.main.SubCategoryAdapter;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubCategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, SearchView.OnQueryTextListener {

    private HomeViewModel homeViewModel;
    private FragmentSubCategoryBinding binding;
    private ArrayList<MergeModel> categories, products;
    private SubCategoryAdapter adapter;
    private View root;
    private LinearLayoutManager layoutManagerRecycler;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Utils utils;
    private Menu menu;
    private SearchView searchView;
    private AlertDialog alertDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "categoryId";
    private static final String ARG_PARAM2 = "categoryName";

    // TODO: Rename and change types of parameters
    private String categoryId;
    private String categoryName;

    public SubCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubCategoryFragment newInstance(String param1, String param2) {
        SubCategoryFragment fragment = new SubCategoryFragment();
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
            categoryId = getArguments().getString(ARG_PARAM1);
            categoryName = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sub_category,container,false);
        root = binding.getRoot();
        Log.d(TAG, "MENUU:  onCreateView");
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(categoryName);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        layoutManagerRecycler = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewSubCategories.setHasFixedSize(true);
        binding.recyclerViewSubCategories.setLayoutManager(layoutManagerRecycler);
        adapter = new SubCategoryAdapter(getContext());
        binding.recyclerViewSubCategories.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.swipeRefresh.setColorSchemeColors(getContext().getColor(R.color.basicColor),
                    getContext().getColor(R.color.colorAccent),
                    getContext().getColor(R.color.colorAccent2));
        }
        binding.swipeRefresh.setOnRefreshListener(this);

        setViewSortDialog();

        if (InternetConnection.isNetworkOnline(getContext())) {
            displayProgressDialog(true);
            if (manager == null) {
                homeViewModel.getProductsAndSubCategories(categoryId);
            }
        } else {
            displaySnackBar(true);
        }

        homeViewModel.getSubCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<MergeModel>>() {
            @Override
            public void onChanged(ArrayList<MergeModel> mergeModels) {
                categories = mergeModels;
                displayProgressDialog(false);
                if (categories != null) {
                    adapter.setList(categories);
                    displaySnackBar(false);
                } else {
                    displaySnackBar(true);
                }
            }
        });
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<MergeModel>>() {
            @Override
            public void onChanged(ArrayList<MergeModel> mergeModels) {
                if (mergeModels != null) {
                    products = mergeModels;
                    adapter.setProductList(mergeModels);

                    displayProducts(mergeModels, false);
                } else {
                    displaySnackBar(true);
                }
            }
        });
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

    private void displayProducts(ArrayList<MergeModel> mergeModels, boolean search){
        if(manager == null || search){
            manager = getChildFragmentManager();
            transaction = manager.beginTransaction();
            transaction.replace(R.id.container_fragment, ProductsFragment.newInstance(mergeModels, null));
            transaction.commit();
        }
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
    public void onRefresh() {

        if (InternetConnection.isNetworkOnline(getContext())) {
            binding.swipeRefresh.setRefreshing(true);
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefresh.setRefreshing(false);
                    displayProgressDialog(true);
                    homeViewModel.getProductsAndSubCategories(categoryId);
                }
            }, 3000);

        } else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true);
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
    public void onClick(View view) {
        getActivity().onBackPressed();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (products != null) {
            searchView.setQuery(query, false);
            ArrayList<MergeModel> mergeModels = filter(products, query);
            if(mergeModels != null){
                displayProducts(mergeModels, true);
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (products != null) {
            ArrayList<MergeModel> mergeModels = filter(products, newText);

            if(mergeModels != null){
                displayProducts(mergeModels, true);
            }
        }
        return true;
    }
}
