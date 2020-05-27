package com.example.ecommerceapp.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class SubCategoryModel {


    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("imagePath")
    private String imagePath;
    @SerializedName("categoryId")
    private String categoryId;

    public SubCategoryModel() {
    }

    public SubCategoryModel(String id, String name, String imagePath, String categoryId) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.categoryId = categoryId;
    }

    public SubCategoryModel(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        map.put("id",id);
        map.put("name",name);
        map.put("imagePath",imagePath);
        map.put("categoryId",categoryId);

        return map;
    }

    public SubCategoryModel toObjectFromMap(Map<String, Object> map){
        this.id = (String) map.get("id");
        this.name = (String) map.get("name");
        this.imagePath = (String) map.get("imagePath");
        this.categoryId = (String) map.get("categoryId");

        return new SubCategoryModel(id, name, imagePath, categoryId);
    }
}
