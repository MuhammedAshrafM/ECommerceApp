package com.example.ecommerceapp.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.PicassoClient;
import com.example.ecommerceapp.pojo.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {

    private ArrayList<CategoryModel> categoriesList = new ArrayList<>();
    private Context context;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoriesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = view;
        CategoryViewHolder viewHolder;
        if(root == null) {
            root = LayoutInflater.from(context).inflate(R.layout.category_item, viewGroup, false);
            viewHolder = new CategoryViewHolder(root);
            root.setTag(viewHolder);
        }else {
            viewHolder = (CategoryViewHolder) root.getTag();
        }

        viewHolder.getTextView().setText(categoriesList.get(i).getName());
        PicassoClient.loadCategoryImage(context, categoriesList.get(i).getImagePath(), viewHolder.getImageView());
        return root;

    }
    public void setList(ArrayList<CategoryModel> categoriesList) {
        this.categoriesList = categoriesList;
        notifyDataSetChanged();

    }


    public class CategoryViewHolder {

        private View itemView;
        private TextView nameTV;
        private ImageView imageView;
        public CategoryViewHolder(View itemView) {
           this.itemView = itemView;
        }

        public TextView getTextView(){
            if(nameTV == null) {
                nameTV = (TextView) itemView.findViewById(R.id.category_name);
            }
            return nameTV;
        }

        public ImageView getImageView(){
            if(imageView == null) {
                imageView = (ImageView) itemView.findViewById(R.id.category_image);
            }
            return imageView;
        }
    }
}
