package com.example.ankit.socio.models;

import com.google.firebase.database.Exclude;

public class Location {
    public double latitude;
    public double longitude;
    public String name;
    public String address;

    public Location() {

    }

    public Location(double latitude, double longitude, String name, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
    }

    @Exclude
    public String getCityState() {
        return "";
    }
}