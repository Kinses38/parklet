package com.kinses38.parklet.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kinses38.parklet.data.model.entity.User;

/**
 *  Responsible for creation of new User profiles and updating cloud messaging token.
 */
public class UserRepo {

    private final String TAG = this.getClass().getSimpleName();
    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference("users");

    public void setNewUser(User user) {
        DB.child(user.getUid()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "New User added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "Add new user failed" + e.getMessage());
                });
    }

    public void updateUserFcmToken(User user) {
        DB.child(user.getUid()).child("fcmToken").setValue(user.getFcmToken())
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Updated user fcmToken");
                }).addOnFailureListener(e -> {
            Log.i(TAG, "Failed to update fcmToken", e.getCause());
        });
    }
}
