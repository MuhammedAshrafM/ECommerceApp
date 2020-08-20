package com.example.ecommerceapp.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.SortDialog;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentReviewsBinding;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment implements View.OnClickListener, ItemDialogClickListener,
        SwipeRefreshLayout.OnRefreshListener, ConnectivityReceiver.ConnectivityReceiveListener {

    private NavController navController;
    private HomeViewModel homeViewModel;
    private FragmentReviewsBinding binding;
    private ArrayList<ReviewModel> reviews;
    private ArrayList<UserModel> users;
    private View root;
    private Menu menu;
    private SortDialog dialog = null;
    private MenuItem cartMenuItem;
    private ReviewAdapter adapter;
    private Utils utils;
    private UserModel user;
    private Context context;
    private Activity activity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "product";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    // TODO: Rename and change types of parameters
    private ProductModel productModel;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        context = getContext();

        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            productModel = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reviews,container,false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(getString(R.string.reviews));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        selectActivity();
    }


    @Override
    public void onStart() {
        super.onStart();

        menu = null;

        handleUi();

        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            homeViewModel.getReviews(productModel.getId());
        } else {
            displaySnackBar(true, null, 0);
        }

        observeLiveData();
        setOnClickListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        // register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        context.registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityReceiveListener(this);

    }

    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
        }
    }

    private void observeLiveData(){

        homeViewModel.getReviews().observe(getViewLifecycleOwner(), new Observer<ArrayList<ReviewModel>>() {
            @Override
            public void onChanged(ArrayList<ReviewModel> reviewModels) {
                if (reviewModels != null && reviewModels.size() > 0) {
                    reviews = reviewModels;
                    binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfReviewsAvailable,reviews.size(),reviews.size()));
                }
            }
        });
        homeViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                displayProgressDialog(false);
                if(userModels != null && userModels.size() > 0){
                    users = userModels;
                    adapter.setList(reviews, userModels);
                    setViewSortDialog();
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

        adapter.getReviewsInfo().observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {
                setReviewRate(stringObjectMap);
            }
        });
    }
    private void setOnClickListener(){
        binding.sortByBt.setOnClickListener(this);
        binding.sortByTv.setOnClickListener(this);
        binding.addReviewFAB.setOnClickListener(this);
    }
    private void handleUi(){
        binding.recyclerViewReviews.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ReviewAdapter(context, this);
        binding.recyclerViewReviews.setAdapter(adapter);

        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();
        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfReviewsAvailable,0,0));
        binding.reviewRateTv.setText(String.format(Locale.getDefault(),"%.1f %s",0.0, getString(R.string.stars)));
        binding.reviewNumRateTv.setText(String.format(Locale.getDefault(), "(%d %s)",0, getString(R.string.ratings)));


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
        utils.snackBar(root.findViewById(R.id.containerReviews), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    private void setViewSortDialog(){
        dialog = dialog.getINSTANCE(context, activity, R.style.MaterialDialogSheet, this, R.string.reviews);
    }

    private void setReviewRate(Map<String, Object> infoReviews){
        if(!infoReviews.get("reviewsCount").toString().equals("0")){
            binding.reviewRateBar.setRating(Float.valueOf(infoReviews.get("reviewsRateAverage").toString()));
            binding.reviewRateTv.setText(String.format(Locale.getDefault(), "%.1f %s",Float.valueOf(infoReviews.get("reviewsRateAverage").toString()), getString(R.string.stars)));
            binding.reviewNumRateTv.setText(String.format(Locale.getDefault(), "(%d %s)",Integer.valueOf(infoReviews.get("reviewsCount").toString()), getString(R.string.ratings)));
        }
    }


    private void setSortReviews(int id, Comparator<ReviewModel> comparator){
        Collections.sort(reviews, comparator);
        users = ReviewModel.sortList(reviews, users);
        adapter.setList(reviews, users);
        binding.sortByTv.setText(getString(id));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(this.menu == null) {
            binding.toolbar.inflateMenu(R.menu.cart_menu);
            binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });

            this.menu = binding.toolbar.getMenu();
            cartMenuItem = this.menu.findItem(R.id.myCart);

            int sizeProductsCarted = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).getProductsCarted().size();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(context, sizeProductsCarted,R.drawable.ic_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myCart){
            navController.navigate(R.id.action_reviewsFragment_to_navigation_cart);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sortBy_bt: case R.id.sortBy_tv:
                if(dialog != null) {
                    dialog.show();
                }
                break;
            case R.id.add_review_fAB:
                if(user.getValidated() == 0){
                    displaySnackBar(true, getString(R.string.add_review_info), -2);
                    navController.navigate(R.id.action_reviewsFragment_to_profileFragment);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("productId", productModel.getId());
                    navController.navigate(R.id.action_reviewsFragment_to_addReviewFragment, bundle);
                }

                break;

            default:
                activity.onBackPressed();
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
    public void onItemDialogClick(View view, int id) {
        switch (id){

            case R.string.mostHelpful:
                setSortReviews(R.string.mostHelpful, ReviewModel.mostHelpfulComparator);
                break;
            case R.string.mostRecent:
                setSortReviews(R.string.mostRecent, ReviewModel.mostRecentComparator);
                break;
            case R.string.highestRatings:
                setSortReviews(R.string.highestRatings, ReviewModel.highestRatingComparator);
                break;
            case R.string.lowestRatings:
                setSortReviews(R.string.lowestRatings, ReviewModel.lowestRatingComparator);
                break;

            default:

                break;
        }
    }

    @Override
    public void onRefresh() {
        if (ConnectivityReceiver.isConnected()) {
            binding.swipeRefresh.setRefreshing(true);
            menu = null;
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefresh.setRefreshing(false);
                    displayProgressDialog(true);
                    homeViewModel.getReviews(productModel.getId());
                }}, 3000);

        }else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null, 0);
        }
    }

}
