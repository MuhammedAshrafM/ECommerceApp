package com.example.ecommerceapp.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class InfoReviewsViewModel extends ViewModel {
    private MutableLiveData<Map<String, Object>> mutableLiveReviewsInfo;
    private MutableLiveData<Map<String, Integer>> mutableLiveReviewsInfoSeller;

    public InfoReviewsViewModel() {
        this.mutableLiveReviewsInfo = new MutableLiveData<>();
        this.mutableLiveReviewsInfoSeller = new MutableLiveData<>();
    }

    public MutableLiveData<Map<String, Object>> getMutableLiveReviewsInfo() {
        return mutableLiveReviewsInfo;
    }

    public void setMutableLiveReviewsInfo(Map<String, Object> map) {
        this.mutableLiveReviewsInfo.setValue(map);
    }

    public MutableLiveData<Map<String, Integer>> getMutableLiveReviewsInfoSeller() {
        return mutableLiveReviewsInfoSeller;
    }

    public void setMutableLiveReviewsInfoSeller(Map<String, Integer> map) {
        this.mutableLiveReviewsInfoSeller.setValue(map);
    }
}
