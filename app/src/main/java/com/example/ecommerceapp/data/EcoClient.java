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

public class EcoClient {

    private static final String BASE_URL = "https://mypharmaciesapp.000webhostapp.com/";
    private static final String SERVER_URL = "http://192.168.1.114/";
    private EcoInterface ecoInterface;
    private static EcoClient INSTANCE;


    private EcoClient() {

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

        ecoInterface = retrofit.create(EcoInterface.class);

    }

    public synchronized static EcoClient getINSTANCE(){

        if(INSTANCE == null){
            INSTANCE = new EcoClient();
        }
        return INSTANCE;
    }


    public Single<String> createAccount(UserModel userModel){
        return ecoInterface.createAccount("createAccount", userModel.toMap());
    }
    public Single<String> signUp(UserModel userModel){
        return ecoInterface.createAccount("signUp", userModel.toMap());
    }
    public Single<ArrayList<UserModel>> logInAccount(UserModel userModel){
        return ecoInterface.logIn("logInAccount", userModel.toMap());
    }
    public Single<ArrayList<UserModel>> logIn(UserModel userModel){
        return ecoInterface.logIn("logIn", userModel.toMap());
    }
    public Single<String> validateAccount(UserModel userModel){
        return ecoInterface.validateAccount("validateAccount", userModel.toMap());
    }
    public Single<ArrayList<UserModel>> editAccount(UserModel userModel){
        return ecoInterface.editAccount("editAccount", userModel.toMap());
    }

    public Single<ArrayList<CategoryModel>> getCategories(){
        return ecoInterface.getCategories("getMainCategories");
    }
    public Single<ArrayList<ProductModel>> getLimitProductsRecentlyViewed(String userId){
        return ecoInterface.getLimitProductsRecentlyViewed("getLimitProductsRecentlyViewed", userId);
    }
    public Single<ArrayList<SubCategoryModel>> getSubCategories(String categoryId){
        return ecoInterface.getSubCategories("getSubCategories", categoryId);
    }
    public Single<ArrayList<SubCategoryModel>> getSubCategoriesOffer(){
        return ecoInterface.getSubCategoriesOffer("getSubCategoriesOffer");
    }
    public Single<ArrayList<Map<String,Float>>> getSubCategoriesOfferInfo(){
        return ecoInterface.getSubCategoriesOfferInfo("getSubCategoriesOfferInfo");
    }
    public Single<ArrayList<SubCategoryModel>> getSubCategoriesSearched(String query){
        return ecoInterface.getSubCategoriesSearched("getSubCategoriesSearched", query);
    }
    public Single<ArrayList<ProductModel>> getProductsCategory(String categoryId){
        return ecoInterface.getProductsCategory("getProductsCategory", categoryId);
    }
    public Single<ArrayList<ProductModel>> getProducts(String subCategoryId){
        return ecoInterface.getProducts("getProducts", subCategoryId);
    }
    public Single<ArrayList<ProductModel>> getProductsOffer(String subCategoryId){
        return ecoInterface.getProducts("getProductsOffer", subCategoryId);
    }
    public Single<ArrayList<ProductModel>> getLimitSuggestedProducts(String subCategoryId, String productId){
        return ecoInterface.getLimitSuggestedProducts("getLimitSuggestedProducts", subCategoryId, productId);
    }
    public Single<ArrayList<ProductModel>> getLimitBoughtProducts(String subCategoryId, String productId, String userId){
        return ecoInterface.getLimitBoughtProducts("getLimitBoughtProducts", subCategoryId, productId, userId);
    }
    public Single<ArrayList<ProductModel>> getSelectedProducts(ArrayList<String> productIds){
        return ecoInterface.getSelectedProducts("getSelectedProducts", productIds);
    }
    public Single<ArrayList<ProductModel>> getProduct(String productId, String userId){
        return ecoInterface.getProduct("getProduct", productId, userId);
    }
    public Single<ArrayList<ImageProductModel>> getImagesProduct(String productId){
        return ecoInterface.getImagesProduct("getImagesProduct", productId);
    }
    public Single<ArrayList<ProductModel>> getProductsSearched(String query){
        return ecoInterface.getProductsSearched("getProductsSearched", query);
    }
    public Single<ArrayList<ReviewModel>> getLimitReviews(String productId){
        return ecoInterface.getLimitReviews("getLimitReviews", productId);
    }
    public Single<ArrayList<UserModel>> getLimitUsersReview(String productId){
        return ecoInterface.getLimitUsersReview("getLimitUsersReview", productId);
    }
    public Single<ArrayList<UserModel>> getUsersReview(String productId){
        return ecoInterface.getUsersReview("getUsersReview", productId);
    }
    public Single<ArrayList<ReviewModel>> getReviews(String productId){
        return ecoInterface.getReviews("getReviews", productId);
    }
    public Single<String> addReview(ReviewModel reviewModel, String productId){
        return ecoInterface.addReview("addReview", reviewModel.toMap(productId));
    }

    public Single<ArrayList<BrandModel>> getBrandsSearched(String query){
        return ecoInterface.getBrandsSearched("getBrandsSearched", query);
    }
    public Single<ArrayList<SellerModel>> getSelectedSellers(ArrayList<String> sellerIds){
        return ecoInterface.getSelectedSellers("getSelectedSellers", sellerIds);
    }

    public Single<String> addAddress(AddressModel addressModel){
        return ecoInterface.setAddress("addAddress", addressModel.toMap());
    }

    public Single<String> editAddress(AddressModel addressModel){
        return ecoInterface.setAddress("editAddress", addressModel.toMap());
    }

    public Single<String> removeAddress(String addressId){
        return ecoInterface.removeAddress("removeAddress", addressId);
    }

    public Single<ArrayList<AddressModel>> getAddresses(String userId){
        return ecoInterface.getAddresses("getAddresses", userId);
    }

    public Single<String> validateMobileNumber(String countryCodeName, String mobileNumber, String userId){
        return ecoInterface.validateMobileNumber("validateMobileNumber", countryCodeName, mobileNumber, userId);
    }

    public Single<ArrayList<String>> sendOrder(String orderId, ArrayList<String> productsId, String userId,
                                               String userToken, String addressId, ArrayList<Integer> productsCount,
                                               String paymentOption, double subTotal, double shippingFee){

        return ecoInterface.sendOrder("sendOrder", orderId, productsId, userId, userToken, addressId, productsCount,
                paymentOption, subTotal, shippingFee);
    }
    public Single<ArrayList<SellerModel>> getSeller(String sellerId){
        return ecoInterface.getSeller("getSeller", sellerId);
    }

    public Single<ArrayList<ProductModel>> getSellerProducts(String sellerId){
        return ecoInterface.getSellerProducts("getSellerProducts", sellerId);
    }
    public Single<ArrayList<SubCategoryModel>> getSellerSubCategories(String sellerId){
        return ecoInterface.getSellerSubCategories("getSellerSubCategories", sellerId);
    }
    public Single<ArrayList<BrandModel>> getSellerBrands(String sellerId){
        return ecoInterface.getSellerBrands("getSellerBrands", sellerId);
    }
    public Single<ArrayList<ReviewModel>> getSellerReviews(String sellerId){
        return ecoInterface.getSellerReviews("getSellerReviews", sellerId);
    }

    public Single<String> getEmail(String userName){
        return ecoInterface.getEmail("getEmail", userName);
    }
    public Single<String> editPassword(String email, String password){
        return ecoInterface.editPassword("editPassword", email, password);
    }

    public Single<ArrayList<OrderModel>> getOrders(String userId){
        return ecoInterface.getOrders("getOrders", userId);
    }
    public Single<ArrayList<OrderModel>> getOrder(String orderId){
        return ecoInterface.getOrder("getOrder", orderId);
    }
    public Single<ArrayList<ProductModel>> getProductsOrder(String orderId){
        return ecoInterface.getProductsOrder("getProductsOrder", orderId);
    }
    public Single<ArrayList<AddressModel>> getAddressOrder(String orderId){
        return ecoInterface.getAddressOrder("getAddressOrder", orderId);
    }
}
