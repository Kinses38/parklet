package com.kinses38.parklet.data.model.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Property implements Serializable {

    //EirCode used as natural Key
    private String addressLine, eircode, ownerUID, ownerName;
    private Double dailyRate, propertyRating = 0.0;
    private Boolean takingBookings = true, weekendBookings = true;
    private Double longitude, latitude;
    @Exclude
    private Double averageComparison = 0.0;

    public Property() {
        //for firebase
    }

    public Property(String addressLine, String eircode, Double dailyRate,
                    Double longitude, Double latitude) {
        this.addressLine = addressLine;
        this.eircode = eircode;
        this.dailyRate = dailyRate;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getEircode() {
        return eircode;
    }

    public void setEircode(String eircode) {
        this.eircode = eircode;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Double getPropertyRating() {
        return propertyRating;
    }

    public void setPropertyRating(Double propertyRating) {
        this.propertyRating = propertyRating;
    }

    public Boolean getTakingBookings() {
        return takingBookings;
    }

    public void setTakingBookings(Boolean takingBookings) {
        this.takingBookings = takingBookings;
    }

    public Boolean getWeekendBookings() {
        return weekendBookings;
    }

    public void setWeekendBookings(Boolean weekendBookings) {
        this.weekendBookings = weekendBookings;
    }

    public void parseWeekend(String availableWeekend) {
        this.weekendBookings = availableWeekend.toLowerCase().equals("yes");
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Exclude
    public LatLng getLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Exclude
    public void setAverageComparison(Double areaAverage) {
        if (areaAverage != 0) {
            double percentage = (areaAverage - this.dailyRate) / areaAverage * 100;
            averageComparison = percentage;
        } else {
            averageComparison = 0.0;
        }
    }

    @Exclude
    public double getAverageComparison() {
        return this.averageComparison;
    }
}

