package com.example.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SellerModel implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("locationLatitude")
    private String locationLatitude;
    @SerializedName("locationLongitude")
    private String locationLongitude;


    public SellerModel() {
    }

    public SellerModel(String id, String name, String locationLatitude, String locationLongitude) {
        this.id = id;
        this.name = name;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    protected SellerModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        locationLatitude = in.readString();
        locationLongitude = in.readString();
    }

    public static final Creator<SellerModel> CREATOR = new Creator<SellerModel>() {
        @Override
        public SellerModel createFromParcel(Parcel in) {
            return new SellerModel(in);
        }

        @Override
        public SellerModel[] newArray(int size) {
            return new SellerModel[size];
        }
    };

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

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(locationLatitude);
        parcel.writeString(locationLongitude);
    }
}
