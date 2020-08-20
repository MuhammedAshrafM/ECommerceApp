package com.example.ecommerceapp.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.GlideClient;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.facebook.internal.FacebookDialogFragment.TAG;

public class SellerReviewsAdapter extends RecyclerView.Adapter<SellerReviewsAdapter.ReviewViewHolder> {

    private ArrayList<ReviewModel> reviews = new ArrayList<>();
    private Context context;
    private int reviewsCount;
    private float positiveReviewsCount, negativeReviewsCount;
    private float rate;
    private Map<String, Integer> infoReviews;
    private DateFormat dateFormat;
    private Date date;
    private InfoReviewsViewModel infoReviewsViewModel;

    public SellerReviewsAdapter(Context context, ViewModelStoreOwner owner) {
        this.context = context;
        infoReviewsViewModel = new ViewModelProvider(owner).get(InfoReviewsViewModel.class);
        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_feedback, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        date = new Date(Long.valueOf(reviews.get(position).getTime()));
        rate = reviews.get(position).getRate();

        holder.timeTV.setText(dateFormat.format(date));
        holder.commentTV.setText(reviews.get(position).getComment());

        if(rate > 2){
            GlideClient.loadResourceImage(context, R.mipmap.ic_mood_good, holder.reviewRate);
            positiveReviewsCount++;
        }else {
            GlideClient.loadResourceImage(context, R.mipmap.ic_mood_bad, holder.reviewRate);
            negativeReviewsCount++;
        }
        reviewsCount++;
        if (reviewsCount == reviews.size()) {
            infoReviewsViewModel.setMutableLiveReviewsInfoSeller(setReviewsInfo());
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public LiveData<Map<String, Integer>> getReviewsInfo() {
        return infoReviewsViewModel.getMutableLiveReviewsInfoSeller();
    }

    public Map<String, Integer> setReviewsInfo(){

        positiveReviewsCount = (positiveReviewsCount/reviewsCount) * 100;
        negativeReviewsCount = (negativeReviewsCount/reviewsCount) * 100;

        infoReviews.put("positiveReviewsCount", (int) positiveReviewsCount);
        infoReviews.put("negativeReviewsCount", (int) negativeReviewsCount);
        return infoReviews;
    }

    public void setList(ArrayList<ReviewModel> reviews) {
        rate = 0;
        reviewsCount = 0;
        positiveReviewsCount = 0;
        negativeReviewsCount = 0;
        infoReviews = new HashMap<>();
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        private TextView timeTV, commentTV;
        private ImageButton reviewRate;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTV = itemView.findViewById(R.id.review_time);
            commentTV = itemView.findViewById(R.id.review_comment);
            reviewRate = itemView.findViewById(R.id.review_rate);

        }

    }
}
