package com.kinses38.parklet;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class ParkLet extends Application {

    public void onCreate(){
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
