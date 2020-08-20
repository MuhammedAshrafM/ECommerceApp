package com.example.ecommerceapp.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SellerModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductOrderedAdapter extends RecyclerView.Adapter<ProductOrderedAdapter.ProductCartedViewHolder> {

    private ArrayList<ProductModel> products = new ArrayList<>();
    private ArrayList<Integer> counts = new ArrayList<>();
    private ArrayList<SellerModel> sellers = new ArrayList<>();
    private Context context;
    private ItemClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat();

    public ProductOrderedAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        decimalFormat.applyPattern("#,###,###,###.##");
    }

    @NonNull
    @Override
    public ProductCartedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductCartedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_product_ordered, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ProductCartedViewHolder holder, int position) {
        double price = products.get(position).getPrice() * ((100 - products.get(position).getOffer())/100);
        double shippingFee = (double) products.get(position).getShippingFee();
        int quantity = products.get(position).getQuantity();
        int count = counts.get(position);

        GlideClient.loadCategoryImage(context, products.get(position).getImagePath(), holder.imageView);
        holder.productTitleTV.setText(products.get(position).getTitle());
        holder.productPriceTV.setText(decimalFormat.format(price) + " " + context.getString(R.string.egp));
        holder.sellerTV.setText(sellers.get(position).getName());
        holder.quantityTV.setText(String.format(Locale.getDefault(),"%s%d", context.getString(R.string.Qty), count));

        if(shippingFee == 0){
            holder.productShippingFeeTV.setText(context.getString(R.string.freeShipping));
        }else {
            holder.productShippingFeeTV.setText(decimalFormat.format(shippingFee) + " " + context.getString(R.string.egp));
        }

        listener.onCartView(shippingFee, true);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setList(ArrayList<ProductModel> productModels, ArrayList<Integer> countProducts,
                        ArrayList<SellerModel> sellerModels) {

        this.products = productModels;
        this.sellers = sellerModels;
        this.counts = countProducts;
        notifyDataSetChanged();
    }



    public class ProductCartedViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView productShippingFeeTV, productTitleTV, productPriceTV, sellerTV, quantityTV;

        private ProductCartedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            productShippingFeeTV = itemView.findViewById(R.id.shipping_fee_tv);
            productTitleTV = itemView.findViewById(R.id.product_title);
            productPriceTV = itemView.findViewById(R.id.product_price);
            sellerTV = itemView.findViewById(R.id.seller_tv);
            quantityTV = itemView.findViewById(R.id.quantity_tv);

        }

    }
}
