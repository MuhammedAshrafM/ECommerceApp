package com.example.ecommerceapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.PicassoClient;
import com.example.ecommerceapp.pojo.SubCategoryModel;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;

public class SubCategoryPagerAdapter extends PagerAdapter {

    private NavController navController;
    private Context context;
    private ArrayList<SubCategoryModel> subCategoryModels;
    private ArrayList<Map<String, Float>> infoSubCategories;
    private LayoutInflater layoutInflater;

    public SubCategoryPagerAdapter(Context context, ArrayList<SubCategoryModel> subCategoryModels) {
        this.context = context;
        this.subCategoryModels = subCategoryModels;
        this.infoSubCategories = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return subCategoryModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return (view == (LinearLayout)object);
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.layout_image_sub_category, container, false);
        ImageView subCategoryIv = (ImageView)root.findViewById(R.id.sub_category_image);
        TextView subCategoryOfferTv = (TextView) root.findViewById(R.id.sub_category_offer_tv);
        TextView subCategoryNameTv = (TextView) root.findViewById(R.id.sub_category_name_tv);
        subCategoryOfferTv.setText(String.format("%s%.0f%s", context.getString(R.string.upTo) + "\n",
                infoSubCategories.get(position).get("maxOffer"),
                context.getString(R.string.percent) + "\n" + context.getString(R.string.off)));
        subCategoryNameTv.setText(subCategoryModels.get(position).getName());
        PicassoClient.loadCategoryImage(context, subCategoryModels.get(position).getImagePath(), subCategoryIv);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("subCategoryId", subCategoryModels.get(position).getId());
                bundle.putString("subCategoryName", subCategoryModels.get(position).getName());
                bundle.putBoolean("offer", true);
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_home_to_productsFragment, bundle);
            }
        });

        container.addView(root);
        return root;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    public void setList(ArrayList<SubCategoryModel> subCategoryModels, ArrayList<Map<String, Float>> infoSubCategories) {
        this.subCategoryModels = subCategoryModels;
        this.infoSubCategories = infoSubCategories;
        notifyDataSetChanged();
    }
}
