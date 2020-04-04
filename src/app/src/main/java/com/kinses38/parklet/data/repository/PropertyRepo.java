package com.kinses38.parklet.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kinses38.parklet.data.model.entity.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyRepo {

    //TODO refactor to static global paths?


    private final String TAG = this.getClass().getSimpleName();

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth ADB = FirebaseAuth.getInstance();
    private DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference("propertyLocations");
    private GeoFire geoFire = new GeoFire(geoFireRef);


    public void create(Property property) {
        //TODO check for existing property using....?Eircode?
        String propertyKey = DB.child("properties").push().getKey();
        DatabaseReference propertyRef = DB.child("properties/" + propertyKey);
        property.setPropertyUID(propertyKey);
        property.setOwnerUID(ADB.getCurrentUser().getUid());
        property.setOwnerName(ADB.getCurrentUser().getDisplayName());
        propertyRef.setValue(property).addOnSuccessListener(aVoid -> {
            Log.i(TAG, "Property added");
        })
                .addOnFailureListener(e ->
                        Log.i(TAG, "Property not added" + e.getMessage()));

        geoFire.setLocation(propertyKey, new GeoLocation(property.getLatitude(), property.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error == null) {
                    Log.i(TAG, "PropertyLocation saved");
                } else {
                    Log.i(TAG, error.getMessage());
                }
            }
        });
    }

    public MutableLiveData<List<Property>> selectAll() {
        MutableLiveData<List<Property>> userPropertiesMutableLiveData = new MutableLiveData<>();
        DatabaseReference allProperties = DB.child("properties/");
        allProperties.orderByChild("ownerUID").equalTo(ADB.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Property> properties = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Property userProperty = ds.getValue(Property.class);
                    properties.add(userProperty);
                }
                userPropertiesMutableLiveData.postValue(properties);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });

        return userPropertiesMutableLiveData;
    }

    public MutableLiveData<List<String>> selectAllInRange(Double lon, Double lat, Double range) {
        MutableLiveData<List<String>> propertiesInRangeMutableLiveData = new MutableLiveData<>();
        List<String> propertyKeys = new ArrayList<>();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), range);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.i(TAG, "Near property: " + key);
                propertyKeys.add(key);
                propertiesInRangeMutableLiveData.postValue(propertyKeys);
                //get key, run query for property, retrieve property, add to list, post.

                //TODO filter user_propertys
            }

            @Override
            public void onKeyExited(String key) {
                propertyKeys.remove(key);
                propertiesInRangeMutableLiveData.postValue(propertyKeys);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //Unused
            }

            @Override
            public void onGeoQueryReady() {
                propertiesInRangeMutableLiveData.postValue(propertyKeys);
                Log.i(TAG, "GeoQuery finished");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.i(TAG, error.getMessage());
            }
        });

        return propertiesInRangeMutableLiveData;
    }

    public MutableLiveData<List<Property>> selectProperty(List<String> keys) {
        DatabaseReference ref = DB.child("properties/");
        String currentUserUid = ADB.getCurrentUser().getUid();
        List<Property> properties = new ArrayList<>();
        MutableLiveData<List<Property>> propertiesInRange = new MutableLiveData<>();
        if (!keys.isEmpty()) {
            for (String key : keys) {
                ref.orderByChild("propertyUID").equalTo(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Property prop = ds.getValue(Property.class);
                            if (!prop.getOwnerUID().equals(currentUserUid)) {
                                properties.add(prop);
                            }

                        }
                        propertiesInRange.postValue(properties);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, databaseError.getMessage());
                    }
                });
            }
        } else {
            propertiesInRange.postValue(properties);
        }
        return propertiesInRange;
    }

    public void remove(Property property) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("properties/" + property.getPropertyUID(), null);
        requestMap.put("propertyLocations/" + property.getPropertyUID(), null);
        DB.updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, property.getEircode() + " removed");
                } else {
                    Log.i(TAG, "Failed");
                }
            }
        });

    }

    public MutableLiveData<Double> getAverage(double lon, double lat) {
        /*
            if range above 5km, trim to 4 positions and get larger area summary. Can create buckets of size 4 and 5 in cloud and aggregate them as well.
         */
        MutableLiveData<Double> averagePrice = new MutableLiveData<>(0.0);
        GeoHash geoHash = new GeoHash(lat, lon, 5);
        DB.child("geoPriceBucket").child(geoHash.getGeoHashString()).child("average").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double average = (double) dataSnapshot.getValue();
                    averagePrice.postValue(average);
                    Log.i(TAG, "retrieved average:" + average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "Error fetching average: " + databaseError.getMessage());
            }
        });
        return averagePrice;
    }
}
