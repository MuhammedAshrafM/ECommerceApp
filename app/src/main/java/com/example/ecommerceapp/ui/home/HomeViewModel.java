package com.example.ecommerceapp.ui.home;

import com.example.ecommerceapp.data.EcoClient;
import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.ImageProductModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.SellerModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.functions.Function4;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveErrorMessage;

    private MutableLiveData<ArrayList<CategoryModel>> mutableLiveCategories;
    private MutableLiveData<ArrayList<ProductModel>> mutableLiveProducts, mutableLiveSuggestedProducts,
            mutableLiveBoughtProducts;
    private MutableLiveData<ProductModel> mutableLiveProduct;
    private MutableLiveData<ArrayList<SubCategoryModel>> mutableLiveSubCategories;
    private MutableLiveData<ArrayList<ReviewModel>> mutableLiveReviews;
    private MutableLiveData<ArrayList<UserModel>> mutableLiveUsers;
    private MutableLiveData<SellerModel> mutableLiveSeller;
    private MutableLiveData<String> mutableLiveAddReview;
    private MutableLiveData<ArrayList<Map<String,Float>>> mutableLiveInfoSubCategories;
    private MutableLiveData<ArrayList<BrandModel>> mutableLiveBrands;

    private CompositeDisposable compositeDisposable;


    private ArrayList<CategoryModel> categoryModels;
    private ArrayList<SubCategoryModel> subCategoryModels;
    private ArrayList<ProductModel> productModels, suggestedProductModels, boughtProductModels;
    private ArrayList<ReviewModel> reviewModels;
    private ArrayList<UserModel> userModels;
    private SellerModel sellerModel;
    private ProductModel productModel;
    private ArrayList<ImageProductModel> imagesProduct;
    private ArrayList<Map<String,Float>> infoSubCategories;
    private ArrayList<BrandModel> brandModels;

    public HomeViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveAddReview = new MutableLiveData<>();
        mutableLiveCategories = new MutableLiveData<>();
        mutableLiveSubCategories = new MutableLiveData<>();
        mutableLiveProducts = new MutableLiveData<>();
        mutableLiveProduct = new MutableLiveData<>();
        mutableLiveReviews = new MutableLiveData<>();
        mutableLiveUsers = new MutableLiveData<>();
        mutableLiveSeller = new MutableLiveData<>();
        mutableLiveSuggestedProducts = new MutableLiveData<>();
        mutableLiveBoughtProducts = new MutableLiveData<>();
        mutableLiveInfoSubCategories = new MutableLiveData<>();
        mutableLiveBrands = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();

    }

    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public LiveData<ArrayList<CategoryModel>> getCategories() {
        return mutableLiveCategories;
    }
    public LiveData<ArrayList<ProductModel>> getProducts() {
        return mutableLiveProducts;
    }
    public LiveData<ArrayList<ProductModel>> getSuggestedProducts() {
        return mutableLiveSuggestedProducts;
    }
    public LiveData<ArrayList<ProductModel>> getBoughtProducts() {
        return mutableLiveBoughtProducts;
    }
    public LiveData<ProductModel> getProduct() {
        return mutableLiveProduct;
    }
    public LiveData<SellerModel> getSeller() {
        return mutableLiveSeller;
    }
    public LiveData<ArrayList<SubCategoryModel>> getSubCategories() {
        return mutableLiveSubCategories;
    }
    public LiveData<ArrayList<Map<String, Float>>> getInfoSubCategories() {
        return mutableLiveInfoSubCategories;
    }
    public LiveData<ArrayList<ReviewModel>> getReviews() {
        return mutableLiveReviews;
    }
    public LiveData<ArrayList<UserModel>> getUsers() {
        return mutableLiveUsers;
    }

    public LiveData<String> addReview() {
        return mutableLiveAddReview;
    }

    public LiveData<ArrayList<BrandModel>> getBrands() {
        return mutableLiveBrands;
    }


    public void getMainCategories(String userId) {

        categoryModels = new ArrayList<>();
        productModels = new ArrayList<>();

        Single<ArrayList<CategoryModel>> observableC = EcoClient.getINSTANCE()
                .getCategories();

        Single<ArrayList<ProductModel>> observableP = EcoClient.getINSTANCE()
                .getLimitProductsRecentlyViewed(userId);

        compositeDisposable.add(Single.zip(observableC, observableP, new BiFunction<ArrayList<CategoryModel>
                ,ArrayList<ProductModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<CategoryModel> t1, ArrayList<ProductModel> t2) throws Throwable {
                if(t1 == null && t2 == null){
                    return false;
                }
                categoryModels = t1;
                productModels = t2;
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setCategoriesAndProducts(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));

    }
    private void setCategoriesAndProducts(){
        mutableLiveProducts.setValue(productModels);
        mutableLiveCategories.setValue(categoryModels);
    }

    public void getSubCategoriesOffer() {

        subCategoryModels = new ArrayList<>();
        infoSubCategories = new ArrayList<>();

        Single<ArrayList<SubCategoryModel>> observableSC = EcoClient.getINSTANCE()
                .getSubCategoriesOffer();

        Single<ArrayList<Map<String,Float>>> observableSCI = EcoClient.getINSTANCE()
                .getSubCategoriesOfferInfo();

        compositeDisposable.add(Single.zip(observableSC, observableSCI, new BiFunction<ArrayList<SubCategoryModel>
                ,ArrayList<Map<String,Float>>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<SubCategoryModel> t1, ArrayList<Map<String,Float>> t2) throws Throwable {
                subCategoryModels = t1;
                infoSubCategories = t2;
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setSubCategoriesOffer(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));
    }

    private void setSubCategoriesOffer(){
        mutableLiveSubCategories.setValue(subCategoryModels);
        mutableLiveInfoSubCategories.setValue(infoSubCategories);
    }


    public void getProductsAndSubCategories(String categoryId) {

        subCategoryModels = new ArrayList<>();
        productModels = new ArrayList<>();

        Single<ArrayList<SubCategoryModel>> observableSC = EcoClient.getINSTANCE()
                .getSubCategories(categoryId);

        Single<ArrayList<ProductModel>> observableP = EcoClient.getINSTANCE()
                .getProductsCategory(categoryId);

        compositeDisposable.add(Single.zip(observableSC, observableP, new BiFunction<ArrayList<SubCategoryModel>
                ,ArrayList<ProductModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<SubCategoryModel> t1, ArrayList<ProductModel> t2) throws Throwable {
                subCategoryModels = t1;
                productModels = t2;
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setProductsAndSubCategories(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));


    }
    private void setProductsAndSubCategories(){

        mutableLiveSubCategories.setValue(subCategoryModels);
        mutableLiveProducts.setValue(productModels);
    }

    public void getProducts(String subCategoryId) {

        Single<ArrayList<ProductModel>> observable = EcoClient.getINSTANCE()
                .getProducts(subCategoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<ArrayList<ProductModel>> observer = new SingleObserver<ArrayList<ProductModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }
            @Override
            public void onSuccess(@NonNull ArrayList<ProductModel> stringArrayLis) {

                mutableLiveProducts.setValue(stringArrayLis);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.cache().subscribe(observer);
    }
    public void getProductsOffer(String subCategoryId) {

        Single<ArrayList<ProductModel>> observable = EcoClient.getINSTANCE()
                .getProductsOffer(subCategoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<ArrayList<ProductModel>> observer = new SingleObserver<ArrayList<ProductModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }
            @Override
            public void onSuccess(@NonNull ArrayList<ProductModel> stringArrayLis) {

                mutableLiveProducts.setValue(stringArrayLis);
            }
            @Override
            public void onError(@NonNull Throwable e) {

                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.cache().subscribe(observer);
    }
    public void getProduct(String productId, String userId, String sellerId) {

        productModel = new ProductModel();
        imagesProduct = new ArrayList<>();
        sellerModel = new SellerModel();

        Single<ArrayList<ProductModel>> observableP = EcoClient.getINSTANCE()
                .getProduct(productId, userId);

        Single<ArrayList<ImageProductModel>> observableIP = EcoClient.getINSTANCE()
                .getImagesProduct(productId);

        Single<ArrayList<SellerModel>> observableS = EcoClient.getINSTANCE()
                .getSeller(sellerId);


        compositeDisposable.add(Single.zip(observableP, observableIP, observableS, new Function3<ArrayList<ProductModel>,
                ArrayList<ImageProductModel>, ArrayList<SellerModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<ProductModel> t1, ArrayList<ImageProductModel> t2, ArrayList<SellerModel> t3) throws Throwable {

                productModel = t1.get(0);
                imagesProduct = t2;
                sellerModel = t3.get(0);
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setProduct(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));

    }

    private void setProduct(){
        productModel.setImagesPaths(imagesProduct);
        mutableLiveProduct.setValue(productModel);
        mutableLiveSeller.setValue(sellerModel);
    }


    public void getLimitReviews(String productId) {

        reviewModels = new ArrayList<>();
        userModels = new ArrayList<>();

        Single<ArrayList<ReviewModel>> observableR = EcoClient.getINSTANCE()
                .getLimitReviews(productId);

        Single<ArrayList<UserModel>> observableU = EcoClient.getINSTANCE()
                .getLimitUsersReview(productId);

        compositeDisposable.add(Single.zip(observableR, observableU, new BiFunction<ArrayList<ReviewModel>
                ,ArrayList<UserModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<ReviewModel> t1, ArrayList<UserModel> t2) throws Throwable {

                reviewModels = t1;
                userModels = t2;
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setLimitReviewsAndUsers(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));
    }
    private void setLimitReviewsAndUsers(){

        mutableLiveReviews.setValue(reviewModels);
        mutableLiveUsers.setValue(userModels);
    }

    public void getReviews(String productId) {

        reviewModels = new ArrayList<>();
        userModels = new ArrayList<>();

        Single<ArrayList<ReviewModel>> observableR = EcoClient.getINSTANCE()
                .getReviews(productId);

        Single<ArrayList<UserModel>> observableU = EcoClient.getINSTANCE()
                .getUsersReview(productId);

        compositeDisposable.add(Single.zip(observableR, observableU, new BiFunction<ArrayList<ReviewModel>,
                ArrayList<UserModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<ReviewModel> t1, ArrayList<UserModel> t2) throws Throwable {
                reviewModels = t1;
                userModels = t2;
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setReviewsAndUsers(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));
    }

    private void setReviewsAndUsers(){
        mutableLiveReviews.setValue(reviewModels);
        mutableLiveUsers.setValue(userModels);
    }

    public void addReview(ReviewModel reviewModel, String productId){

        Single<String> observable = EcoClient.getINSTANCE()
                .addReview(reviewModel, productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveAddReview.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
    }


    public void getSpecialProducts(String subCategoryId, String productId, String userId) {

        suggestedProductModels = new ArrayList<>();
        boughtProductModels = new ArrayList<>();


        Single<ArrayList<ProductModel>> observablePS = EcoClient.getINSTANCE()
                .getLimitSuggestedProducts(subCategoryId, productId);


        Single<ArrayList<ProductModel>> observablePB = EcoClient.getINSTANCE()
                .getLimitBoughtProducts(subCategoryId, productId, userId);


        compositeDisposable.add(Single.zip(observablePS, observablePB, new BiFunction<ArrayList<ProductModel>
                ,ArrayList<ProductModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<ProductModel> t1, ArrayList<ProductModel> t2) throws Throwable {

                suggestedProductModels = t1;
                boughtProductModels = t2;
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setSpecialProducts(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));
    }


    private void setSpecialProducts(){

        mutableLiveSuggestedProducts.setValue(suggestedProductModels);
        mutableLiveBoughtProducts.setValue(boughtProductModels);
    }

    public void getSellerProducts(String sellerId){
        productModels = new ArrayList<>();
        subCategoryModels = new ArrayList<>();
        brandModels = new ArrayList<>();

        Single<ArrayList<ProductModel>> observableP = EcoClient.getINSTANCE()
                .getSellerProducts(sellerId);

        Single<ArrayList<SubCategoryModel>> observableSC = EcoClient.getINSTANCE()
                .getSellerSubCategories(sellerId);

        Single<ArrayList<BrandModel>> observableB = EcoClient.getINSTANCE()
                .getSellerBrands(sellerId);


        compositeDisposable.add(Single.zip(observableP, observableSC, observableB,
                new Function3<ArrayList<ProductModel>, ArrayList<SubCategoryModel>, ArrayList<BrandModel>, Boolean>() {
                    @Override
                    public Boolean apply(ArrayList<ProductModel> t1, ArrayList<SubCategoryModel> t2, ArrayList<BrandModel> t3) throws Throwable {
                        productModels = t1;
                        subCategoryModels = t2;
                        brandModels = t3;
                        return true;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setSellerProducts(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));

    }

    private void setSellerProducts(){
        mutableLiveProducts.setValue(productModels);
        mutableLiveSubCategories.setValue(subCategoryModels);
        mutableLiveBrands.setValue(brandModels);
    }

    public void getSellerReviews(String sellerId){
        Single<ArrayList<ReviewModel>> observable = EcoClient.getINSTANCE()
                .getSellerReviews(sellerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<ArrayList<ReviewModel>> observer = new SingleObserver<ArrayList<ReviewModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }
            @Override
            public void onSuccess(@NonNull ArrayList<ReviewModel> reviewModels) {

                mutableLiveReviews.setValue(reviewModels);
            }
            @Override
            public void onError(@NonNull Throwable e) {

                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.cache().subscribe(observer);
    }



    // this is one of lifecycle of viewModel and call it when viewModel is killed
    @Override
    protected void onCleared() {
        super.onCleared();


        compositeDisposable.clear();
    }
}