package com.example.ecommerceapp.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


public interface MapClickListener extends Serializable {

    void onMapClick(LatLng latLng);
}
