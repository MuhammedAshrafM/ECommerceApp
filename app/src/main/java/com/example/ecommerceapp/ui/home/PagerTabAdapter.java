package com.example.ecommerceapp.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.SellerModel;

import java.util.ArrayList;

public class PagerTabAdapter extends FragmentStatePagerAdapter {

    private String tabTitles [];
    private ArrayList<Fragment> fragments;

    public PagerTabAdapter(@NonNull FragmentManager fm, SellerModel sellerModel, ItemClickListener listener) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(0, SellerProductsFragment.newInstance(sellerModel, "", listener));
        fragments.add(1, SellerReviewsFragment.newInstance(sellerModel, ""));
        tabTitles = new String[]{"Seller Listings","Ratings and Reviews"};

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {

        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
