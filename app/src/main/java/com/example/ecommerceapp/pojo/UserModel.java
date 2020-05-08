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
    @SerializedName("validated")
    private int validated;

    public UserModel(String name, String userName, String password) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.id = "";
        this.email = "";
        this.validated = 0;
    }

    public UserModel(String name,String email) {
        this.name = name;
        this.email = email;
        this.id = "";
        this.validated = 0;
        this.userName = "";
        this.password = "";
    }

    public UserModel(String userName,String password, boolean b) {
        this.userName = userName;
        this.password = password;
        this.name = "";
        this.email = "";
        this.id = "";
        this.validated = 0;
    }

    public UserModel(String email) {
        this.email = email;
        this.name = "";
        this.id = "";
        this.validated = 0;
        this.userName = "";
        this.password = "";
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
        map.put("validated",validated);

        return map;
    }


}
