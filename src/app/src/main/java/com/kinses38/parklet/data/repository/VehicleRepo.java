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
    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void create(Vehicle vehicle) {
        String vehicleKey = DB.child("vehicles" + userUid).push().getKey();
        DatabaseReference vehicleRef = DB.child("vehicles/" + userUid + "/" + vehicleKey);
        vehicleRef.setValue(vehicle)
                .addOnSuccessListener(aVoid -> {
                    Log.i("VehicleRepo", "Add worked");
                })
                .addOnFailureListener(e ->
                        Log.i("VehicleRepo", "add failed"));
    }


    public MutableLiveData<List<Vehicle>> selectAll() {
        MutableLiveData<List<Vehicle>> userVehiclesMutableLiveData = new MutableLiveData<>();
        DatabaseReference allVehicles = DB.child("vehicles/"+userUid);
        allVehicles.addValueEventListener(new ValueEventListener() {
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
}
