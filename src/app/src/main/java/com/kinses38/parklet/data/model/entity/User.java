package com.kinses38.parklet.data.model.entity;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class User implements Serializable {

    private String uid, name, email;

    @Exclude
    private boolean isNew, isCreated, isAuthenticated;

    public User(){
        //empty constructor required for firebase
    }

    public User(String uid, String name, String email){
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName(){
        if(name.contains(" ")){
            return name.split(" ")[0];
        }else{
            return name;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean checkIsNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean checkIsCreated() {
        return isCreated;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    public boolean checkIsAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }
}
