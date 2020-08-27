package com.example.ecommerceapp.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "notification")
public class NotificationModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String body;
    private String summaryText;
    private String imagePath;
    private int destination;
    private String json;
    private String time;
    private boolean displayed;
    private String userId;

    public NotificationModel(String title, String body, String summaryText, String imagePath, int destination,
                             String json, String time, boolean displayed, String userId) {
        this.title = title;
        this.body = body;
        this.summaryText = summaryText;
        this.imagePath = imagePath;
        this.destination = destination;
        this.json = json;
        this.time = time;
        this.displayed = displayed;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }


    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
