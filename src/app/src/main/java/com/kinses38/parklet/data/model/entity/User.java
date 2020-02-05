package com.kinses38.parklet.data.model.entity;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class User implements Serializable {

    private String uuid, name, email;

    @Exclude
    private boolean isNew, isCreated, isAuthenticated;

    public User(){
        //empty constructor required for firebase
    }

    public User(String uuid, String name, String email){
        this.uuid = uuid;
        this.name = name;
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
