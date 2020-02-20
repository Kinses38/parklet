package com.kinses38.parklet.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;

import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.VehicleRepo;

import java.util.List;

public class VehiclesViewModel extends AndroidViewModel {

    private Vehicle vehicle;
    private VehicleRepo vehicleRepo = new VehicleRepo();

    public VehiclesViewModel(Application application) {
        super(application);
    }

    public void onClickVehicleSubmit(String make, String model, String reg){
        vehicle = new Vehicle(make, model, reg);
        createNewVehicle(vehicle);
    }

    public void createNewVehicle(Vehicle vehicle){
        vehicleRepo.create(vehicle);
    }

    public LiveData<List<Vehicle>> getVehicles(){
        return vehicleRepo.selectAll();
    }

    public void remove(Vehicle vehicle){
        vehicleRepo.remove(vehicle);
    }
}