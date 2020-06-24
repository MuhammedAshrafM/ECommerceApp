package com.example.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ReviewModel implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("comment")
    private String comment;
    @SerializedName("rate")
    private float rate;
    @SerializedName("time")
    private String time;
    @SerializedName("userId")
    private String userId;


    public ReviewModel(String id, String comment, float rate, String time, String userId) {
        this.id = id;
        this.comment = comment;
        this.rate = rate;
        this.time = time;
        this.userId = userId;
    }

    public ReviewModel() {
    }

    protected ReviewModel(Parcel in) {
        id = in.readString();
        comment = in.readString();
        rate = in.readFloat();
        time = in.readString();
        userId = in.readString();
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override
        public ReviewModel createFromParcel(Parcel in) {
            return new ReviewModel(in);
        }

        @Override
        public ReviewModel[] newArray(int size) {
            return new ReviewModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        map.put("id",id);
        map.put("comment",comment);
        map.put("rate",rate);
        map.put("time",time);
        map.put("userId",userId);

        return map;
    }
    public Map<String, Object> toMap(String productId){
        Map<String, Object> map = new HashMap<>();

        map.put("comment",comment);
        map.put("rate",rate);
        map.put("userId",userId);
        map.put("productId",productId);

        return map;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(comment);
        parcel.writeFloat(rate);
        parcel.writeString(time);
        parcel.writeString(userId);
    }

    public static Comparator<ReviewModel> mostHelpfulComparator = new Comparator<ReviewModel>() {
        @Override
        public int compare(ReviewModel m1, ReviewModel m2) {
            String p1 = m1.getId();
            String p2 = m2.getId();

            return p1.compareTo(p2);
        }
    };

    public static Comparator<ReviewModel> lowestRatingComparator = new Comparator<ReviewModel>() {
        @Override
        public int compare(ReviewModel m1, ReviewModel m2) {
            String rate1 = String.valueOf(m1.getRate());
            String rate2 = String.valueOf(m2.getRate());

            return rate1.compareTo(rate2);
        }
    };

    public static Comparator<ReviewModel> highestRatingComparator = new Comparator<ReviewModel>() {
        @Override
        public int compare(ReviewModel m1, ReviewModel m2) {
            String rate1 = String.valueOf(m1.getRate());
            String rate2 = String.valueOf(m2.getRate());

            return rate2.compareTo(rate1);
        }
    };

    public static Comparator<ReviewModel> mostRecentComparator = new Comparator<ReviewModel>() {
        @Override
        public int compare(ReviewModel m1, ReviewModel m2) {
            String rate1 = String.valueOf(m1.getTime());
            String rate2 = String.valueOf(m2.getTime());

            return rate2.compareTo(rate1);
        }
    };

    public static ArrayList<UserModel> sortList(ArrayList<ReviewModel> reviewModels, ArrayList<UserModel> userModels) {

        HashMap<Object, Integer> indexMap = new HashMap<>();
        int index = 0;
        for (ReviewModel reviewModel : reviewModels) {
            indexMap.put(reviewModels.get(index).getUserId(), index);
            index++;
        }

        Collections.sort(userModels, new Comparator<UserModel>() {

            public int compare(UserModel u1, UserModel u2) {

                Integer leftIndex = indexMap.get(u1.getId());
                Integer rightIndex = indexMap.get(u2.getId());
//                if (leftIndex == null) {
//                    return -1;
//                }
//                if (rightIndex == null) {
//                    return 1;
//                }

                return Integer.compare(leftIndex, rightIndex);
            }
        });

        return userModels;
    }
}
