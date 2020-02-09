package com.kinses38.parklet.data.model.entity;

import java.io.Serializable;

public class Vehicle implements Serializable {

    private String reg, model, make;
    //TODO do we need user uuid here?

    public Vehicle(){
        //Empty constructor for firebase
    }

    public Vehicle(String make, String model, String reg){
        this.make = make;
        this.model = model;
        this.reg = reg;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }
}
