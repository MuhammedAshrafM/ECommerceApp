package com.example.ecommerceapp.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.EcoClient;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.OrderModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function3;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrdersViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveErrorMessage;

    private MutableLiveData<ArrayList<OrderModel>> mutableLiveOrders;
    private MutableLiveData<OrderModel> mutableLiveOrder;
    private MutableLiveData<ArrayList<ProductModel>> mutableLiveProducts;
    private MutableLiveData<AddressModel> mutableLiveAddress;

    private CompositeDisposable compositeDisposable;

    private ArrayList<ProductModel> productModels;
    private AddressModel addressModel;
    private OrderModel orderModel;

    public OrdersViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveOrders = new MutableLiveData<>();
        mutableLiveProducts = new MutableLiveData<>();
        mutableLiveAddress = new MutableLiveData<>();

        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<ArrayList<OrderModel>> getOrders() {
        return mutableLiveOrders;
    }
    public LiveData<OrderModel> getOrder() {
        return mutableLiveOrder;
    }
    public LiveData<ArrayList<ProductModel>> getProducts() {
        return mutableLiveProducts;
    }
    public LiveData<AddressModel> getAddress() {
        return mutableLiveAddress;
    }

    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public void getOrders(String userId){

        Single<ArrayList<OrderModel>> observable = EcoClient.getINSTANCE()
                .getOrders(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<ArrayList<OrderModel>> observer = new SingleObserver<ArrayList<OrderModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<OrderModel> orderModels) {
                mutableLiveOrders.setValue(orderModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.cache().subscribe(observer);
    }


    public void getOrderDetails(String orderId) {

        productModels = new ArrayList<>();
        addressModel = new AddressModel();

        Single<ArrayList<ProductModel>> observableP = EcoClient.getINSTANCE()
                .getProductsOrder(orderId);

        Single<ArrayList<AddressModel>> observableA = EcoClient.getINSTANCE()
                .getAddressOrder(orderId);

        compositeDisposable.add(Single.zip(observableP, observableA, new BiFunction<ArrayList<ProductModel>
                ,ArrayList<AddressModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<ProductModel> t1, ArrayList<AddressModel> t2) throws Throwable {
                productModels = t1;
                addressModel = t2.get(0);
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setOrderDetails(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));

    }
    private void setOrderDetails(){
        mutableLiveProducts.setValue(productModels);
        mutableLiveAddress.setValue(addressModel);
    }

    public void getOrder(String orderId) {
        orderModel = new OrderModel();
        productModels = new ArrayList<>();
        addressModel = new AddressModel();

        Single<ArrayList<OrderModel>> observableO = EcoClient.getINSTANCE()
                .getOrder(orderId);

        Single<ArrayList<ProductModel>> observableP = EcoClient.getINSTANCE()
                .getProductsOrder(orderId);

        Single<ArrayList<AddressModel>> observableA = EcoClient.getINSTANCE()
                .getAddressOrder(orderId);

        compositeDisposable.add(Single.zip(observableO, observableP, observableA, new Function3<ArrayList<OrderModel>,
                ArrayList<ProductModel>, ArrayList<AddressModel>, Boolean>() {
            @Override
            public Boolean apply(ArrayList<OrderModel> t1, ArrayList<ProductModel> t2, ArrayList<AddressModel> t3) throws Throwable {
                orderModel = t1.get(0);
                productModels = t2;
                addressModel = t3.get(0);
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(result -> setOrder(),
                        error-> mutableLiveErrorMessage.setValue(error.getMessage())));
    }
    private void setOrder(){
        mutableLiveOrder.setValue(orderModel);
        mutableLiveProducts.setValue(productModels);
        mutableLiveAddress.setValue(addressModel);
    }



    // this is one of lifecycle of viewModel and call it when viewModel is killed
    @Override
    protected void onCleared() {
        super.onCleared();


        compositeDisposable.clear();
    }
}