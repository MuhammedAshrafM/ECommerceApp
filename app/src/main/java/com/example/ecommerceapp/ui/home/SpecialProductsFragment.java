package com.example.ecommerceapp.ui.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentSpecialProductsBinding;
import com.example.ecommerceapp.pojo.ProductModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpecialProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpecialProductsFragment extends Fragment implements ItemClickListener{

    private FragmentSpecialProductsBinding binding;
    private ArrayList<String> productsCartedId;
    private ArrayList<ProductModel> products;
    private SpecialProductsAdapter adapter;
    private LinearLayoutManager layoutManagerRecycler;
    private Utils utils;
    private View root;
    private ItemClickListener listener;
    private Context context;
    private Activity activity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "products";
    private static final String ARG_PARAM2 = "typeView";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";

    // TODO: Rename and change types of parameters
    private String typeView;

    public SpecialProductsFragment() {
        // Required empty public constructor
    }
    public SpecialProductsFragment(ItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpecialProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpecialProductsFragment newInstance(ArrayList<ProductModel> param1, String param2, ItemClickListener listener) {
        SpecialProductsFragment fragment = new SpecialProductsFragment(listener);
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, (ArrayList<? extends Parcelable>) param1);
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

        if (getArguments() != null) {
            products = getArguments().getParcelableArrayList(ARG_PARAM1);
            typeView = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_special_products,container,false);
        root = binding.getRoot();

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.typeView.setText(typeView);

    }

    @Override
    public void onStart() {
        super.onStart();


        layoutManagerRecycler = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewSpecialProducts.setHasFixedSize(true);
        binding.recyclerViewSpecialProducts.setLayoutManager(layoutManagerRecycler);

        adapter = new SpecialProductsAdapter(context, this);
        binding.recyclerViewSpecialProducts.setAdapter(adapter);

        if(typeView.equals(getString(R.string.recentlyViewed))){
            adapter.setList(products, true);
        }else {
            adapter.setList(products, false);
        }
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerSpecialProducts), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
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
}
