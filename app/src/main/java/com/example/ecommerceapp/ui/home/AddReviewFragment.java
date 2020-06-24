package com.example.ecommerceapp.ui.home;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentAddReviewBinding;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.UserModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddReviewFragment extends Fragment implements View.OnClickListener, TextWatcher,
        ConnectivityReceiver.ConnectivityReceiveListener, RatingBar.OnRatingBarChangeListener {

    private HomeViewModel homeViewModel;
    private FragmentAddReviewBinding binding;
    private View root;
    private Utils utils;
    private UserModel user;
    private float reviewRate = 0;
    private String reviewComment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "productId";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    // TODO: Rename and change types of parameters
    private String productId;
    private String mParam2;

    public AddReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddReviewFragment newInstance(String param1, String param2) {
        AddReviewFragment fragment = new AddReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_review,container,false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(getString(R.string.writeReview));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        binding.toolbar.setNavigationOnClickListener(this);

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        user = Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).getDataUser();

        binding.reviewRateBar.setOnRatingBarChangeListener(this);
        binding.addReviewBt.setOnClickListener(this);

        binding.reviewCommentEt.addTextChangedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        homeViewModel.addReview().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                if(s != null) {
                    responseData(s);
                }else {
                    displaySnackBar(true, null, 0);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        getContext().registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityReceiveListener(this);

    }

    private void responseData(String response){
        String message = "";
        if(response.equals("Success")){
            message = getString(R.string.reviewAdded);
            displaySnackBar(true, message, 0);
            getActivity().onBackPressed();
        }
        else if(response.equals("Failed")){
            message = getString(R.string.reviewField);
            displaySnackBar(true, message, -2);
        }
    }
    private void addReview(){
        if(validateReview()){
            if (ConnectivityReceiver.isConnected()) {
                ReviewModel review = new ReviewModel();
                review.setRate(reviewRate);
                review.setComment(reviewComment);
                review.setUserId(user.getId());
                displayProgressDialog(true);
                homeViewModel.addReview(review, productId);
            }else {
                displaySnackBar(true, null, 0);
            }
        }
    }

    private void getData(){
        binding.reviewCommentEt.setError(null);

        reviewComment = binding.reviewCommentEt.getText().toString().trim();
    }

    private boolean validateReview() {
        boolean cancel = false;
        View focusView = null;

        getData();

        if(reviewRate == 0){
            binding.infoRatingTv.setTextColor(Color.RED);
            focusView = binding.infoRatingTv;
            cancel = true;
        }
        else if(TextUtils.isEmpty(reviewComment)){
            binding.infoRatingTv.setTextColor(Color.rgb(161,160,160));
            binding.reviewCommentEt.setError(getString(R.string.reviewCommentField));
            focusView = binding.reviewCommentEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        binding.infoRatingTv.setTextColor(Color.rgb(161,160,160));
        return true;
    }

    private void displayProgressDialog(boolean show) {
        if (show) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }

    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(getContext());
        utils.snackBar(root.findViewById(R.id.containerAddReview), msg, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.add_review_bt:
                addReview();
                break;

            default:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        reviewRate = v;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int length = binding.reviewCommentEt.getText().length();
        binding.charNumTv.setText(length + "/500");
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
