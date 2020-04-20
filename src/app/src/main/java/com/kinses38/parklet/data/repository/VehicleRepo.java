package com.kinses38.parklet.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.model.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for:
 * Adding, removing or querying a users vehicles.
 */
public class VehicleRepo {

    private final String TAG = this.getClass().getSimpleName();

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth ADB = FirebaseAuth.getInstance();

    /**
     * Add users vehicle to the "Vehicles" node
     *
     * @param vehicle vehicle object to be added
     */
    public void create(Vehicle vehicle) {
        String vehicleKey = DB.child("vehicles").push().getKey();
        DatabaseReference vehicleRef = DB.child("vehicles/" + vehicleKey);
        vehicle.setOwnerUID(ADB.getCurrentUser().getUid());
        vehicleRef.setValue(vehicle)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Add worked");
                })
                .addOnFailureListener(e ->
                        Log.i(TAG, "add failed"));
    }

    /**
     * Return all of the current users vehicles
     *
     * @return livedata list of vehicles
     */
    public MutableLiveData<List<Vehicle>> selectAll() {
        MutableLiveData<List<Vehicle>> userVehiclesMutableLiveData = new MutableLiveData<>();
        DatabaseReference allVehicles = DB.child("vehicles/");
        allVehicles.orderByChild("ownerUID").equalTo(ADB.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Vehicle> vehicles = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Vehicle userVehicle = ds.getValue(Vehicle.class);
                    userVehicle.setVehicleUID(ds.getKey());
                    vehicles.add(userVehicle);
                }
                userVehiclesMutableLiveData.postValue(vehicles);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return userVehiclesMutableLiveData;
    }


    /**
     * Removes users selected vehicle
     *
     * @param vehicle vehicle to be removed, matched by registration.
     */
    public void remove(Vehicle vehicle) {
        DatabaseReference userVehicles = DB.child("vehicles/");
        userVehicles.child(vehicle.getVehicleUID()).setValue(null)
                .addOnSuccessListener(aVoid -> Log.i(TAG, "Vehicle Deleted"))
                .addOnFailureListener(e -> Log.i(TAG, e.getMessage()));
    }

}
