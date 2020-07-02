package com.example.ecommerceapp.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class BrandModel {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    public BrandModel() {
        this.id = "";
        this.name = "";
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


    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        map.put("id",id);
        map.put("name",name);

        return map;
    }


}
