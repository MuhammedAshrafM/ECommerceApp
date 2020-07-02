package com.example.ecommerceapp.ui.home;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.SortDialog;
import com.example.ecommerceapp.data.ItemDialogClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment implements View.OnClickListener, ItemDialogClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener{

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "product";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    // TODO: Rename and change types of parameters
    private ProductModel productModel;
    private String mParam2;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
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
            productModel = getArguments().getParcelable(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
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

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        binding.recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewAdapter(getContext());
        binding.recyclerViewReviews.setAdapter(adapter);

        binding.sortByBt.setOnClickListener(this);
        binding.sortByTv.setOnClickListener(this);
        binding.addReviewFAB.setOnClickListener(this);

        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            homeViewModel.getReviews(productModel.getId());
        } else {
            displaySnackBar(true, null, 0);
        }

        user = Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).getDataUser();
        binding.numItems.setText(getResources().getQuantityString(R.plurals.numberOfReviewsAvailable,0,0));
        binding.reviewRateTv.setText(String.format(Locale.getDefault(),"%.1f %s",0.0, getString(R.string.stars)));
        binding.reviewNumRateTv.setText(String.format(Locale.getDefault(), "(%d %s)",0, getString(R.string.ratings)));
    }


    @Override
    public void onStart() {
        super.onStart();

        menu = null;

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
                if(userModels != null && userModels.size() > 0){
                    users = userModels;
                    adapter.setList(reviews, userModels);
                    setViewSortDialog();
                }
            }
        });
        homeViewModel.getInfoReviews().observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> map) {
                displayProgressDialog(false);
                setReviewRate(map);
            }
        });


        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0);
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
        utils.snackBar(root.findViewById(R.id.containerReviews), msg, duration);
        utils.displaySnackBar(show);
    }

    private void setViewSortDialog(){
        dialog = dialog.getINSTANCE(getContext(), getActivity(), R.style.MaterialDialogSheet, this, R.string.reviews);
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

    private ArrayList<UserModel> getNewSort(ArrayList<ReviewModel> reviewModels, ArrayList<UserModel> userModels){
        ArrayList<UserModel> newUserModels = new ArrayList<>();

        for(int i=0; i<reviewModels.size(); i++){
            int index = userModels.indexOf(reviewModels.get(i).getUserId());
            newUserModels.set(i,userModels.get(index));
        }

        return newUserModels;
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

            int sizeProductsCarted = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).getProductsCarted().size();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(), sizeProductsCarted,R.drawable.ic_cart));
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
}
