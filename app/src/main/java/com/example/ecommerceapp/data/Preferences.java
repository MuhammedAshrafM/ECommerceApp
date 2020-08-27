package com.example.ecommerceapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class Preferences {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context _context;
    // mode type
    private int PRIVATE_MODE = 0;

    private static Preferences INSTANCE;

    private static final String IS_DATA_USER_EXIST = "IsDataUserExist";
    private static final String ID_USER = "id";
    private static final String NAME_USER = "name";
    private static final String EMAIL_USER = "email";
    private static final String USER_NAME = "UserName";
    private static final String PASSWORD_USER = "password";
    private static final String IMAGE_PROFILE_USER = "imageProfilePath";
    private static final String TOKEN_USER = "token";
    private static final String VALIDATED_USER = "validated";
    private static final String PRODUCTS_CARTED = "productsCarted";
    private static final String PRODUCTS_WISHED = "productsWished";
    private static final String ADDRESSES_SAVED = "addressSaved";


    // create Shared Preferences by a name and mode type
    public Preferences(Context context, String PREFERENCES_NAME) {
        this._context = context;
        preferences = _context.getSharedPreferences(PREFERENCES_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public synchronized static Preferences getINSTANCE(Context context, String PREFERENCES_NAME){

        if(INSTANCE == null){
            INSTANCE = new Preferences(context, PREFERENCES_NAME);
        }
        return INSTANCE;
    }

    // delete all data for this Shared Preferences
    public void deleteSharedPreferencesData() {
        editor.clear().commit();
    }

    // set true for Shared Preferences if user add normal data, and the opposite false
    public void setDataUserExist(boolean isDataUserExist) {
        editor.putBoolean(IS_DATA_USER_EXIST, isDataUserExist);
        editor.commit();
    }

    // check to adding normal data or not
    public boolean isDataUserExist() {
        return preferences.getBoolean(IS_DATA_USER_EXIST, false);
    }

    // set normal data of the user
    public void setDataUser(UserModel user){
        editor.putString(ID_USER, user.getId());
        editor.putString(NAME_USER, user.getName());
        editor.putString(USER_NAME, user.getUserName());
        editor.putString(EMAIL_USER, user.getEmail());
        editor.putString(PASSWORD_USER, user.getPassword());
        editor.putString(IMAGE_PROFILE_USER, user.getImagePath());
        editor.putString(TOKEN_USER, user.getToken());
        editor.putInt(VALIDATED_USER, user.getValidated());
        editor.putBoolean(IS_DATA_USER_EXIST, true);
        editor.commit();
    }


    public void validateAccountUser(UserModel user){
        editor.putString(EMAIL_USER, user.getEmail());
        editor.putString(IMAGE_PROFILE_USER, user.getImagePath());
        editor.putInt(VALIDATED_USER, user.getValidated());
        editor.commit();
    }

    // get normal data of the user
    public UserModel getDataUser() {
        UserModel user = new UserModel();

        user.setId(preferences.getString(ID_USER, ""));
        user.setName(preferences.getString(NAME_USER, ""));
        user.setUserName(preferences.getString(USER_NAME, ""));
        user.setEmail(preferences.getString(EMAIL_USER, ""));
        user.setPassword(preferences.getString(PASSWORD_USER, ""));
        user.setImagePath(preferences.getString(IMAGE_PROFILE_USER, ""));
        user.setToken(preferences.getString(TOKEN_USER, ""));
        user.setValidated(preferences.getInt(VALIDATED_USER, 0));

        return user;
    }

    public void removeDataUser(){
        editor.remove(ID_USER);
        editor.remove(NAME_USER);
        editor.remove(USER_NAME);
        editor.remove(EMAIL_USER);
        editor.remove(PASSWORD_USER);
        editor.remove(IMAGE_PROFILE_USER);
        editor.remove(TOKEN_USER);
        editor.remove(VALIDATED_USER);
        editor.commit();
    }

    public ArrayList<String> setProductCarted(ProductModel productModel){
        ArrayList<String> productModels =  new ArrayList<>();
        String json = preferences.getString(PRODUCTS_CARTED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        if(json != null){
            productModels = gson.fromJson(json, type);
        }

        productModels.add(productModel.getId());
        json = gson.toJson(productModels);
        editor.putString(PRODUCTS_CARTED, json);
        editor.commit();

        return productModels;
    }
    public ArrayList<String> setProductsCarted(ArrayList<String> productModels){
        Gson gson = new Gson();

        String json = gson.toJson(productModels);
        editor.putString(PRODUCTS_CARTED, json);
        editor.commit();

        return productModels;
    }
    public ArrayList<String> removeProductCarted(ProductModel productModel){
        String json = preferences.getString(PRODUCTS_CARTED, null);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = (JsonElement) parser.parse(gson.toJson(productModel.getId()));
        JsonArray jsonArray = (JsonArray) parser.parse(json);
        jsonArray.remove(jsonElement);
        json = jsonArray.toString();

        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        editor.putString(PRODUCTS_CARTED, json);
        editor.commit();

        return gson.fromJson(json, type);
    }
    public void removeProductsCarted(){
//        editor.putString(PRODUCTS_CARTED, null);
//        editor.commit();

        editor.remove(PRODUCTS_CARTED).commit();
    }

    public boolean isProductCarted(ProductModel productModel){
        String json = preferences.getString(PRODUCTS_CARTED, null);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        if(json != null){
            JsonElement jsonElement = (JsonElement) parser.parse(gson.toJson(productModel.getId()));
            JsonArray jsonArray = (JsonArray) parser.parse(json);

            return jsonArray.contains(jsonElement);
        }
        return false;
    }
    public ArrayList<String> getProductsCarted(){
        ArrayList<String> productModels =  new ArrayList<>();
        String json = preferences.getString(PRODUCTS_CARTED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        if(json != null){
            productModels = gson.fromJson(json, type);
        }

        return productModels;
    }

    public ArrayList<String> setProductWished(ProductModel productModel){
        ArrayList<String> productModels =  new ArrayList<>();
        String json = preferences.getString(PRODUCTS_WISHED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        if(json != null){
            productModels = gson.fromJson(json, type);
        }

        productModels.add(productModel.getId());
        json = gson.toJson(productModels);
        editor.putString(PRODUCTS_WISHED, json);
        editor.commit();

        return productModels;
    }

//    public ArrayList<String> setProductsWished(ArrayList<String> productModels){
//        Gson gson = new Gson();
//
//        String json = gson.toJson(productModels);
//        editor.putString(PRODUCTS_WISHED, json);
//        editor.commit();
//
//        return productModels;
//    }


    public ArrayList<String> removeProductWished(ProductModel productModel){
        String json = preferences.getString(PRODUCTS_WISHED, null);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = (JsonElement) parser.parse(gson.toJson(productModel.getId()));
        JsonArray jsonArray = (JsonArray) parser.parse(json);
        jsonArray.remove(jsonElement);
        json = jsonArray.toString();

        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        editor.putString(PRODUCTS_WISHED, json);
        editor.commit();

        return gson.fromJson(json, type);
    }
    public void removeProductsWished(){
        editor.remove(PRODUCTS_WISHED).commit();
    }

    public boolean isProductWished(ProductModel productModel){
        String json = preferences.getString(PRODUCTS_WISHED, null);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        if(json != null){
            JsonElement jsonElement = (JsonElement) parser.parse(gson.toJson(productModel.getId()));
            JsonArray jsonArray = (JsonArray) parser.parse(json);

            return jsonArray.contains(jsonElement);
        }
        return false;
    }
    public ArrayList<String> getProductsWished(){
        ArrayList<String> productModels =  new ArrayList<>();
        String json = preferences.getString(PRODUCTS_WISHED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        if(json != null){
            productModels = gson.fromJson(json, type);
        }

        return productModels;
    }

    public ArrayList<AddressModel> setAddressSaved(AddressModel addressModel){
        ArrayList<AddressModel> addressModels =  new ArrayList<>();
        String json = preferences.getString(ADDRESSES_SAVED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AddressModel>>(){}.getType();

        if(json != null){
            addressModels = gson.fromJson(json, type);
        }

        int size = addressModels.size();
        addressModels.add(addressModel);
        if(size != 0){
            Collections.swap(addressModels, 0, size);
        }

        json = gson.toJson(addressModels);
        editor.putString(ADDRESSES_SAVED, json);
        editor.commit();

        return addressModels;
    }

    public void setAddressesSaved(ArrayList<AddressModel> addressModels){
        Gson gson = new Gson();
        String json = gson.toJson(addressModels);
        editor.putString(ADDRESSES_SAVED, json);
        editor.commit();
    }

    public ArrayList<AddressModel> editAddressSaved(AddressModel addressModel){
        ArrayList<AddressModel> addressModels =  new ArrayList<>();
        String json = preferences.getString(ADDRESSES_SAVED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AddressModel>>(){}.getType();

        if(json != null){
            addressModels = gson.fromJson(json, type);
        }

        addressModels.remove(0);
        addressModels.add(0, addressModel);
        json = gson.toJson(addressModels);
        editor.putString(ADDRESSES_SAVED, json);
        editor.commit();

        return addressModels;
    }
    public ArrayList<AddressModel> removeAddressSaved(AddressModel addressModel){
        String json = preferences.getString(ADDRESSES_SAVED, null);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = (JsonElement) parser.parse(gson.toJson(addressModel));
        JsonArray jsonArray = (JsonArray) parser.parse(json);
        jsonArray.remove(jsonElement);
        json = jsonArray.toString();

        Type type = new TypeToken<ArrayList<AddressModel>>(){}.getType();

        editor.putString(ADDRESSES_SAVED, json);
        editor.commit();

        return gson.fromJson(json, type);
    }
    public void removeAddressesSaved(){
        editor.remove(ADDRESSES_SAVED).commit();
    }

    public ArrayList<AddressModel> getAddressesSaved(){
        ArrayList<AddressModel> addressModels =  new ArrayList<>();
        String json = preferences.getString(ADDRESSES_SAVED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AddressModel>>(){}.getType();

        if(json != null){
            addressModels = gson.fromJson(json, type);
        }

        return addressModels;
    }
    public void validateMobileNumber(String mobileNumber, String countryCodeName){
        ArrayList<AddressModel> addressModels =  new ArrayList<>();
        String json = preferences.getString(ADDRESSES_SAVED, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AddressModel>>(){}.getType();

        if(json != null){
            addressModels = gson.fromJson(json, type);
        }

        for(int i = 0; addressModels.size() > i; i++){
            if(addressModels.get(i).getMobileNumber().equals(mobileNumber) &&
                    addressModels.get(i).getCountryCodeName().equals(countryCodeName)){
                addressModels.get(i).setMobileNumberValidated(1);
            }
        }

        json = gson.toJson(addressModels);
        editor.putString(ADDRESSES_SAVED, json);
        editor.commit();
    }

    public void selectAddressSaved(int index){

        ArrayList<AddressModel> addressModels = getAddressesSaved();
        int size = addressModels.size();
        if(size > 1) {
            if(index != 0){
                Collections.swap(addressModels, 0, index);
                setAddressesSaved(addressModels);
            }
        }
    }
    public AddressModel getAddressSavedSelected(){
        AddressModel addressModel;

        addressModel = getAddressesSaved().get(0);

        return addressModel;
    }


}