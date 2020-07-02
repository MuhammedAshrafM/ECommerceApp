package com.example.ecommerceapp.ui.more;

import com.example.ecommerceapp.data.EcoClient;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoreViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveErrorMessage;

    private MutableLiveData<ArrayList<ProductModel>> mutableLiveProductsWished;
    private MutableLiveData<String> mutableLiveDataValidateAccount;
    private MutableLiveData<ArrayList<UserModel>> mutableLiveDataEditAccount;

    public MoreViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveProductsWished = new MutableLiveData<>();
        mutableLiveDataValidateAccount = new MutableLiveData<>();
        mutableLiveDataEditAccount = new MutableLiveData<>();
    }

    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public LiveData<ArrayList<ProductModel>> getProductsWished() {
        return mutableLiveProductsWished;
    }
    public LiveData<String> getValidateAccount() {
        return mutableLiveDataValidateAccount;
    }
    public LiveData<ArrayList<UserModel>> getEditAccount() {
        return mutableLiveDataEditAccount;
    }


    public void getProductsWished(ArrayList<String> productIds){

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
                mutableLiveProductsWished.setValue(productModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }

    public void validateAccount(UserModel userModel){

        Single<String> observable = EcoClient.getINSTANCE()
                .validateAccount(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveDataValidateAccount.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataLogIn.setValue(d.get("resultArray"))));
    }


    public void editAccount(UserModel userModel){

        Single<Map<String,ArrayList<UserModel>>> observable = EcoClient.getINSTANCE()
                .editAccount(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<Map<String, ArrayList<UserModel>>> observer = new SingleObserver<Map<String, ArrayList<UserModel>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Map<String, ArrayList<UserModel>> stringArrayListMap) {
                mutableLiveDataEditAccount.setValue(stringArrayListMap.get("resultArray"));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataLogIn.setValue(d.get("resultArray"))));
    }
}