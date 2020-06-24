package com.example.ecommerceapp.ui.more;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
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
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.pojo.ProductModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class ProductWishedAdapter extends RecyclerView.Adapter<ProductWishedAdapter.ProductViewHolder> {

    private NavController navController;
    private ArrayList<ProductModel> products = new ArrayList<>();
    private Context context;
    private ItemClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat();
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_PRODUCTS_WISHED = "PRODUCTS_WISHED";

    public ProductWishedAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        decimalFormat.applyPattern("#,###,###,###.##");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_product, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        double price = products.get(position).getPrice() * ((100 - products.get(position).getOffer())/100);
        float offer = products.get(position).getOffer();
        int quantity = products.get(position).getQuantity();

        PicassoClient.loadCategoryImage(context, products.get(position).getImagePath(), holder.imageView);
        holder.productOfferTV.setText(String.format("%.0f%s",offer, context.getString(R.string.off_percent)));
        holder.productTitleTV.setText(products.get(position).getTitle());
        holder.productRateNumTV.setText(String.format("(%d)",products.get(position).getReviewsCount()));
        holder.productPriceTV.setText(decimalFormat.format(price) + " " + context.getString(R.string.egp));
        holder.productCountTV.setText(String.format("%d%s",quantity," in stock"));
        holder.productPriceWithoutOfferTV.setText(decimalFormat.format(products.get(position).getPrice()) + " "
                + context.getString(R.string.egp));
        holder.productRateBar.setRating(products.get(position).getReviewsRateAverage());

        holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wished);
        holder.saveInWishListBt.setChecked(true);

        if(offer > 0){
            holder.productOfferTV.setVisibility(View.VISIBLE);
            holder.productPriceWithoutOfferTV.setVisibility(View.VISIBLE);
            holder.productPriceWithoutOfferTV.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.productOfferTV.setVisibility(View.INVISIBLE);
            holder.productPriceWithoutOfferTV.setVisibility(View.INVISIBLE);
        }

        if(quantity <= 10){
            holder.productCountTV.setVisibility(View.VISIBLE);
        }else {
            holder.productCountTV.setVisibility(View.INVISIBLE);
        }


        if(Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).isProductCarted(products.get(position))){
            holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_cart_added);
            holder.saveInCartBt.setChecked(true);
        }else {
            holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_add_cart);
            holder.saveInCartBt.setChecked(false);
        }


        holder.addToCartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.saveInCartBt.isChecked()){
                    listener.onCartClick(view, products.get(position), true, 0);
                    holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_cart_added);
                }
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_wishFragment_to_navigation_cart);
            }
        });

        holder.saveInCartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.saveInCartBt.isChecked()){
                    listener.onCartClick(view, products.get(position), true, 0);
                    holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_cart_added);
                }else {
                    listener.onCartClick(view, products.get(position), false, 0);
                    holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_add_cart);
                }
            }
        });
        holder.saveInWishListBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWishClick(view, products.get(position),false);
            }
        });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("product", products.get(position));
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_wishFragment_to_productFragment, bundle);
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
        return products.size();
    }

    public void setList(ArrayList<ProductModel> productModels) {
        this.products = productModels;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        private ImageView imageView;
        private TextView productOfferTV, productTitleTV, productRateNumTV, productPriceTV, productCountTV,
                productPriceWithoutOfferTV;
        private RatingBar productRateBar;
        Button addToCartBt;
        ToggleButton saveInCartBt, saveInWishListBt;

        private ItemClickListener itemClickListener;

        public ProductViewHolder(@NonNull View itemView) {
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
