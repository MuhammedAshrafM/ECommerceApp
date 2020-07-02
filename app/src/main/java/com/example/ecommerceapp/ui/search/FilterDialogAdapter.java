package com.example.ecommerceapp.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilterDialogAdapter extends RecyclerView.Adapter<FilterDialogAdapter.FilterDialogViewHolder> {

    private ArrayList<SubCategoryModel> categories = new ArrayList<>();
    private ArrayList<BrandModel> brands = new ArrayList<>();
    private ArrayList<String> categoriesSelected = new ArrayList<>();
    private ArrayList<String> brandsSelected = new ArrayList<>();
    private Context context;
    private int typeFilter;

    public FilterDialogAdapter(Context context, int typeFilter) {
        this.context = context;
        this.typeFilter = typeFilter;
    }

    @NonNull
    @Override
    public FilterDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new FilterDialogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterDialogViewHolder holder, final int position) {

        if(typeFilter == R.string.category) {
            holder.checkBox.setText(categories.get(position).getName());

        }else if (typeFilter == R.string.brand){
            holder.checkBox.setText(brands.get(position).getName());

        }


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    if(typeFilter == R.string.category) {
                        categoriesSelected.add(categories.get(position).getId());
                    }else if (typeFilter == R.string.brand){
                        brandsSelected.add(brands.get(position).getId());
                    }
                }else {
                    if(typeFilter == R.string.category) {
                        categoriesSelected.remove(categories.get(position).getId());
                    }else if (typeFilter == R.string.brand){
                        brandsSelected.remove(brands.get(position).getId());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(typeFilter == R.string.category) {
            return categories.size();
        }else if (typeFilter == R.string.brand){
            return brands.size();
        }
        return 0;
    }

    public void setSubCategoryList(ArrayList<SubCategoryModel> subCategoryModels) {
        this.categories = subCategoryModels;
        notifyDataSetChanged();
    }
    public void setBrandList(ArrayList<BrandModel> brandModels) {
        this.brands = brandModels;
        notifyDataSetChanged();
    }
    public ArrayList<String> getSubCategoriesSelected() {
        return categoriesSelected;
    }
    public ArrayList<String> getBrandsSelected() {
        return brandsSelected;
    }

    public class FilterDialogViewHolder extends RecyclerView.ViewHolder{

        private CheckBox checkBox;

        private FilterDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);

        }
    }
}
