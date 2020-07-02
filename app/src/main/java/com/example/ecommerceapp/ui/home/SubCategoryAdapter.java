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
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder> {

    private NavController navController;
    private ArrayList<SubCategoryModel> categories = new ArrayList<>();
    private Context context;

    public SubCategoryAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SubCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_sub_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        holder.nameTV.setText(categories.get(position).getName());
        GlideClient.loadCategoryImage(context, categories.get(position).getImagePath(), holder.imageView);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("subCategoryId", categories.get(position).getId());
                bundle.putString("subCategoryName", categories.get(position).getName());
                bundle.putBoolean("offer", false);
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_subCategoryFragment_to_productsFragment, bundle);
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

    public void setSubCategoryList(ArrayList<SubCategoryModel> subCategoryModels) {
        this.categories = subCategoryModels;
        notifyDataSetChanged();
    }

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameTV;
        private ImageView imageView;

        private ItemClickListener itemClickListener;

        private SubCategoryViewHolder(@NonNull View itemView) {
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
