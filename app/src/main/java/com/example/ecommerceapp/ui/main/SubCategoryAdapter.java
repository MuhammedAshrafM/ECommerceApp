package com.example.ecommerceapp.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.PicassoClient;
import com.example.ecommerceapp.pojo.MergeModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder> {

    private NavController navController;
    private ArrayList<MergeModel> categories = new ArrayList<>();
    private ArrayList<MergeModel> products = new ArrayList<>();
    private Context context;

    public SubCategoryAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SubCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        holder.nameTV.setText(categories.get(position).getSubCategoryName());
        PicassoClient.loadCategoryImage(context, categories.get(position).getSubCategoryImagePath(), holder.imageView);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("subCategoryId", categories.get(position).getSubCategoryIds());
                bundle.putString("subCategoryName", categories.get(position).getSubCategoryName());
                bundle.putParcelableArrayList("products", products);
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_subCategoryFragment_to_productFragment, bundle);
            }

            @Override
            public void onCartClick(View view, String productId) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setList(ArrayList<MergeModel> mergeModels) {
        this.categories = mergeModels;
        notifyDataSetChanged();
    }
    public void setProductList(ArrayList<MergeModel> mergeModels) {
        this.products = mergeModels;
    }

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameTV;
        private ImageView imageView;

        private ItemClickListener itemClickListener;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.category_name);
            imageView = itemView.findViewById(R.id.category_image);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, this.getAdapterPosition());
        }
    }
}
