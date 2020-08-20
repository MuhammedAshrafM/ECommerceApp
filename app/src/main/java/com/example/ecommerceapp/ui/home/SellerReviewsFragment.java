package com.example.ecommerceapp.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentReviewsBinding;
import com.example.ecommerceapp.databinding.FragmentSellerReviewsBinding;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.SellerModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerReviewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private HomeViewModel homeViewModel;
    private FragmentSellerReviewsBinding binding;
    private ArrayList<ReviewModel> reviews;
    private SellerReviewsAdapter adapter;
    private Utils utils;
    private View root;
    private Context context;
    private Activity activity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "seller";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private SellerModel seller;
    private String mParam2;

    public SellerReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerReviewsFragment newInstance(SellerModel param1, String param2) {
        SellerReviewsFragment fragment = new SellerReviewsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();

        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            seller = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_seller_reviews,container,false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding.sellerNameTv.setText(seller.getName());
    }

    @Override
    public void onStart() {
        super.onStart();

        handleUi();

        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            homeViewModel.getSellerReviews(seller.getId());
        } else {
            displaySnackBar(true, null, 0);
        }

        observeLiveData();
    }

    private void observeLiveData(){
        homeViewModel.getReviews().observe(getViewLifecycleOwner(), new Observer<ArrayList<ReviewModel>>() {
            @Override
            public void onChanged(ArrayList<ReviewModel> reviewModels) {
                displayProgressDialog(false);
                if (reviewModels != null && reviewModels.size() > 0) {
                    reviews = reviewModels;
                    adapter.setList(reviews);
                }
            }
        });

        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0);
            }
        });


        adapter.getReviewsInfo().observe(getViewLifecycleOwner(), new Observer<Map<String, Integer>>() {
            @Override
            public void onChanged(Map<String, Integer> stringObjectMap) {
                setReviewRate(stringObjectMap);
            }
        });
    }
    private void handleUi(){
        binding.recyclerViewSellerReviews.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SellerReviewsAdapter(context, this);
        binding.recyclerViewSellerReviews.setAdapter(adapter);

        binding.positiveRateTv.setText(String.format(Locale.getDefault(),"%d %s",0, getString(R.string.percent)));
        binding.negativeRateTv.setText(String.format(Locale.getDefault(), "(%d %s)",0, getString(R.string.percent)));

        binding.positivePb.setProgress(0);
        binding.negativePb.setProgress(0);

        binding.numRatings.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.totalRating), 0));

        binding.swipeRefresh.setColorSchemeColors(Color.rgb(3,169,244),
                Color.rgb(3,169,244),
                Color.rgb(13,179,163));
        binding.swipeRefresh.setOnRefreshListener(this);
    }
    private void displayProgressDialog(boolean show) {
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerSellerReviews), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }


    private void setReviewRate(Map<String, Integer> infoReviews){
            int positiveReviewsCount = infoReviews.get("positiveReviewsCount");
            int negativeReviewsCount = infoReviews.get("negativeReviewsCount");

            binding.positiveRateTv.setText(String.format(Locale.getDefault(),"%d %s",
                    positiveReviewsCount, getString(R.string.percent)));
            binding.negativeRateTv.setText(String.format(Locale.getDefault(), "%d %s",
                    negativeReviewsCount, getString(R.string.percent)));

            binding.positivePb.setProgress(positiveReviewsCount);
            binding.negativePb.setProgress(negativeReviewsCount);

            binding.numRatings.setText(String.format(Locale.getDefault(), "%s %d",
                    getString(R.string.totalRating), reviews.size()));

    }

    @Override
    public void onRefresh() {
        if (ConnectivityReceiver.isConnected()) {
            binding.swipeRefresh.setRefreshing(true);
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefresh.setRefreshing(false);
                    displayProgressDialog(true);
                    homeViewModel.getReviews(seller.getId());
                }}, 3000);

        }else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null, 0);
        }
    }

}