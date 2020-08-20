package com.example.ecommerceapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<ReviewModel> reviews = new ArrayList<>();
    private ArrayList<UserModel> users = new ArrayList<>();
    private Context context;
    private int reviewsCount;
    private float rate;
    private Double reviewsRateAverage;
    private Map<String, Object> infoReviews;
    private DateFormat dateFormat;
    private Date date;
    private InfoReviewsViewModel infoReviewsViewModel;

    public ReviewAdapter(Context context) {
        this.context = context;
        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
    }
    public ReviewAdapter(Context context, ViewModelStoreOwner owner) {
        this.context = context;
        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        infoReviewsViewModel = new ViewModelProvider(owner).get(InfoReviewsViewModel.class);
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        date = new Date(Long.valueOf(reviews.get(position).getTime()));
        rate = reviews.get(position).getRate();

        holder.userTV.setText(users.get(position).getName());
        holder.timeTV.setText(dateFormat.format(date));
        holder.commentTV.setText(reviews.get(position).getComment());
        holder.reviewRateBar.setRating(rate);

        if(infoReviewsViewModel != null) {
            reviewsCount++;
            reviewsRateAverage += rate;
            if (reviewsCount == reviews.size()) {
                infoReviewsViewModel.setMutableLiveReviewsInfo(setReviewsInfo());
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public LiveData<Map<String, Object>> getReviewsInfo() {
        return infoReviewsViewModel.getMutableLiveReviewsInfo();
    }

    private Map<String, Object> setReviewsInfo(){
        reviewsRateAverage /= reviewsCount;
        infoReviews.put("reviewsCount", reviewsCount);
        infoReviews.put("reviewsRateAverage", reviewsRateAverage);
        return infoReviews;
    }

    public void setList(ArrayList<ReviewModel> reviews, ArrayList<UserModel> users) {

        rate = 0;
        reviewsCount = 0;
        reviewsRateAverage = 0.0;
        infoReviews = new HashMap<>();
        this.reviews = reviews;
        this.users = users;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        private TextView userTV, timeTV, commentTV;
        private RatingBar reviewRateBar;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.review_user);
            timeTV = itemView.findViewById(R.id.review_time);
            commentTV = itemView.findViewById(R.id.review_comment);
            reviewRateBar = itemView.findViewById(R.id.review_rate_bar);

        }

    }
}
