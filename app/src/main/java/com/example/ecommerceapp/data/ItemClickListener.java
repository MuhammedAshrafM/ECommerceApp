package com.example.ecommerceapp.data;

import android.view.View;

/**
 * Created by Mu7ammed_A4raf on 07-Dec-17.
 */

public interface ItemClickListener {

    void onItemClick(View view, int position);

    void onCartClick(View view, String productId);

}
