package com.example.ecommerceapp.data;

import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.MergeModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ECommerceClient {

    private static final String BASE_URL = "https://mypharmaciesapp.000webhostapp.com/";
    private static final String SERVER_URL = "http://192.168.1.114/";
    private ECommerceInterface eCommerceInterface;
    private static ECommerceClient INSTANCE;


    public ECommerceClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        // to connect with server by php script
        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS);
        httpClient.interceptors().add(logging);

        Retrofit retrofit = new Retrofit.Builder()
                // to connect with server by php script
                .client(httpClient.build())
                // add basic url of server
                .baseUrl(BASE_URL)
                // add converter for convert to json by used GsonConverterFactory
                .addConverterFactory(GsonConverterFactory.create(gson))
                // add adapter to convert callback to observable
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        eCommerceInterface = retrofit.create(ECommerceInterface.class);

    }

    public static ECommerceClient getINSTANCE(){

        if(INSTANCE == null){
            INSTANCE = new ECommerceClient();
        }
        return INSTANCE;
    }


    public Single<String> createAccount(UserModel userModel){
        return eCommerceInterface.createAccount("createAccount", userModel.toMap());
    }
    public Single<String> signUp(UserModel userModel){
        return eCommerceInterface.createAccount("signUp", userModel.toMap());
    }
    public Single<Map<String,ArrayList<UserModel>>> logInAccount(UserModel userModel){
        return eCommerceInterface.logIn("logInAccount", userModel.toMap());
    }
    public Single<Map<String,ArrayList<UserModel>>> logIn(UserModel userModel){
        return eCommerceInterface.logIn("logIn", userModel.toMap());
    }

    public Single<Map<String,ArrayList<CategoryModel>>> getCategories(){
        return eCommerceInterface.getCategories("getMainCategories");
    }

    public Single<Map<String,ArrayList<MergeModel>>> getProductsAndSubCategories(String categoryId){
        return eCommerceInterface.getProductsAndSubCategories("getProductsAndSubCategories", categoryId);
    }
    public Single<Map<String,ArrayList<MergeModel>>> getProducts(String subCategoryId){
        return eCommerceInterface.getProducts("getProducts", subCategoryId);
    }


}
