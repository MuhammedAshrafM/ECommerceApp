package com.example.ecommerceapp.ui.home;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.pojo.ProductModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class SpecialProductsAdapter extends RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductViewHolder> {

    private NavController navController;
    private ArrayList<ProductModel> products = new ArrayList<>();
    private Context context;
    private ItemClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat();
    private boolean home;
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_PRODUCTS_WISHED = "PRODUCTS_WISHED";

    public SpecialProductsAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        decimalFormat.applyPattern("#,###,###,###.##");
    }

    @NonNull
    @Override
    public SpecialProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpecialProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_special_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialProductViewHolder holder, int position) {
        double price = products.get(position).getPrice() * ((100 - products.get(position).getOffer())/100);
        float offer = products.get(position).getOffer();

        GlideClient.loadCategoryImage(context, products.get(position).getImagePath(), holder.imageView);
        holder.productOfferTV.setText(String.format(Locale.getDefault(),"%.0f%s",offer, context.getString(R.string.off_percent)));
        holder.productTitleTV.setText(products.get(position).getTitle());
        holder.productPriceTV.setText(decimalFormat.format(price) + " " + context.getString(R.string.egp));
        holder.productPriceWithoutOfferTV.setText(decimalFormat.format(products.get(position).getPrice()) + " "
                + context.getString(R.string.egp));

        if(offer > 0){
            holder.productOfferTV.setVisibility(View.VISIBLE);
            holder.productPriceWithoutOfferTV.setVisibility(View.VISIBLE);
            holder.productPriceWithoutOfferTV.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.productOfferTV.setVisibility(View.INVISIBLE);
            holder.productPriceWithoutOfferTV.setVisibility(View.INVISIBLE);
        }


        if(Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).isProductCarted(products.get(position))){
            holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_cart_added);
            holder.saveInCartBt.setChecked(true);
        }else {
            holder.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_add_cart);
            holder.saveInCartBt.setChecked(false);
        }

        if(Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_WISHED).isProductWished(products.get(position))){
            holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wished);
            holder.saveInWishListBt.setChecked(true);
        }else {
            holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wish);
            holder.saveInWishListBt.setChecked(false);
        }

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
                if(holder.saveInWishListBt.isChecked()){
                    Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_WISHED).setProductWished(products.get(position));
                    holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wished);
                }else {
                    Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_WISHED).removeProductWished(products.get(position));
                    holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wish);
                }
            }
        });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("product", products.get(position));
                navController = Navigation.findNavController(view);
                if(home){
                    navController.navigate(R.id.action_navigation_home_to_productFragment, bundle);

                }else {
                    navController.navigate(R.id.action_productFragment_self, bundle);
                }
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

    public void setList(ArrayList<ProductModel> productModels, boolean home) {
        this.products = productModels;
        this.home = home;
        notifyDataSetChanged();
    }

    public class SpecialProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView productOfferTV, productTitleTV, productPriceTV,
                productPriceWithoutOfferTV;
        private ToggleButton saveInCartBt, saveInWishListBt;

        private ItemClickListener itemClickListener;

        private SpecialProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            productOfferTV = itemView.findViewById(R.id.product_offer);
            productTitleTV = itemView.findViewById(R.id.product_title);
            productPriceTV = itemView.findViewById(R.id.product_price);
            productPriceWithoutOfferTV = itemView.findViewById(R.id.product_price_without_offer);
            saveInCartBt = itemView.findViewById(R.id.saveInCart_bt);
            saveInWishListBt = itemView.findViewById(R.id.saveInWishList_bt);

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
