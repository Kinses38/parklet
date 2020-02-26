package com.kinses38.parklet.data.model.entity;

public class Property {

    private String addressLine, eircode, ownerUID;
    private Double dailyRate, propertyRating = 0.0;
    private Boolean takingBookings = true, weekendBookings = true;
    private Double longitude, latitude;

    public Property(){
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

    public void setWeekendBookings(String weekEnd) {
        weekendBookings = weekEnd.toLowerCase().equals("yes");
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
}
