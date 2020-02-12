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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleRepo {
    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid() ;

    public void createNewVehicle(Vehicle vehicle){

        //Obtain uid vehicleKey for item before pushing
        //https://firebase.google.com/docs/database/android/read-and-write#save_data_as_transactions
        String vehicleKey = DB.child("vehicles").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("vehicles/"+vehicleKey, vehicle);
        childUpdates.put("users/"+userUid+"/vehicles/"+vehicleKey, vehicle.getReg());

        DB.updateChildren(childUpdates)
                .addOnSuccessListener(aVoid -> {
                    DB.child("users/"+userUid+"/vehicles/");
                    Log.i("VehicleRepo","New vehicle added successfully " + vehicle.getReg());
        }).addOnFailureListener(e ->{
                    Log.i("VehicleRepo", "Add new vehicle failed");
        });

    }

    public MutableLiveData<List<Vehicle>> getVehicles(){
        MutableLiveData<List<Vehicle>> userVehiclesMutableLiveData = new MutableLiveData<>();
        ArrayList<Vehicle> userVehicleList = new ArrayList<Vehicle>();
        DatabaseReference userVehiclesRef = DB.child("users/"+userUid+"/vehicles/");
        userVehiclesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String vehicleKey = ds.getKey();
                    DatabaseReference vehicleInfo = DB.child("vehicles/"+vehicleKey);
                    vehicleInfo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Vehicle userVehicle = dataSnapshot.getValue(Vehicle.class);
                            userVehicleList.add(userVehicle);
                            userVehiclesMutableLiveData.setValue(userVehicleList);
                            Log.i("VehicleRepo", userVehicle.getMake());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("VehicleRepo", databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("VehicleRepo", databaseError.getMessage());
            }
        });
        return userVehiclesMutableLiveData;
    }
}
