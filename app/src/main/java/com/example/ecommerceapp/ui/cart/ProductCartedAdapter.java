package com.example.ecommerceapp.ui.cart;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
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
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class ProductCartedAdapter extends RecyclerView.Adapter<ProductCartedAdapter.ProductCartedViewHolder>
implements View.OnClickListener {

    private NavController navController;
    private ArrayList<ProductModel> products = new ArrayList<>();
    private Context context;
    private ItemClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat();
    private ArrayList<Integer> countProducts;
    private ArrayList<String> sellersId;

    private static final String PREFERENCES_PRODUCTS_WISHED = "PRODUCTS_WISHED";

    public ProductCartedAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        decimalFormat.applyPattern("#,###,###,###.##");
    }

    @NonNull
    @Override
    public ProductCartedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductCartedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_product_carted, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ProductCartedViewHolder holder, int position) {
        holder.containerCV.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));

        double price = products.get(position).getPrice() * ((100 - products.get(position).getOffer())/100);
        float offer = products.get(position).getOffer();
        int quantity = products.get(position).getQuantity();
        int count = Integer.parseInt(String.valueOf(holder.quantityTV.getText()));

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

        listener.onCartView(price * count, true);

        if(Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_WISHED).isProductWished(products.get(position))){
            holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wished);
            holder.saveInWishListBt.setChecked(true);
        }else {
            holder.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wish);
            holder.saveInWishListBt.setChecked(false);
        }

        countProducts.add(position, count);
        sellersId.add(position, products.get(position).getSellerId());

        holder.saveInCartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countProducts.remove(products.get(position));
                listener.onCartView(0, false);
                listener.onCartClick(view, products.get(position), false, 0);
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

        holder.increaseIBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(String.valueOf(holder.quantityTV.getText()));
                if(count < 99){
                    if(count < quantity) {
                        count++;
                        holder.quantityTV.setText(String.valueOf(count));
                        listener.onCartView(price, true);
                        countProducts.remove(position);
                        countProducts.add(position, count);
                    }else {
                        listener.onCartView(-1, false);
                    }
                }
            }
        });
        holder.decreaseIBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(String.valueOf(holder.quantityTV.getText()));
                if(count > 1){
                    count --;
                    holder.quantityTV.setText(String.valueOf(count));
                    listener.onCartView(price, false);
                    countProducts.remove(position);
                    countProducts.add(position, count);
                }
            }
        });


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("product", products.get(position));
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_cart_to_productFragment, bundle);
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
        countProducts = new ArrayList<>();
        sellersId = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

    }

    public ArrayList<Integer> getCountProductsCarted(){
        return countProducts;
    }

    public ArrayList<String> getSellersId(){
        return sellersId;
    }


    public class ProductCartedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView containerCV;
        private ImageView imageView;
        private TextView productOfferTV, productTitleTV, productPriceTV, productPriceWithoutOfferTV, quantityTV;
        private ToggleButton saveInCartBt, saveInWishListBt;
        private ImageButton increaseIBt, decreaseIBt;

        private ItemClickListener itemClickListener;

        private ProductCartedViewHolder(@NonNull View itemView) {
            super(itemView);
            containerCV = itemView.findViewById(R.id.container_item_product_carted);
            imageView = itemView.findViewById(R.id.product_image);
            productOfferTV = itemView.findViewById(R.id.product_offer);
            productTitleTV = itemView.findViewById(R.id.product_title);
            productPriceTV = itemView.findViewById(R.id.product_price);
            productPriceWithoutOfferTV = itemView.findViewById(R.id.product_price_without_offer);
            quantityTV = itemView.findViewById(R.id.quantity_tv);
            saveInCartBt = itemView.findViewById(R.id.saveInCart_bt);
            saveInWishListBt = itemView.findViewById(R.id.saveInWishList_bt);
            increaseIBt = itemView.findViewById(R.id.increase_ibt);
            decreaseIBt = itemView.findViewById(R.id.decrease_ibt);

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
