package com.example.ecommerceapp.data;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ecommerceapp.R;

public class GlideClient {

    public static void loadCategoryImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.loader)
                .error(R.drawable.background_simple_button)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }
    public static void loadResourceImage(Context context, int id, ImageView imageView) {
        Glide.with(context)
                .load(id)
                .placeholder(id)
                .error(id)
                .into(imageView);
    }
    public static void loadProfileImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.ic_launcher_profile)
                .error(R.mipmap.ic_launcher_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }
}
