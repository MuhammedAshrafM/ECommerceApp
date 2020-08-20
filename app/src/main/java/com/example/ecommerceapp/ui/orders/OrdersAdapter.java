package com.example.ecommerceapp.ui.orders;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.pojo.OrderModel;
import com.example.ecommerceapp.pojo.ProductModel;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.android.volley.VolleyLog.TAG;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private NavController navController;
    private ArrayList<OrderModel> orders = new ArrayList<>();
    private Context context;
    private DateFormat dateFormat;
    private Date date;

    public OrdersAdapter(Context context) {
        this.context = context;
        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        holder.containerCV.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));

        int received = orders.get(position).getReceived();
//        date = new Date(Long.valueOf(orders.get(position).getOrderTime()));
//        String orderTime = dateFormat.format(date);
        String orderCode = orders.get(position).getId().substring(3);
        String orderStatus;

        if(received == 0){
            holder.orderDeliveredIbt.setVisibility(View.INVISIBLE);
            holder.orderPreparedPb.setVisibility(View.VISIBLE);
            orderStatus = context.getString(R.string.orderPrepared);
        }else {
            holder.orderPreparedPb.setVisibility(View.INVISIBLE);
            holder.orderDeliveredIbt.setVisibility(View.VISIBLE);
            orderStatus = String.format(Locale.getDefault(),"%s %s", context.getString(R.string.orderDelivered), "Muhammed Ashraf");
        }

        holder.orderStatusTV.setText(orderStatus);
        holder.orderCodeTV.setText(String.format(Locale.getDefault(),"%s %s", context.getString(R.string.orderCode), orderCode));
        holder.orderTimeTV.setText(orders.get(position).getOrderTime());


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", orders.get(position));
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_orders_to_orderDetailsFragment, bundle);
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
        return orders.size();
    }

    public void setList(ArrayList<OrderModel> orderModels) {
        this.orders = orderModels;
        notifyDataSetChanged();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView containerCV;
        private ProgressBar orderPreparedPb;
        private TextView orderStatusTV, orderCodeTV, orderTimeTV;
        private ImageButton orderDeliveredIbt;

        private ItemClickListener itemClickListener;

        private OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            containerCV = itemView.findViewById(R.id.container_item_order);
            orderPreparedPb = itemView.findViewById(R.id.order_prepared_pb);
            orderStatusTV = itemView.findViewById(R.id.order_status_tv);
            orderCodeTV = itemView.findViewById(R.id.order_code_tv);
            orderTimeTV = itemView.findViewById(R.id.order_time_tv);
            orderDeliveredIbt = itemView.findViewById(R.id.order_delivered_ibt);

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
