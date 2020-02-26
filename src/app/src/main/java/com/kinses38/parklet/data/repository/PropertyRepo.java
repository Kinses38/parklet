package com.kinses38.parklet.data.repository;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kinses38.parklet.data.model.entity.Property;

public class PropertyRepo {

    //TODO refactor to static global paths?
    //TODO check for existing property using....?Eircode?

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference("propertyLocations");
    private GeoFire geoFire = new GeoFire(geoFireRef);

    public void create(Property property){
        String propertyKey = DB.child("properties"+ userUid).push().getKey();
        DatabaseReference propertyRef = DB.child("properties/" + userUid + "/" + propertyKey);
        property.setOwnerUID(userUid);
        propertyRef.setValue(property).addOnSuccessListener(aVoid -> {
            Log.i("PropertyRepo", "Property added");
                })
                .addOnFailureListener(e ->
                        Log.i("PropertyRepo", "Property not added" + e.getMessage()));

        geoFire.setLocation(propertyKey, new GeoLocation(property.getLatitude(), property.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if(error == null){
                    Log.i("PropertyRepo", "PropertyLocation saved");
                }else{
                    Log.i("PropertyRepo", error.getMessage());
                }
            }
        });
    }
}
