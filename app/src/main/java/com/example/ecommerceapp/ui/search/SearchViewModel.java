package com.example.ecommerceapp.ui.search;

import android.util.Log;

import com.example.ecommerceapp.data.EcoClient;
import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;

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
import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.functions.Function4;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.facebook.GraphRequest.TAG;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveErrorMessage;

    private MutableLiveData<ArrayList<ProductModel>> mutableLiveProducts;
    private MutableLiveData<ArrayList<SubCategoryModel>> mutableLiveSubCategories;
    private MutableLiveData<ArrayList<BrandModel>> mutableLiveBrands;

    private CompositeDisposable compositeDisposable;

    private ArrayList<ProductModel> productModels;
    private ArrayList<SubCategoryModel> subCategoryModels;
    private ArrayList<BrandModel> brandModels;

    public SearchViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveProducts = new MutableLiveData<>();
        mutableLiveSubCategories = new MutableLiveData<>();
        mutableLiveBrands = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();

    }

    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public LiveData<ArrayList<ProductModel>> getProducts() {
        return mutableLiveProducts;
    }


    public LiveData<ArrayList<SubCategoryModel>> getSubCategories() {
        return mutableLiveSubCategories;
    }
    public LiveData<ArrayList<BrandModel>> getBrands() {
        return mutableLiveBrands;
    }


    public void getProductsSearched(String query) {

        productModels = new ArrayList<>();
        subCategoryModels = new ArrayList<>();
        brandModels = new ArrayList<>();

        Single<ArrayList<ProductModel>> observableP = EcoClient.getINSTANCE()
                .getProductsSearched(query);

        Single<ArrayList<SubCategoryModel>> observableSC = EcoClient.getINSTANCE()
                .getSubCategoriesSearched(query);

        Single<ArrayList<BrandModel>> observableB = EcoClient.getINSTANCE()
                .getBrandsSearched(query);


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
                        .subscribe(result -> setProductsSearched(),
                                error-> mutableLiveErrorMessage.setValue(error.getMessage())));

    }

    private void setProductsSearched(){
        mutableLiveProducts.setValue(productModels);
        mutableLiveSubCategories.setValue(subCategoryModels);
        mutableLiveBrands.setValue(brandModels);
    }

    // this is one of lifecycle of viewModel and call it when viewModel is killed
    @Override
    protected void onCleared() {
        super.onCleared();

        compositeDisposable.clear();
    }
}