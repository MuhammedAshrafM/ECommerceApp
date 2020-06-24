package com.example.ecommerceapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.PicassoClient;
import com.example.ecommerceapp.pojo.ImageProductModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ProductPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<ImageProductModel> productImages;
    private LayoutInflater layoutInflater;

    public ProductPagerAdapter(Context context, ArrayList<ImageProductModel> productImages) {
        this.context = context;
        this.productImages = productImages;
    }

    @Override
    public int getCount() {
        return productImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return (view == (LinearLayout)object);
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.layout_image_product, container, false);
        ImageView productIv = (ImageView)root.findViewById(R.id.product_image);
        PicassoClient.loadCategoryImage(context, productImages.get(position).getImagePath(), productIv);
        container.addView(root);
        return root;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    public void setList(ArrayList<ImageProductModel> productImages) {
        this.productImages = productImages;
        notifyDataSetChanged();
    }
}
