package com.kinses38.parklet.data.model.entity;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {

    private String ownerUID, renterUID, propertyUID, renterVehicleReg;
    private String propertyAddress, renterName, ownerName;
    private double priceAtTime, priceTotal;
    private boolean customerCancelled, ownerCancelled, bookingComplete;
    private List<Long> bookingDates;

    public Booking() {
        //Firebase empty constructor required
    }

    public Booking(String ownerUID, String ownerName, String propertyUID, String propertyAddress,
                   String renterVehicleReg, double priceAtTime, double priceTotal,
                   List<Long> bookingDates) {
        this.ownerUID = ownerUID;
        this.ownerName = ownerName;
        this.propertyUID = propertyUID;
        this.propertyAddress = propertyAddress;
        this.renterVehicleReg = renterVehicleReg;
        this.priceAtTime = priceAtTime;
        this.priceTotal = priceTotal;
        this.bookingDates = bookingDates;
    }

    public String getPropertyUID() {
        return propertyUID;
    }

    public void setPropertyUID(String propertyUID) {
        this.propertyUID = propertyUID;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public String getRenterUID() {
        return renterUID;
    }

    public void setRenterUID(String renterUID) {
        this.renterUID = renterUID;
    }

    public double getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public double getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(double priceTotal) {
        this.priceTotal = priceTotal;
    }

    public boolean isCustomerCancelled() {
        return customerCancelled;
    }

    public void setCustomerCancelled(boolean customerCancelled) {
        this.customerCancelled = customerCancelled;
    }

    public boolean isOwnerCancelled() {
        return ownerCancelled;
    }

    public void setOwnerCancelled(boolean ownerCancelled) {
        this.ownerCancelled = ownerCancelled;
    }

    public boolean isBookingComplete() {
        return bookingComplete;
    }

    public void setBookingComplete(boolean bookingComplete) {
        this.bookingComplete = bookingComplete;
    }

    public List<Long> getBookingDates() {
        return bookingDates;
    }

    public void setBookingDates(List<Long> bookingDates) {
        this.bookingDates = bookingDates;
    }

    public String getRenterVehicleReg() {
        return renterVehicleReg;
    }

    public void setRenterVehicleReg(String renterVehicleReg) {
        this.renterVehicleReg = renterVehicleReg;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}