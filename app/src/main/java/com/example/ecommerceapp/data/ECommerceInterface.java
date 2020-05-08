package com.example.ecommerceapp.data;

import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ECommerceInterface {

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<String> createAccount(@Field("method") String funName,
                                              @FieldMap Map<String, Object> mapUser);

    //This method is used for "POST"
    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<UserModel>> logIn(@Field("method") String funName,
                                              @FieldMap Map<String, Object> mapUser);

}
