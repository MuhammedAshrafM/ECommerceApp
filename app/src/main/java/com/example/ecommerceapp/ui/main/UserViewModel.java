package com.example.ecommerceapp.ui.main;

import com.example.ecommerceapp.data.ECommerceClient;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModel extends ViewModel {


    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<UserModel>> mutableLiveData2 = new MutableLiveData<>();
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void createAccount(UserModel userModel){

        Single<String> observable = ECommerceClient.getINSTANCE().createAccount(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(observable.subscribe(d-> mutableLiveData.setValue(d)));
    }
    public void signUp(UserModel userModel){

        Single<String> observable = ECommerceClient.getINSTANCE().signUp(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(observable.subscribe(d-> mutableLiveData.setValue(d)));
    }

    public void logInAccount(UserModel userModel){

        Single<ArrayList<UserModel>> observable = ECommerceClient.getINSTANCE().logInAccount(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(observable.subscribe(d-> mutableLiveData2.setValue(d)));
    }
    public void logIn(UserModel userModel){

        Single<ArrayList<UserModel>> observable = ECommerceClient.getINSTANCE().logIn(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        compositeDisposable.add(observable.subscribe(d-> mutableLiveData2.setValue(d)));
    }

    // this is one of lifecycle of viewModel and call it when viewModel is killed
    @Override
    protected void onCleared() {
        super.onCleared();

        compositeDisposable.clear();
    }
}
