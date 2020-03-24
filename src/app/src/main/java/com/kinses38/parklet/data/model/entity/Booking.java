package com.kinses38.parklet.data.model.entity;
import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {

    private String ownerUID, renterUID, propertyUID, renterVehicleReg;
    private double priceAtTime, priceTotal;
    private boolean customerCancelled, ownerCancelled, bookingComplete;
    private List<Long> bookingDates;

    public Booking() {
        //Firebase empty constructor required
    }

    public Booking(String ownerUID, String propertyUID, String renterVehicleReg, double priceAtTime, double priceTotal,
                   List<Long> bookingDates) {
        this.ownerUID = ownerUID;
        this.propertyUID = propertyUID;
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
}