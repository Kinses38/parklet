package com.kinses38.parklet.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kinses38.parklet.data.model.entity.Property;

import java.util.ArrayList;
import java.util.List;

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

    public MutableLiveData<List<Property>> selectAll(){
        MutableLiveData<List<Property>> userPropertiesMutableLiveData = new MutableLiveData<>();
        DatabaseReference allProperties = DB.child("properties/"+userUid);
        allProperties.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Property> properties = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Property userProperty = ds.getValue(Property.class);
                    properties.add(userProperty);
                }
                userPropertiesMutableLiveData.postValue(properties);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("PropertyRepo", databaseError.getMessage());
            }
        });

        return userPropertiesMutableLiveData;
    }
}
