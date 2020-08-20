package com.example.ecommerceapp.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Map;

public class InfoProductsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Double>> mutableLiveProductsInfo;

    public InfoProductsViewModel() {
        this.mutableLiveProductsInfo = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Double>> getMutableLiveProductsInfo() {
        return mutableLiveProductsInfo;
    }

    public void setMutableLiveProductsInfo(ArrayList<Double> arrayList) {
        this.mutableLiveProductsInfo.setValue(arrayList);
    }
}
