package com.example.ecommerceapp.ui.main;

import android.util.Log;

import com.example.ecommerceapp.data.Config;
import com.example.ecommerceapp.data.EcoClient;
import com.example.ecommerceapp.data.JavaMailAPI;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.facebook.internal.FacebookDialogFragment.TAG;

public class UserViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveErrorMessage;

    private MutableLiveData<String> mutableLiveDataSignUp;
    private MutableLiveData<ArrayList<UserModel>> mutableLiveDataLogIn;
    private MutableLiveData<String> mutableLiveEmail;
    private MutableLiveData<String> mutableLivePassword;
    private MutableLiveData<Boolean> mutableLiveMessage;
    private CompositeDisposable compositeDisposable;

    public UserViewModel() {
        mutableLiveErrorMessage = new MutableLiveData<>();

        mutableLiveDataSignUp = new MutableLiveData<>();
        mutableLiveDataLogIn = new MutableLiveData<>();
        mutableLiveEmail = new MutableLiveData<>();
        mutableLivePassword = new MutableLiveData<>();
        mutableLiveMessage = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<String> getErrorMessage() {
        return mutableLiveErrorMessage;
    }

    public LiveData<String> getSignUpUser() {
        return mutableLiveDataSignUp;
    }
    public LiveData<ArrayList<UserModel>> getLogInUser() {
        return mutableLiveDataLogIn;
    }

    public LiveData<String> getEmail() {
        return mutableLiveEmail;
    }
    public LiveData<String> getEditPassword() {
        return mutableLivePassword;
    }
    public LiveData<Boolean> getMessageResult() {
        return mutableLiveMessage;
    }

    public void createAccount(UserModel userModel){

        Single<String> observable = EcoClient.getINSTANCE()
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
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataSignUp.setValue(d)));
    }
    public void signUp(UserModel userModel){

        Single<String> observable = EcoClient.getINSTANCE()
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
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataSignUp.setValue(d)));
    }

    public void logInAccount(UserModel userModel){

        Single<ArrayList<UserModel>> observable = EcoClient.getINSTANCE()
                .logInAccount(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<ArrayList<UserModel>> observer = new SingleObserver<ArrayList<UserModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<UserModel> userModels) {
                mutableLiveDataLogIn.setValue(userModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataLogIn.setValue(d.get("resultArray"))));
    }
    public void logIn(UserModel userModel){

        Single<ArrayList<UserModel>> observable = EcoClient.getINSTANCE()
                .logIn(userModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        SingleObserver<ArrayList<UserModel>> observer = new SingleObserver<ArrayList<UserModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<UserModel> userModels) {
                mutableLiveDataLogIn.setValue(userModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };

        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataLogIn.setValue(d.get("resultArray"))));
    }


    public void getEmail(String userName){

        Single<String> observable = EcoClient.getINSTANCE()
                .getEmail(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLiveEmail.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataSignUp.setValue(d)));
    }
    public void editPassword(String email, String password){

        Single<String> observable = EcoClient.getINSTANCE()
                .editPassword(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SingleObserver<String> observer = new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                mutableLivePassword.setValue(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
        };
        observable.subscribe(observer);
//        compositeDisposable.add(observable.subscribe(d-> mutableLiveDataSignUp.setValue(d)));
    }
    public void sendMessage(String email, String mSubject, String mMessage){

        JavaMailAPI mailAPI = new JavaMailAPI(email, mSubject, mMessage);

        Observable observable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if(mailAPI.execute()){
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }
            @Override
            public void onNext(Object o) {
            }
            @Override
            public void onError(@NonNull Throwable e) {
                mutableLiveErrorMessage.setValue(e.getMessage());
            }
            @Override
            public void onComplete() {
                mutableLiveMessage.setValue(true);
            }
        };

        observable.subscribe(observer);
    }

    // this is one of lifecycle of viewModel and call it when viewModel is killed
    @Override
    protected void onCleared() {
        super.onCleared();


        compositeDisposable.clear();
    }
}
