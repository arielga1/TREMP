package com.ariel.tremp;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.util.Map;

public class Tremp {
    private PointData fromPoint;
    private PointData toPoint;
    private String fromAddress;
    private String toAddress;
    private String duration;
    private String distance;
    private Time time;

    public Tremp() { }

    public Tremp(PointData fromPoint, PointData toPoint, String fromAddress, String toAddress, String duration, String distance, Time time) {
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.duration = duration;
        this.distance = distance;
        this.time = time;
    }

    public Tremp(PointData fromPoint, PointData toPoint, String fromAddress, String toAddress, String duration, String distance) {
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.duration = duration;
        this.distance = distance;
    }

    public PointData getFromPoint() {
        return fromPoint;
    }

    public void setFromPoint(PointData fromPoint) {
        this.fromPoint = fromPoint;
    }

    public PointData getToPoint() {
        return toPoint;
    }

    public void setToPoint(PointData toPoint) {
        this.toPoint = toPoint;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Tremp{" +
                "fromPoint=" + fromPoint +
                ", toPoint=" + toPoint +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", duration='" + duration + '\'' +
                ", distance='" + distance + '\'' +
                ", time=" + time +
                '}';
    }
}
