package com.rapandroid.dpw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeModel {
    String name;
    String desc;
    double price;
    String date;
    String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HomeModel(){

    }

    public HomeModel(String name, String desc, double price, String date, String id) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.date = date;
        this.id = id;
    }
}
