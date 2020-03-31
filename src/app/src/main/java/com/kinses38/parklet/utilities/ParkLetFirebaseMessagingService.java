package com.kinses38.parklet.utilities;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

public class ParkLetFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = this.getClass().getSimpleName();
    private final DatabaseReference DB = FirebaseDatabase.getInstance().getReference("users/");

    @Override
    public void onNewToken(@NotNull String token){
        Log.d(TAG, "Token refreshed: " + token);
        updateUsersToken(token);
    }

    //In the case of uninstall/updates/changing device
    private void updateUsersToken(String token){
       FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
       if(currentUser != null){
           String uid = currentUser.getUid();
           DB.child(uid).child("fcmToken").setValue(token).addOnSuccessListener(aVoid ->
                   Log.i(TAG, "User: "+ uid + " token updated"));
       }
    }

}
