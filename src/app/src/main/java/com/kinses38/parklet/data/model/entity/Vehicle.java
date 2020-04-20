package com.kinses38.parklet.data.model.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Vehicle extends BaseObservable implements Serializable {

    private String reg, model, make, ownerUID;

    public Vehicle() {
        //Empty constructor for firebase
    }

    public Vehicle(String make, String model, String reg) {
        this.make = make;
        this.model = model;
        this.reg = reg;
    }

    @Bindable
    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
        notifyPropertyChanged(BR.vehicle);
    }

    @Bindable
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
        notifyPropertyChanged(BR.vehicle);
    }

    @Bindable
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
        notifyPropertyChanged(BR.vehicle);
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    @Override
    public String toString() {
        return this.getMake() + ": " + this.getModel();
    }
}
