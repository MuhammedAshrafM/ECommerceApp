package com.example.ecommerceapp.pojo;

import com.google.gson.annotations.SerializedName;

public class ProductModel {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("price")
    private Double price;
    @SerializedName("offer")
    private Float offer;
    @SerializedName("imagePath")
    private String imagePath;
    @SerializedName("descriptionPath")
    private String descriptionPath;
    @SerializedName("specificationPath")
    private String specificationPath;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("brandId")
    private String brandId;
    @SerializedName("subCategoryId")
    private String subCategoryId;
    @SerializedName("available")
    private int available;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Float getOffer() {
        return offer;
    }

    public void setOffer(Float offer) {
        this.offer = offer;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescriptionPath() {
        return descriptionPath;
    }

    public void setDescriptionPath(String descriptionPath) {
        this.descriptionPath = descriptionPath;
    }

    public String getSpecificationPath() {
        return specificationPath;
    }

    public void setSpecificationPath(String specificationPath) {
        this.specificationPath = specificationPath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
