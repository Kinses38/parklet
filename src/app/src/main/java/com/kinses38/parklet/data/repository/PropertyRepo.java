package com.kinses38.parklet.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.core.GeoHash;
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

/**
 * Repository responsible for:
 * Adding, removing and querying users properties.
 * Querying properties falling within a geohash range for users searching for property to rent
 * Querying the average property price of an area.
 */
public class PropertyRepo {

    private final String TAG = this.getClass().getSimpleName();
    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth ADB = FirebaseAuth.getInstance();
    private DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference("propertyLocations");
    private GeoFire geoFire = new GeoFire(geoFireRef);


    /**
     * Creates new property entry belong to current user under "properties" node
     * and queryable geohash for that properties location under "propertyLocations" node
     *
     * @param property the property to add.
     */
    public MutableLiveData<String> create(Property property) {
        //Property EirCode used as natural key
        DatabaseReference propertyRef = DB.child("properties/" + property.getEircode());
        MutableLiveData<String> result = new MutableLiveData<>();
        property.setOwnerUID(ADB.getCurrentUser().getUid());
        property.setOwnerName(ADB.getCurrentUser().getDisplayName());
        propertyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "Cant add property, exists");
                    result.postValue("Property already exists!");

                } else {
                    propertyRef.setValue(property)
                            .addOnSuccessListener(aVoid -> {
                                result.postValue("Property added!");
                                Log.i(TAG, "Property added");
                            })
                            .addOnFailureListener(e ->
                                    Log.i(TAG, "Property not added" + e.getMessage()));

                    /* creates queryable geohash that allows us to search by
                    area and range. Maps to property ID under "properties" node */
                    geoFire.setLocation(property.getEircode(),
                            new GeoLocation(property.getLatitude(), property.getLongitude()),
                            (key, error) -> {
                                if (error == null) {
                                    Log.i(TAG, "PropertyLocation saved: " + key);
                                } else {
                                    Log.i(TAG, error.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
            }
        });
        return result;
    }

    /**
     * Query for all properties belonging to current user
     *
     * @return livedata list of properties
     */
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

    /**
     * Query for propertyid's that fall within geohash range of users search.
     *
     * @param lon   double longitude of area of interest
     * @param lat   double latitude of area of interest
     * @param range double range in kms in which the user is interested in
     * @return livedata list of all property ids that fall within this geohash range.
     */
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

    /**
     * Returns all properties matching the list of property id keys
     *
     * @param keys the ids of properties to fetch
     * @return livedata list of properties.
     */
    public MutableLiveData<List<Property>> selectProperty(List<String> keys) {
        DatabaseReference ref = DB.child("properties/");
        String currentUserUid = ADB.getCurrentUser().getUid();
        List<Property> properties = new ArrayList<>();
        MutableLiveData<List<Property>> propertiesInRange = new MutableLiveData<>();
        if (!keys.isEmpty()) {
            for (String key : keys) {
                ref.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Property prop = dataSnapshot.getValue(Property.class);
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

    /**
     * Atomically deletes provided property from both "properties" node and "propertyLocations"
     * by setting both child nodes to null.
     * Both must succeed or neither will be removed. Prevents dangling properties/locations
     *
     * @param property the property to remove.
     */
    public void remove(Property property) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("properties/" + property.getEircode(), null);
        requestMap.put("propertyLocations/" + property.getEircode(), null);
        DB.updateChildren(requestMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, property.getEircode() + " removed");
            } else {
                Log.i(TAG, "Failed");
            }
        });

    }

    /**
     * Queries the "GeoPriceBucket" node for a precomputed average for the area. The level of precision is
     * set by mapping the range of the users query to the appropriate geohash length for an approximation of
     * their area of interest
     *
     * @param lon       double longitude of area of interest
     * @param lat       double latitude of area of interest
     * @param precision the number of bits to be used for matching the geohash. The longer the geohash the more specific the area
     * @return the average price for that area
     */
    public MutableLiveData<Double> getAverage(double lon, double lat, int precision) {
        MutableLiveData<Double> averagePrice = new MutableLiveData<>(0.0);
        GeoHash geoHash = new GeoHash(lat, lon, precision);
        DB.child("geoPriceBucket").child(geoHash.getGeoHashString()).child("average").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Null exception is protected by snapshot.exists() IDE error
                    @SuppressWarnings("ConstantConditions") double average = dataSnapshot.getValue(Double.class);
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
