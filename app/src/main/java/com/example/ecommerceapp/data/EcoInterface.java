package com.example.ecommerceapp.data;

import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.pojo.CategoryModel;
import com.example.ecommerceapp.pojo.ImageProductModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.ReviewModel;
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
    public Single<String> createAccount(@Field("method") String funName,
                                        @FieldMap Map<String, Object> mapUser);
    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<Map<String,ArrayList<UserModel>>> logIn(@Field("method") String funName,
                                                          @FieldMap Map<String, Object> mapUser);
    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<String> validateAccount(@Field("method") String funName,
                                          @FieldMap Map<String, Object> mapUser);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<Map<String,ArrayList<UserModel>>> editAccount(@Field("method") String funName,
                                                                @FieldMap Map<String, Object> mapUser);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<CategoryModel>> getCategories(@Field("method") String funName);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getLimitProductsRecentlyViewed(@Field("method") String funName,
                                                                          @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<SubCategoryModel>> getSubCategories(@Field("method") String funName,
                                                                @Field("categoryId") String categoryId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<SubCategoryModel>> getSubCategoriesOffer(@Field("method") String funName);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<Map<String,Float>>> getSubCategoriesOfferInfo(@Field("method") String funName);


    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<SubCategoryModel>> getSubCategoriesSearched(@Field("method") String funName,
                                                                        @Field("query") String query);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getProductsCategory(@Field("method") String funName,
                                                               @Field("categoryId") String categoryId);
    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getProducts(@Field("method") String funName,
                                                       @Field("subCategoryId") String subCategoryId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getLimitSuggestedProducts(@Field("method") String funName,
                                                                     @Field("subCategoryId") String subCategoryId,
                                                                     @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getLimitBoughtProducts(@Field("method") String funName,
                                                                  @Field("subCategoryId") String subCategoryId,
                                                                  @Field("productId") String productId,
                                                                  @Field("userId") String userId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getSelectedProducts(@Field("method") String funName,
                                                               @Field("productIds[]") ArrayList<String> productIds);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getProduct(@Field("method") String funName,
                                                      @Field("productId") String productId,
                                                      @Field("userId") String userId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ImageProductModel>> getImagesProduct(@Field("method") String funName,
                                                                 @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getProductsSearched(@Field("method") String funName,
                                                               @Field("query") String query);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<Map<String,Double>> getInfoProductsSearched(@Field("method") String funName,
                                                              @Field("query") String query);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ReviewModel>> getLimitReviews(@Field("method") String funName,
                                                          @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<UserModel>> getLimitUsersReview(@Field("method") String funName,
                                                            @Field("productId") String productId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ReviewModel>> getReviews(@Field("method") String funName,
                                                     @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<UserModel>> getUsersReview(@Field("method") String funName,
                                                       @Field("productId") String productId);


    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<Map<String, Object>> getInfoReviews(@Field("method") String funName,
                                                      @Field("productId") String productId);

    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<String> addReview(@Field("method") String funName, @FieldMap Map<String, Object> mapReview);


    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<BrandModel>> getBrandsSearched(@Field("method") String funName,
                                                           @Field("query") String query);


    @FormUrlEncoded
    @POST("ecommerce.php")
    public Single<ArrayList<ProductModel>> getProductsFiltered(@Field("method") String funName,
                                                               @Field("query") String query,
                                                               @Field("subCategoryIds[]") ArrayList<String> subCategoryIds,
                                                               @FieldMap Map<String, Double> priceRange,
                                                               @Field("brandIds[]") ArrayList<String> brandsIds);

}
