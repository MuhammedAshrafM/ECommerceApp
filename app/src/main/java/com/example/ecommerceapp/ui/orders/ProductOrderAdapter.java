package com.example.ecommerceapp.ui.orders;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SellerModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ProductCartedViewHolder> {

    private ArrayList<ProductModel> products = new ArrayList<>();
    private Context context;
    private DecimalFormat decimalFormat = new DecimalFormat();

    public ProductOrderAdapter(Context context) {
        this.context = context;
        decimalFormat.applyPattern("#,###,###,###.##");
    }

    @NonNull
    @Override
    public ProductCartedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductCartedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_product_order, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ProductCartedViewHolder holder, int position) {
        double price = products.get(position).getPrice() * ((100 - products.get(position).getOffer())/100);
        float offer = products.get(position).getOffer();
        int quantity = products.get(position).getQuantity();

        GlideClient.loadCategoryImage(context, products.get(position).getImagePath(), holder.imageView);
        holder.productTitleTV.setText(products.get(position).getTitle());
        holder.productPriceTV.setText(decimalFormat.format(price) + " " + context.getString(R.string.egp));
        holder.quantityTV.setText(String.format(Locale.getDefault(),"%s%d", context.getString(R.string.Qty), quantity));
        holder.productPriceWithoutOfferTV.setText(decimalFormat.format(products.get(position).getPrice()) + " "
                + context.getString(R.string.egp));

        if(offer > 0){
            holder.productPriceWithoutOfferTV.setVisibility(View.VISIBLE);
            holder.productPriceWithoutOfferTV.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.productPriceWithoutOfferTV.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setList(ArrayList<ProductModel> productModels) {
        this.products = productModels;
        notifyDataSetChanged();
    }



    public class ProductCartedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView productTitleTV, productPriceTV, productPriceWithoutOfferTV, quantityTV;

        private ProductCartedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            productTitleTV = itemView.findViewById(R.id.product_title);
            productPriceTV = itemView.findViewById(R.id.product_price);
            productPriceWithoutOfferTV = itemView.findViewById(R.id.product_price_without_offer);
            quantityTV = itemView.findViewById(R.id.quantity_tv);

        }

    }
}
