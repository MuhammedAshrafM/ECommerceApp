package com.example.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class AddressModel implements Parcelable {


    @SerializedName("id")
    String id;
    @SerializedName("recipientName")
    String recipientName;
    @SerializedName("address")
    String address;
    @SerializedName("mobileNumber")
    String mobileNumber;
    @SerializedName("countryCodeName")
    String countryCodeName;
    @SerializedName("mobileNumberValidated")
    int mobileNumberValidated;
    @SerializedName("buildingNumber")
    String buildingNumber;
    @SerializedName("floorNumber")
    String floorNumber;
    @SerializedName("apartmentNumber")
    String apartmentNumber;
    @SerializedName("userId")
    private String userId;


    public AddressModel() {
    }

    public AddressModel(String id, String recipientName, String address, String mobileNumber, String countryCodeName,
                        int mobileNumberValidated, String buildingNumber, String floorNumber,
                        String apartmentNumber, String userId) {
        this.id = id;
        this.recipientName = recipientName;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.countryCodeName = countryCodeName;
        this.mobileNumberValidated = mobileNumberValidated;
        this.buildingNumber = buildingNumber;
        this.floorNumber = floorNumber;
        this.apartmentNumber = apartmentNumber;
        this.userId = userId;
    }

    protected AddressModel(Parcel in) {
        id = in.readString();
        recipientName = in.readString();
        address = in.readString();
        mobileNumber = in.readString();
        countryCodeName = in.readString();
        mobileNumberValidated = in.readInt();
        buildingNumber = in.readString();
        floorNumber = in.readString();
        apartmentNumber = in.readString();
        userId = in.readString();
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCountryCodeName() {
        return countryCodeName;
    }

    public void setCountryCodeName(String countryCodeName) {
        this.countryCodeName = countryCodeName;
    }

    public int getMobileNumberValidated() {
        return mobileNumberValidated;
    }

    public void setMobileNumberValidated(int mobileNumberValidated) {
        this.mobileNumberValidated = mobileNumberValidated;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static Creator<AddressModel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(recipientName);
        parcel.writeString(address);
        parcel.writeString(mobileNumber);
        parcel.writeString(countryCodeName);
        parcel.writeInt(mobileNumberValidated);
        parcel.writeString(buildingNumber);
        parcel.writeString(floorNumber);
        parcel.writeString(apartmentNumber);
        parcel.writeString(userId);
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        map.put("id",id);
        map.put("recipientName",recipientName);
        map.put("address",address);
        map.put("mobileNumber",mobileNumber);
        map.put("countryCodeName",countryCodeName);
        map.put("mobileNumberValidated",mobileNumberValidated);
        map.put("buildingNumber",buildingNumber);
        map.put("floorNumber",floorNumber);
        map.put("apartmentNumber",apartmentNumber);
        map.put("userId",userId);

        return map;
    }
}
