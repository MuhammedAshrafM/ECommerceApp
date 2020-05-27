package com.example.ecommerceapp.pojo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class CategoryModel {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("imagePath")
    private String imagePath;

    public CategoryModel(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    protected CategoryModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        imagePath = in.readString();
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        map.put("id",id);
        map.put("name",name);
        map.put("imagePath",imagePath);

        return map;
    }

}
