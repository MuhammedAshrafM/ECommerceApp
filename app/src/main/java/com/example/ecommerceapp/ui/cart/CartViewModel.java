package com.example.ecommerceapp.ui.cart;

import com.example.ecommerceapp.data.EcoClient;
import com.example.ecommerceapp.pojo.ProductModel;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveErrorMessage;

    public MutableLiveData<ArrayList<ProductModel>> mutableLiveProductsCarted;

    public CartViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveProductsCarted = new MutableLiveData<>();
    }


    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public LiveData<ArrayList<ProductModel>> getProductsCarted() {
        return mutableLiveProductsCarted;
    }

    public void getProductsCarted(String[] productIds){

        Single<ArrayList<ProductModel>> observable = EcoClient.getINSTANCE()
                .getSelectedProducts(productIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<ArrayList<ProductModel>> observer = new SingleObserver<ArrayList<ProductModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<ProductModel> productModels) {
                mutableLiveProductsCarted.setValue(productModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }

}