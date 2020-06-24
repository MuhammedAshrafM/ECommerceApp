package com.example.ecommerceapp.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class UserModel {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("userName")
    private String userName;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("imagePath")
    private String imagePath;
    @SerializedName("validated")
    private int validated;

    public UserModel() {
        this.id = "";
        this.name = "";
        this.userName = "";
        this.email = "";
        this.password = "";
        this.imagePath = "";
        this.validated = 0;
    }

    public UserModel(String id, String name, String userName, String email, String password, String imagePath, int validated) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
        this.validated = validated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getValidated() {
        return validated;
    }

    public void setValidated(int validated) {
        this.validated = validated;
    }


    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        map.put("id",id);
        map.put("name",name);
        map.put("userName",userName);
        map.put("email",email);
        map.put("password",password);
        map.put("imagePath",imagePath);
        map.put("validated",validated);

        return map;
    }


}
