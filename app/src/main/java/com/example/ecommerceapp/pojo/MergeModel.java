package com.example.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MergeModel extends ProductModel implements Parcelable {

    @SerializedName("idSubCategory")
    private String subCategoryIds;
    @SerializedName("nameSubCategory")
    private String subCategoryName;
    @SerializedName("imagePathSubCategory")
    private String subCategoryImagePath;
    @SerializedName("categoryId")
    private String categoryId;

    @SerializedName("reviewsCount")
    private int reviewsCount;
    @SerializedName("reviewsRateAverage")
    private float reviewsRateAverage;


    protected MergeModel(Parcel in) {
        subCategoryIds = in.readString();
        subCategoryName = in.readString();
        subCategoryImagePath = in.readString();
        categoryId = in.readString();
        reviewsCount = in.readInt();
        reviewsRateAverage = in.readFloat();
    }

    public static final Creator<MergeModel> CREATOR = new Creator<MergeModel>() {
        @Override
        public MergeModel createFromParcel(Parcel in) {
            return new MergeModel(in);
        }

        @Override
        public MergeModel[] newArray(int size) {
            return new MergeModel[size];
        }
    };

    public String getSubCategoryIds() {
        return subCategoryIds;
    }

    public void setSubCategoryIds(String subCategoryIds) {
        this.subCategoryIds = subCategoryIds;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryImagePath() {
        return subCategoryImagePath;
    }

    public void setSubCategoryImagePath(String subCategoryImagePath) {
        this.subCategoryImagePath = subCategoryImagePath;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public float getReviewsRateAverage() {
        return reviewsRateAverage;
    }

    public void setReviewsRateAverage(float reviewsRateAverage) {
        this.reviewsRateAverage = reviewsRateAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subCategoryIds);
        parcel.writeString(subCategoryName);
        parcel.writeString(subCategoryImagePath);
        parcel.writeString(categoryId);
        parcel.writeInt(reviewsCount);
        parcel.writeFloat(reviewsRateAverage);
    }
}
