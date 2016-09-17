package com.angel.black.baskettogether.recruit.googlemap;

/**
 * Created by KimJeongHun on 2016-09-18.
 */
public class LocationInfo {
    public double latitude;
    public double longitude;
    public String address;

    public LocationInfo(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
