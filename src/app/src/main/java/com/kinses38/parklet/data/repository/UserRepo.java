package com.kinses38.parklet.data.repository;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kinses38.parklet.data.model.entity.User;

public class UserRepo {

    public void setNewUser(User user) {
        //TODO constants
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("users").child(user.getUid());

         myRef.setValue(user)
                 .addOnSuccessListener(aVoid -> {
                     Log.i("UserRepo", "New User added successfully");
                 })
         .addOnFailureListener(e -> {
             Log.i("UserRepo", "Add new user failed");
         });

    }

}
