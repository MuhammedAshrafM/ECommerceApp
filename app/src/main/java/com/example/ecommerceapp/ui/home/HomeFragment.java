package com.example.ecommerceapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.InternetConnection;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentHomeBinding;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.ui.main.CategoryAdapter;

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

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private NavController navController;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private CategoryAdapter adapter;
    private ArrayList<CategoryModel> categories;
    private Utils utils;
    private View root;

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

        adapter = new CategoryAdapter(getContext());
        binding.gridViewCategory.setAdapter(adapter);
        binding.gridViewCategory.setOnItemClickListener(this);
        if(categories == null) {
            if (InternetConnection.isNetworkOnline(getContext())) {
                displayProgressDialog(true);
                homeViewModel.getMainCategories();
            } else {
                displaySnackBar(true);
            }
        }

        homeViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<ArrayList<CategoryModel>>() {
            @Override
            public void onChanged(ArrayList<CategoryModel> categoryModels) {
                categories = categoryModels;
                displayProgressDialog(false);
                if (categoryModels != null) {
                    adapter.setList(categoryModels);
                } else {
                    displaySnackBar(true);
                }
            }
        });
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
            utils.snackBar(root.findViewById(R.id.containerHome), message);
        }
        utils.displaySnackBar(show);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", categories.get(i).getId());
            bundle.putString("categoryName", categories.get(i).getName());
            navController.navigate(R.id.action_navigation_home_to_subCategoryFragment, bundle);
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
