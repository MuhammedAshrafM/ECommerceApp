package com.example.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OrderModel implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("orderTime")
    private String orderTime;
    @SerializedName("received")
    private int received;
    @SerializedName("receiptTime")
    private String receiptTime;
    @SerializedName("paymentOption")
    private String paymentOption;
    @SerializedName("subTotal")
    private Double subTotal;
    @SerializedName("shippingFee")
    private int shippingFee;
    @SerializedName("addressId")
    private String addressId;

    public OrderModel() {
    }

    public OrderModel(String id, String userId, String orderTime, int received, String receiptTime, String paymentOption, Double subTotal, int shippingFee, String addressId) {
        this.id = id;
        this.userId = userId;
        this.orderTime = orderTime;
        this.received = received;
        this.receiptTime = receiptTime;
        this.paymentOption = paymentOption;
        this.subTotal = subTotal;
        this.shippingFee = shippingFee;
        this.addressId = addressId;
    }

    protected OrderModel(Parcel in) {
        id = in.readString();
        userId = in.readString();
        orderTime = in.readString();
        received = in.readInt();
        receiptTime = in.readString();
        paymentOption = in.readString();
        if (in.readByte() == 0) {
            subTotal = null;
        } else {
            subTotal = in.readDouble();
        }
        shippingFee = in.readInt();
        addressId = in.readString();
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public String getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(String receiptTime) {
        this.receiptTime = receiptTime;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public int getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(int shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userId);
        parcel.writeString(orderTime);
        parcel.writeInt(received);
        parcel.writeString(receiptTime);
        parcel.writeString(paymentOption);
        if (subTotal == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(subTotal);
        }
        parcel.writeInt(shippingFee);
        parcel.writeString(addressId);
    }
}
