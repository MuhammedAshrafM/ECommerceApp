package com.example.ecommerceapp.ui.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.PicassoClient;
import com.example.ecommerceapp.pojo.MergeModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.SubCategoryViewHolder> {

    private ArrayList<MergeModel> products = new ArrayList<>();
    private Context context;
    private ItemClickListener listener;

    public ProductAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {

        }catch (Exception e){
            Log.d(TAG, "errrrrr: " +e.toString());
        }
        return new SubCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        double price = products.get(position).getPrice() * ((100 - products.get(position).getOffer())/100);
        float offer = products.get(position).getOffer();
        int quantity = products.get(position).getQuantity();

        PicassoClient.loadCategoryImage(context, products.get(position).getImagePath(), holder.imageView);
        holder.productOfferTV.setText(String.format("%.0f%s",offer,"% OFF"));
        holder.productTitleTV.setText(products.get(position).getTitle());
        holder.productRateNumTV.setText(String.format("(%d)",products.get(position).getReviewsCount()));
        holder.productPriceTV.setText(String.format("%.2f",price));
        holder.productCountTV.setText(String.format("%d%s",quantity," in stock"));
        holder.productPriceWithoutOfferTV.setText(String.format("%.2f",products.get(position).getPrice()));
        holder.productRateBar.setRating(products.get(position).getReviewsRateAverage());

        if(offer > 0){
            holder.productOfferTV.setVisibility(View.VISIBLE);
            holder.productPriceWithoutOfferTV.setVisibility(View.VISIBLE);
        }else {
            holder.productOfferTV.setVisibility(View.INVISIBLE);
            holder.productPriceWithoutOfferTV.setVisibility(View.INVISIBLE);
        }

        if(quantity <= 10){
            holder.productCountTV.setVisibility(View.VISIBLE);
        }else {
            holder.productCountTV.setVisibility(View.INVISIBLE);
        }

        holder.addToCartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "product title: " + products.get(position).getTitle());
            }
        });

        holder.saveInCartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.saveInCartBt.isChecked()){
                    listener.onCartClick(view, products.get(position).getId());
                    holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_cart_added);
                    Log.d(TAG, "product cart on: " + products.get(position).getTitle());
                }else {
                    Log.d(TAG, "product cart off: " + products.get(position).getTitle());
                    holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_add_cart);
                }
            }
        });
        holder.saveInWishListBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.saveInWishListBt.isChecked()){
                    holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_launcher_wish_list_added);
                    Log.d(TAG, "product wish on: " + products.get(position).getTitle());
                }else {
                    holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_launcher_add_wish_list);
                    Log.d(TAG, "product wish off: " + products.get(position).getTitle());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setList(ArrayList<MergeModel> mergeModels) {
        this.products = mergeModels;
        notifyDataSetChanged();
    }

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView productOfferTV, productTitleTV, productRateNumTV, productPriceTV, productCountTV,
                productPriceWithoutOfferTV;
        private RatingBar productRateBar;
        Button addToCartBt;
        ToggleButton saveInCartBt, saveInWishListBt;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            productOfferTV = itemView.findViewById(R.id.product_offer);
            productTitleTV = itemView.findViewById(R.id.product_title);
            productRateNumTV = itemView.findViewById(R.id.product_rate_num);
            productPriceTV = itemView.findViewById(R.id.product_price);
            productCountTV = itemView.findViewById(R.id.product_count);
            productPriceWithoutOfferTV = itemView.findViewById(R.id.product_price_without_offer);
            productRateBar = itemView.findViewById(R.id.product_rate);
            addToCartBt = itemView.findViewById(R.id.addToCart_bt);
            saveInCartBt = itemView.findViewById(R.id.saveInCart_bt);
            saveInWishListBt = itemView.findViewById(R.id.saveInWishList_bt);

        }
    }
}
