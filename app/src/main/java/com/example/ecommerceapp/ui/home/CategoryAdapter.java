package com.example.ecommerceapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.ProductModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private NavController navController;
    private ArrayList<CategoryModel> categories = new ArrayList<>();
    private Context context;

    public CategoryAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.nameTV.setText(categories.get(position).getName());
        GlideClient.loadCategoryImage(context, categories.get(position).getImagePath(), holder.imageView);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("categoryId", categories.get(position).getId());
                bundle.putString("categoryName", categories.get(position).getName());
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_home_to_subCategoryFragment, bundle);
            }
            @Override
            public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {

            }
            @Override
            public void onCartView(double subTotal, boolean added) {

            }
            @Override
            public void onWishClick(View view, ProductModel productModel, boolean wished) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setList(ArrayList<CategoryModel> CategoryModels) {
        this.categories = CategoryModels;
        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameTV;
        private ImageView imageView;

        private ItemClickListener itemClickListener;

        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.category_name);
            imageView = itemView.findViewById(R.id.category_image);

            itemView.setOnClickListener(this);
        }

        private void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, this.getAdapterPosition());
        }
    }
}
