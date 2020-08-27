package com.example.ecommerceapp.ui.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkRequest;
import androidx.navigation.NavDirections;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.pojo.NotificationModel;
import com.example.ecommerceapp.pojo.OrderModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private NavController navController;
    private List<NotificationModel> notifications = new ArrayList<>();
    private Context context;

    public NotificationsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_notification, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {

        holder.containerCV.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));

        String summaryText = notifications.get(position).getSummaryText();

        GlideClient.loadNotificationImage(context, notifications.get(position).getImagePath(), holder.imageView);
        holder.titleTv.setText(notifications.get(position).getTitle());
        holder.bodyTv.setText(notifications.get(position).getBody());
        if(summaryText != null){
            holder.summaryTextTv.setText(summaryText);
        }else {
            holder.summaryTextTv.setVisibility(View.GONE);
        }
        holder.timeTv.setText(notifications.get(position).getTime());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int destination = notifications.get(position).getDestination();
                if(destination != 0){
                    navController = Navigation.findNavController(view);
                    String json = notifications.get(position).getJson();
                    switch (destination){
                        case R.string.deepLinkOrderDetails:
                            break;

                        case  R.string.deepLinkProduct:
                            Gson gson = new Gson();
                            Type type = new TypeToken<ProductModel>(){}.getType();
                            ProductModel product = gson.fromJson(json, type);
                            json = product.getId() + "?productTitle=" + product.getTitle() + "&sellerId=" +
                                    product.getSellerId() + "&subCategoryId=" + product.getSubCategoryId();
                            break;
                    }

                    Uri uri = Uri.parse(context.getString(destination) + json);
                    navController.navigate(uri);
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
        return notifications.size();
    }

    public void setList(List<NotificationModel> NotificationModels) {
        this.notifications = NotificationModels;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        notifications.remove(position);
        notifyDataSetChanged();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView containerCV;
        private TextView titleTv, bodyTv, summaryTextTv, timeTv;
        private ImageView imageView;
        public ConstraintLayout foregroundCola;

        private ItemClickListener itemClickListener;

        private NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);

            containerCV = itemView.findViewById(R.id.container_item_notification);
            imageView = itemView.findViewById(R.id.notification_image);
            titleTv = itemView.findViewById(R.id.notification_title);
            bodyTv = itemView.findViewById(R.id.notification_body);
            summaryTextTv = itemView.findViewById(R.id.notification_summary_text);
            timeTv = itemView.findViewById(R.id.notification_time);
            foregroundCola = itemView.findViewById(R.id.foreground_cola);

            itemView.setOnClickListener(this);
        }

        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, this.getAdapterPosition());
        }
    }
}
