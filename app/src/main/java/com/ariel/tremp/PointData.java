package com.ariel.tremp;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class PointData {
    private final double latitude, longitude;

    public PointData() {
        latitude = 0;
        longitude = 0;
    }

    public PointData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointData(@NonNull LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String toString() {
        return "PointData{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
