package com.example.ecommerceapp.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.databinding.LayoutSortBinding;
import com.example.ecommerceapp.databinding.LayoutSortReviewBinding;

import androidx.annotation.NonNull;

public class SortDialog extends Dialog implements View.OnClickListener {

    private LayoutSortBinding bindingSortProducts;
    private LayoutSortReviewBinding bindingSortReviews;
    private View view;
    private Activity activity;
    private Context context;
    private ItemDialogClickListener listener;
    private int typeSort;
    private static SortDialog INSTANCE;

    private SortDialog(@NonNull Context context, Activity activity, int themeResId,
                      ItemDialogClickListener listener, int typeSort) {
        super(context, themeResId);
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.typeSort = typeSort;
    }

    public synchronized static SortDialog getINSTANCE(Context context, Activity activity, int themeResId,
                                         ItemDialogClickListener listener, int typeSort){
        INSTANCE = new SortDialog(context, activity, themeResId, listener, typeSort);
        INSTANCE.setCancelable(true);
        INSTANCE.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        INSTANCE.getWindow().setGravity(Gravity.BOTTOM);
        INSTANCE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(typeSort == R.string.products) {
            view = getLayoutInflater().inflate(R.layout.layout_sort, null);
            bindingSortProducts = LayoutSortBinding.bind(view);
            setContentView(view);

            bindingSortProducts.sortBestMatches.setOnClickListener(this);
            bindingSortProducts.sortPriceLow.setOnClickListener(this);
            bindingSortProducts.sortPriceHigh.setOnClickListener(this);
            bindingSortProducts.sortOfferLow.setOnClickListener(this);
            bindingSortProducts.sortOfferHigh.setOnClickListener(this);
            bindingSortProducts.sortTopRated.setOnClickListener(this);
            bindingSortProducts.sortNew.setOnClickListener(this);

        }else if (typeSort == R.string.reviews){
            view = getLayoutInflater().inflate(R.layout.layout_sort_review, null);
            bindingSortReviews = LayoutSortReviewBinding.bind(view);
            setContentView(view);

            bindingSortReviews.sortMostHelpful.setOnClickListener(this);
            bindingSortReviews.sortMostRecent.setOnClickListener(this);
            bindingSortReviews.sortHighestRatings.setOnClickListener(this);
            bindingSortReviews.sortLowestRatings.setOnClickListener(this);
        }


    }

    private void setListen(View view, int id){
        dismiss();
        listener.onItemDialogClick(view, id);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sort_best_matches:
                setListen(view, R.string.bestMatches);
                break;
            case R.id.sort_price_low:
                setListen(view, R.string.lowToHighPrice);
                break;
            case R.id.sort_price_high:
                setListen(view, R.string.highToLowPrice);
                break;
            case R.id.sort_offer_low:
                setListen(view, R.string.lowToHighOffer);
                break;
            case R.id.sort_offer_high:
                setListen(view, R.string.highToLowOffer);
                break;
            case R.id.sort_top_rated:
                setListen(view, R.string.topRated);
                break;
            case R.id.sort_new:
                setListen(view, R.string.newArrivals);
                break;

            case R.id.sort_most_helpful:
                setListen(view, R.string.mostHelpful);
                break;
            case R.id.sort_most_recent:
                setListen(view, R.string.mostRecent);
                break;
            case R.id.sort_highest_ratings:
                setListen(view, R.string.highestRatings);
                break;
            case R.id.sort_lowest_ratings:
                setListen(view, R.string.lowestRatings);
                break;

            default:
                break;
        }
    }
}
