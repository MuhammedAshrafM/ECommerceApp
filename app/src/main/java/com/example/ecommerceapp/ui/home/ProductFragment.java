package com.example.ecommerceapp.ui.home;

import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentProductBinding;
import com.example.ecommerceapp.pojo.ImageProductModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener,
        ItemClickListener, ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private HomeViewModel homeViewModel;
    private FragmentProductBinding binding;
    private View root;
    private Menu menu;
    private MenuItem cartMenuItem;
    private ArrayList<ProductModel> suggestedProductModels;
    private ArrayList<String> productsCartedId;
    private LinearLayoutManager layoutManagerRecycler;
    private ProductPagerAdapter adapter;
    private ReviewAdapter reviewAdapter;
    private ArrayList<ReviewModel> reviews;
    private ArrayList<ImageProductModel> productImages;
    private Utils utils;
    private ImageView[] dotsIv;
    private int dotsCount;
    private DecimalFormat decimalFormat;
    private Bundle bundle;
    private UserModel user;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "product";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_PRODUCTS_WISHED = "PRODUCTS_WISHED";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    // TODO: Rename and change types of parameters
    private ProductModel product;
    private String mParam2;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
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
            product = getArguments().getParcelable(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_product,container,false);
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

        binding.toolbar.setTitle(product.getTitle());
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        layoutManagerRecycler = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewProductReviews.setHasFixedSize(true);
        binding.recyclerViewProductReviews.setLayoutManager(layoutManagerRecycler);

        reviewAdapter = new ReviewAdapter(getContext());
        binding.recyclerViewProductReviews.setAdapter(reviewAdapter);

        user = Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).getDataUser();

        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            homeViewModel.getProduct(product.getId(), user.getId());
            homeViewModel.getLimitReviews(product.getId());
            homeViewModel.getSpecialProducts(product.getSubCategoryId(), product.getId());
        } else {
            displaySnackBar(true, null, 0);
        }

        binding.viewPager.setOnPageChangeListener(this);
        binding.saveInCartBt.setOnClickListener(this);
        binding.saveInWishListBt.setOnClickListener(this);

        binding.productDescriptionBt.setOnClickListener(this);
        binding.productSpecificationsBt.setOnClickListener(this);
        binding.productReviewsTv.setOnClickListener(this);
        binding.productAddReviewLila.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        menu = null;
        manager = null;

        decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern("#,###,###,###.##");

        productImages = new ArrayList<>();
        adapter = new ProductPagerAdapter(getContext(), productImages);
        binding.viewPager.setAdapter(adapter);

        setInitialData();
        setData(product);


        homeViewModel.getProduct().observe(getViewLifecycleOwner(), new Observer<ProductModel>() {
            @Override
            public void onChanged(ProductModel productModel) {
                if (productModel != null ) {
                    product = productModel;
                    setData(product);
                }
            }
        });

        homeViewModel.getReviews().observe(getViewLifecycleOwner(), new Observer<ArrayList<ReviewModel>>() {
            @Override
            public void onChanged(ArrayList<ReviewModel> reviewModels) {
                if (reviewModels != null && reviewModels.size() > 0) {
                    reviews = reviewModels;
                }
            }
        });
        homeViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                if (userModels != null && userModels.size() > 0) {
                    reviewAdapter.setList(reviews, userModels);
                }
            }
        });

        homeViewModel.getSuggestedProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                suggestedProductModels = productModels;
            }
        });
        homeViewModel.getBoughtProducts().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProductModel>>() {
            @Override
            public void onChanged(ArrayList<ProductModel> productModels) {
                displayProgressDialog(false);
                if ((suggestedProductModels != null && suggestedProductModels.size() > 0)
                        || (productModels != null && productModels.size() > 0)) {
                    displayProducts(suggestedProductModels, productModels);
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


    private void displayProducts(ArrayList<ProductModel> suggestedProductModels, ArrayList<ProductModel> boughtProductModels){
        if(manager == null){
            manager = getChildFragmentManager();
            transaction = manager.beginTransaction();

            if(suggestedProductModels.size() > 0) {
                transaction.replace(R.id.container_fragment, SpecialProductsFragment.newInstance(suggestedProductModels, getString(R.string.suggested), this));
            }
            if(boughtProductModels.size() > 0) {
                transaction.replace(R.id.container_fragment2, SpecialProductsFragment.newInstance(boughtProductModels, getString(R.string.bought), this));
            }
            transaction.commit();
        }
    }


    private void setInitialData(){

        if(Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).isProductCarted(product)){
            Log.d(TAG, "MERO: setInitialData checked " + product.getTitle() + " / " +product);
            binding.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_cart_added);
            binding.saveInCartBt.setChecked(true);
        }else {
            Log.d(TAG, "MERO: setInitialData unchecked " + product.getTitle() + " / " +product);
            binding.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_add_cart);
            binding.saveInCartBt.setChecked(false);
        }

        if(Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_WISHED).isProductWished(product)){
            binding.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wished);
            binding.saveInWishListBt.setChecked(true);
        }else {
            binding.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wish);
            binding.saveInWishListBt.setChecked(false);
        }

    }
    private void setData(ProductModel product){
        productImages = new ArrayList<>();

        if(product.getImagesPaths() != null){
            productImages.addAll(product.getImagesPaths());
        }else {
            ImageProductModel imageProductModel = new ImageProductModel();
            imageProductModel.setImagePath(product.getImagePath());
            productImages.add(imageProductModel);
        }

        adapter.setList(productImages);
        dotsCount = productImages.size();
        setSlideDots(dotsCount);

        double price = product.getPrice() * ((100 - product.getOffer())/100);
        float offer = product.getOffer();

        binding.productTitleTv.setText(product.getTitle());
        binding.productOfferTv.setText(String.format("%.0f%s",offer,"% OFF"));
        binding.productRateNumTv.setText(String.format("%d %s",product.getReviewsCount(), "Reviews"));
        binding.productPriceTv.setText(decimalFormat.format(price) + getContext().getString(R.string.egp));
        binding.productPriceWithoutOfferTv.setText(decimalFormat.format(product.getPrice())
                + getContext().getString(R.string.egp));
        binding.productRateBar.setRating(product.getReviewsRateAverage());

        if(offer > 0){
            binding.productOfferTv.setVisibility(View.VISIBLE);
            binding.productPriceWithoutOfferTv.setVisibility(View.VISIBLE);
            binding.productPriceWithoutOfferTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            binding.productOfferTv.setVisibility(View.INVISIBLE);
            binding.productPriceWithoutOfferTv.setVisibility(View.INVISIBLE);
        }
    }

    private void setSlideDots(int dotsCount){
        dotsIv = new ImageView[dotsCount];
        binding.slideDotsLila.removeAllViews();
        for(int i = 0; i < dotsCount; i++){
            dotsIv[i] = new ImageView(getContext());
            dotsIv[i].setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.background_dots_unactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5,0,5,0);
            binding.slideDotsLila.addView(dotsIv[i], params);
        }
        dotsIv[0].setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.background_dots_active));
    }
    private void setProductCart(boolean carted){
        if(carted){
            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).setProductCarted(product);
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));
            displaySnackBar(true, getString(R.string.carted), -1);

        }else {
            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).removeProductCarted(product);
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(),productsCartedId.size(),R.drawable.ic_cart));
            displaySnackBar(true, getString(R.string.unCarted), -1);
        }

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
        utils.snackBar(root.findViewById(R.id.containerProduct), msg, duration);
        utils.displaySnackBar(show);
    }

    private void saveInCart(){
        if(binding.saveInCartBt.isChecked()){
            setProductCart(true);
            binding.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_cart_added);
        }else {
            setProductCart(false);
            binding.saveInCartBt.setBackgroundResource(R.mipmap.ic_launcher_add_cart);
        }
    }
    private void saveInWishList(){
        if(binding.saveInWishListBt.isChecked()){
            Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_WISHED).setProductWished(product);
            binding.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wished);
        }else {
            Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_WISHED).removeProductWished(product);
            binding.saveInWishListBt.setBackgroundResource(R.mipmap.ic_wish);
        }
    }

    private void displayProductDescription(){
        bundle = new Bundle();
        bundle.putString("productDetailsPath", product.getDescriptionPath());
        bundle.putString("typeDetails", getString(R.string.description));
        navController.navigate(R.id.action_productFragment_to_pdfFileFragment, bundle);
    }
    private void displayProductSpecifications(){
        bundle = new Bundle();
        bundle.putString("productDetailsPath", product.getSpecificationPath());
        bundle.putString("typeDetails", getString(R.string.specifications));
        navController.navigate(R.id.action_productFragment_to_pdfFileFragment, bundle);
    }

    private void displayProductReviews(){
        bundle = new Bundle();
        bundle.putParcelable("product", product);
        navController.navigate(R.id.action_productFragment_to_reviewsFragment, bundle);
    }

    private void addReview(){
        if(user.getValidated() == 0){
            displaySnackBar(true, getString(R.string.add_review_info), -2);
            navController.navigate(R.id.action_productFragment_to_profileFragment);
        }else {
            bundle = new Bundle();
            bundle.putString("productId", product.getId());
            navController.navigate(R.id.action_productFragment_to_addReviewFragment, bundle);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.saveInCart_bt:
                saveInCart();
                break;

            case R.id.saveInWishList_bt:
                saveInWishList();
                break;

            case R.id.product_description_bt:
                displayProductDescription();
                break;

            case R.id.product_specifications_bt:
                displayProductSpecifications();
                break;

            case R.id.product_reviews_tv:
                displayProductReviews();
                break;

            case R.id.product_add_review_lila:
                addReview();
                break;

            default:
                getActivity().onBackPressed();
                break;

        }
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

            productsCartedId = Preferences.getINSTANCE(getContext(), PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(getContext(), productsCartedId.size(),R.drawable.ic_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.myCart:
                navController.navigate(R.id.action_productFragment_to_navigation_cart);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for(int i = 0; i < dotsCount; i++){
            dotsIv[i].setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.background_dots_unactive));
        }
        dotsIv[position].setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.background_dots_active));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

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
}