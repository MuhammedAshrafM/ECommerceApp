package com.example.ecommerceapp.ui.main;

import com.example.ecommerceapp.data.ECommerceClient;
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
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveDataSignUp;
    private MutableLiveData<ArrayList<UserModel>> mutableLiveDataLogIn;
    private CompositeDisposable compositeDisposable;

    public UserViewModel() {
        mutableLiveDataSignUp = new MutableLiveData<>();
        mutableLiveDataLogIn = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<String> getSignUpUser() {
        return mutableLiveDataSignUp;
    }
    public LiveData<ArrayList<UserModel>> getLogInUser() {
        return mutableLiveDataLogIn;
    }

    public void createAccount(UserModel userModel){

        Single<String> observable = ECommerceClient.getINSTANCE()
                .createAccount(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveDataSignUp.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveDataSignUp.setValue(null);
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataSignUp.setValue(d)));
    }
    public void signUp(UserModel userModel){

        Single<String> observable = ECommerceClient.getINSTANCE()
                .signUp(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveDataSignUp.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveDataSignUp.setValue(null);
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataSignUp.setValue(d)));
    }

    public void logInAccount(UserModel userModel){

        Single<Map<String,ArrayList<UserModel>>> observable = ECommerceClient.getINSTANCE()
                .logInAccount(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<Map<String, ArrayList<UserModel>>> observer = new SingleObserver<Map<String, ArrayList<UserModel>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Map<String, ArrayList<UserModel>> stringArrayListMap) {
                mutableLiveDataLogIn.setValue(stringArrayListMap.get("resultArray"));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveDataLogIn.setValue(null);
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataLogIn.setValue(d.get("resultArray"))));
    }
    public void logIn(UserModel userModel){

        Single<Map<String,ArrayList<UserModel>>> observable = ECommerceClient.getINSTANCE()
                .logIn(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        SingleObserver<Map<String, ArrayList<UserModel>>> observer = new SingleObserver<Map<String, ArrayList<UserModel>>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Map<String, ArrayList<UserModel>> stringArrayListMap) {
                mutableLiveDataLogIn.setValue(stringArrayListMap.get("resultArray"));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveDataLogIn.setValue(null);
            }
        };

        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataLogIn.setValue(d.get("resultArray"))));
    }

    // this is one of lifecycle of viewModel and call it when viewModel is killed
    @Override
    protected void onCleared() {
        super.onCleared();


        compositeDisposable.clear();
    }
}
