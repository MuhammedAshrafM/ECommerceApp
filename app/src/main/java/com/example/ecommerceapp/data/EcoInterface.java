package com.example.ecommerceapp.data;

import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.ImageProductModel;
import com.example.ecommerceapp.pojo.OrderModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
import com.example.ecommerceapp.pojo.SellerModel;
import com.example.ecommerceapp.pojo.SubCategoryModel;
import com.example.ecommerceapp.pojo.UserModel;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface EcoInterface {


    //This method is used for "POST"
    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> createAccount(@Field("method") String funName,
                                        @FieldMap Map<String, Object> mapUser);
    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<UserModel>> logIn(@Field("method") String funName,
                                                          @FieldMap Map<String, Object> mapUser);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> addToken(@Field("method") String funName,
                            @Field("token") String token,
                            @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> validateAccount(@Field("method") String funName,
                                          @FieldMap Map<String, Object> mapUser);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<UserModel>> editAccount(@Field("method") String funName,
                                                                @FieldMap Map<String, Object> mapUser);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<CategoryModel>> getCategories(@Field("method") String funName);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getLimitProductsRecentlyViewed(@Field("method") String funName,
                                                                          @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<SubCategoryModel>> getSubCategories(@Field("method") String funName,
                                                                @Field("categoryId") String categoryId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<SubCategoryModel>> getSubCategoriesOffer(@Field("method") String funName);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<Map<String,Float>>> getSubCategoriesOfferInfo(@Field("method") String funName);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<SubCategoryModel>> getSubCategoriesSearched(@Field("method") String funName,
                                                                        @Field("query") String query);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getProductsCategory(@Field("method") String funName,
                                                               @Field("categoryId") String categoryId);
    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getProducts(@Field("method") String funName,
                                                       @Field("subCategoryId") String subCategoryId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getLimitSuggestedProducts(@Field("method") String funName,
                                                                     @Field("subCategoryId") String subCategoryId,
                                                                     @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getLimitBoughtProducts(@Field("method") String funName,
                                                                  @Field("subCategoryId") String subCategoryId,
                                                                  @Field("productId") String productId,
                                                                  @Field("userId") String userId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getSelectedProducts(@Field("method") String funName,
                                                               @Field("productIds[]") ArrayList<String> productIds);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getProduct(@Field("method") String funName,
                                                      @Field("productId") String productId,
                                                      @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ImageProductModel>> getImagesProduct(@Field("method") String funName,
                                                                 @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getProductsSearched(@Field("method") String funName,
                                                               @Field("query") String query);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ReviewModel>> getLimitReviews(@Field("method") String funName,
                                                          @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<UserModel>> getLimitUsersReview(@Field("method") String funName,
                                                            @Field("productId") String productId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ReviewModel>> getReviews(@Field("method") String funName,
                                                     @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<UserModel>> getUsersReview(@Field("method") String funName,
                                                       @Field("productId") String productId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> addReview(@Field("method") String funName, @FieldMap Map<String, Object> mapReview);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<BrandModel>> getBrandsSearched(@Field("method") String funName,
                                                           @Field("query") String query);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<SellerModel>> getSelectedSellers(@Field("method") String funName,
                                                             @Field("sellerIds[]") ArrayList<String> productIds);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> setAddress(@Field("method") String funName, @FieldMap Map<String, Object> mapAddress);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> removeAddress(@Field("method") String funName, @Field("id") String addressId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<AddressModel>> getAddresses(@Field("method") String funName, @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> validateMobileNumber(@Field("method") String funName,
                                               @Field("countryCodeName") String countryCodeName,
                                               @Field("mobileNumber") String mobileNumber,
                                               @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<String>> sendOrder(@Field("method") String funName,
                                               @Field("orderId") String orderId,
                                               @Field("productsId[]") ArrayList<String> productsId,
                                               @Field("userId") String userId,
                                               @Field("userToken") String userToken,
                                               @Field("addressId") String addressId,
                                               @Field("productsCount[]") ArrayList<Integer> productsCount,
                                               @Field("paymentOption") String paymentOption,
                                               @Field("subTotal") double subTotal,
                                               @Field("shippingFee") double shippingFee);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<SellerModel>> getSeller(@Field("method") String funName,
                                                     @Field("sellerId") String sellerId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getSellerProducts(@Field("method") String funName,
                                                             @Field("sellerId") String sellerId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ReviewModel>> getSellerReviews(@Field("method") String funName,
                                                           @Field("sellerId") String sellerId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<SubCategoryModel>> getSellerSubCategories(@Field("method") String funName,
                                                                      @Field("sellerId") String sellerId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<BrandModel>> getSellerBrands(@Field("method") String funName,
                                                         @Field("sellerId") String sellerId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> getEmail(@Field("method") String funName, @Field("userName") String userName);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<String> editPassword(@Field("method") String funName,
                                       @Field("email") String email,
                                       @Field("password") String password);


    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<OrderModel>> getOrders(@Field("method") String funName,
                                                   @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<OrderModel>> getOrder(@Field("method") String funName,
                                           @Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<ProductModel>> getProductsOrder(@Field("method") String funName,
                                                            @Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    Single<ArrayList<AddressModel>> getAddressOrder(@Field("method") String funName,
                                                            @Field("orderId") String orderId);

}
