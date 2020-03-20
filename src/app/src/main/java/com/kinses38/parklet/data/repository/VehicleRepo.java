package com.kinses38.parklet.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kinses38.parklet.data.model.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleRepo {
    
    private final String TAG = this.getClass().getSimpleName();
    
    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth ADB = FirebaseAuth.getInstance();
    
    
    
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


    public MutableLiveData<List<Vehicle>> selectAll() {
        MutableLiveData<List<Vehicle>> userVehiclesMutableLiveData = new MutableLiveData<>();
        DatabaseReference allVehicles = DB.child("vehicles/");
        allVehicles.orderByChild("ownerUID").equalTo(ADB.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Vehicle> vehicles = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Vehicle userVehicle = ds.getValue(Vehicle.class);
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

    public void remove(Vehicle vehicle){
        DatabaseReference userVehicles = DB.child("vehicles/");
        userVehicles.orderByChild("reg").equalTo(vehicle.getReg()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                     ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.i(TAG, String.format("Vehicle %s deleted", vehicle.getReg()));
    }

}
