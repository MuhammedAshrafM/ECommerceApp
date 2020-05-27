package com.example.ecommerceapp.ui.home;

import com.example.ecommerceapp.data.ECommerceClient;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.MergeModel;

import java.util.ArrayList;
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
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<CategoryModel>> mutableLiveCategories;
    private MutableLiveData<ArrayList<MergeModel>> mutableLiveProducts;
    private MutableLiveData<ArrayList<MergeModel>> mutableLiveSubCategories;
    private CompositeDisposable compositeDisposable;

    public HomeViewModel() {
        mutableLiveCategories = new MutableLiveData<>();
        mutableLiveProducts = new MutableLiveData<>();
        mutableLiveSubCategories = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<ArrayList<CategoryModel>> getCategories() {
        return mutableLiveCategories;
    }
    public LiveData<ArrayList<MergeModel>> getProducts() {
        return mutableLiveProducts;
    }
    public LiveData<ArrayList<MergeModel>> getSubCategories() {
        return mutableLiveSubCategories;
    }

    public void getMainCategories() {
        Single<Map<String, ArrayList<CategoryModel>>> observable = ECommerceClient.getINSTANCE()
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<Map<String, ArrayList<CategoryModel>>> observer = new SingleObserver<Map<String, ArrayList<CategoryModel>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Map<String, ArrayList<CategoryModel>> stringArrayListMap) {
                mutableLiveCategories.setValue(stringArrayListMap.get("resultArray"));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveCategories.setValue(null);
//                if (e.getMessage().contains("timed out")) {
//                    mutableLiveCategories.setValue(null);
//                } else {
//                    Log.d(TAG, "OnERRor: " + e.getMessage());
//
//                }
            }
        };


        observable.cache().subscribe(observer);
//        compositeDisposable.add(observable.cache().subscribe(d-> mutableLiveCategories.setValue(d)));

    }

    public void getProductsAndSubCategories(String categoryId) {
        Single<Map<String, ArrayList<MergeModel>>> observable = ECommerceClient.getINSTANCE()
                .getProductsAndSubCategories(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<Map<String, ArrayList<MergeModel>>> observer = new SingleObserver<Map<String, ArrayList<MergeModel>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Map<String, ArrayList<MergeModel>> stringArrayListMap) {
                mutableLiveProducts.setValue(stringArrayListMap.get("products"));
                mutableLiveSubCategories.setValue(stringArrayListMap.get("subCategories"));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveProducts.setValue(null);
                mutableLiveSubCategories.setValue(null);
            }
        };


        observable.cache().subscribe(observer);

    }

    public void getProducts(String subCategoryId) {
        Single<Map<String, ArrayList<MergeModel>>> observable = ECommerceClient.getINSTANCE()
                .getProducts(subCategoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<Map<String, ArrayList<MergeModel>>> observer = new SingleObserver<Map<String, ArrayList<MergeModel>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Map<String, ArrayList<MergeModel>> stringArrayListMap) {
                mutableLiveProducts.setValue(stringArrayListMap.get("products"));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveProducts.setValue(null);
            }
        };


        observable.cache().subscribe(observer);

    }

}