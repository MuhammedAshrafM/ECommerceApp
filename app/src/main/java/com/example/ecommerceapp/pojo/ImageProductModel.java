package com.example.ecommerceapp.pojo;

import com.google.gson.annotations.SerializedName;

public class ImageProductModel {


    @SerializedName("id")
    private String id;
    @SerializedName("imagePath")
    private String imagePath;
    @SerializedName("productId")
    private String productId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
