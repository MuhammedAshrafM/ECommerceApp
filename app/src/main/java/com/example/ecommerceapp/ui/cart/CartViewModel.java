package com.example.ecommerceapp.ui.cart;

import com.example.ecommerceapp.data.EcoClient;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.SellerModel;

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

    private MutableLiveData<ArrayList<ProductModel>> mutableLiveProductsCarted;
    private MutableLiveData<ArrayList<SellerModel>> mutableLiveSellers;
    private MutableLiveData<ArrayList<String>> mutableLiveProductsCanceled;
    private MutableLiveData<ArrayList<AddressModel>> mutableLiveAddresses;

    private MutableLiveData<String> mutableLiveAddressAdded;
    private MutableLiveData<String> mutableLiveAddressEdited;
    private MutableLiveData<String> mutableLiveAddressRemoved;
    private MutableLiveData<String> mutableLiveAddressValidated;

    public CartViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveProductsCarted = new MutableLiveData<>();
        mutableLiveSellers = new MutableLiveData<>();
        mutableLiveProductsCanceled = new MutableLiveData<>();
        mutableLiveAddresses = new MutableLiveData<>();
        mutableLiveAddressAdded = new MutableLiveData<>();
        mutableLiveAddressEdited = new MutableLiveData<>();
        mutableLiveAddressRemoved = new MutableLiveData<>();
        mutableLiveAddressValidated = new MutableLiveData<>();
    }


    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public LiveData<ArrayList<ProductModel>> getProductsCarted() {
        return mutableLiveProductsCarted;
    }
    public LiveData<ArrayList<String>> sendOrder() {
        return mutableLiveProductsCanceled;
    }

    public LiveData<String> addAddress() {
        return mutableLiveAddressAdded;
    }
    public LiveData<String> editAddress() {
        return mutableLiveAddressEdited;
    }
    public LiveData<String> removeAddress() {
        return mutableLiveAddressRemoved;
    }
    public LiveData<String> validateMobileNumber() {
        return mutableLiveAddressValidated;
    }
    public LiveData<ArrayList<AddressModel>> getAddresses() {
        return mutableLiveAddresses;
    }
    public LiveData<ArrayList<SellerModel>> getSelectedSellers() {
        return mutableLiveSellers;
    }

    public void getProductsCarted(ArrayList<String> productIds){

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
    public void getSelectedSellers(ArrayList<String> sellerIds){

        Single<ArrayList<SellerModel>> observable = EcoClient.getINSTANCE()
                .getSelectedSellers(sellerIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<ArrayList<SellerModel>> observer = new SingleObserver<ArrayList<SellerModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<SellerModel> sellerModels) {
                mutableLiveSellers.setValue(sellerModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }

    public void addAddress(AddressModel addressModel){

        Single<String> observable = EcoClient.getINSTANCE()
                .addAddress(addressModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveAddressAdded.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }
    public void editAddress(AddressModel addressModel){

        Single<String> observable = EcoClient.getINSTANCE()
                .editAddress(addressModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveAddressEdited.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }
    public void removeAddress(String addressId){

        Single<String> observable = EcoClient.getINSTANCE()
                .removeAddress(addressId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveAddressRemoved.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }
    public void validateMobileNumber(String countryCodeName, String mobileNumber, String userId){

        Single<String> observable = EcoClient.getINSTANCE()
                .validateMobileNumber(countryCodeName, mobileNumber, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveAddressValidated.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }
    public void getAddresses(String userId){


        Single<ArrayList<AddressModel>> observable = EcoClient.getINSTANCE()
                .getAddresses(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<ArrayList<AddressModel>> observer = new SingleObserver<ArrayList<AddressModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<AddressModel> addressModels) {
                mutableLiveAddresses.setValue(addressModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }

    public void sendOrder(String orderId, ArrayList<String> productsId, String userId, String userToken, String addressId,
                          ArrayList<Integer> productsCount, String paymentOption, double subTotal, double shippingFee){

        Single<ArrayList<String>> observable = EcoClient.getINSTANCE()
                .sendOrder(orderId, productsId, userId, userToken, addressId, productsCount, paymentOption, subTotal, shippingFee)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<ArrayList<String>> observer = new SingleObserver<ArrayList<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<String> strings) {
                mutableLiveProductsCanceled.setValue(strings);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }
}