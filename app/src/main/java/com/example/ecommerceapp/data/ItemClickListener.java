package com.example.ecommerceapp.data;

import android.view.View;

import com.example.ecommerceapp.pojo.ProductModel;


public interface ItemClickListener {

    void onItemClick(View view, int position);

    void onCartClick(View view, ProductModel productModel, boolean carted, int newSize);

    void onCartView(double subTotal, boolean added);

    void onWishClick(View view, ProductModel productModel, boolean wished);
}
