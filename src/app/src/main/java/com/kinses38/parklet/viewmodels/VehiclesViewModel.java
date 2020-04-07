package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.VehicleRepo;

import java.util.List;

public class VehiclesViewModel extends ViewModel {

    private Vehicle vehicle;
    private VehicleRepo vehicleRepo;

    public VehiclesViewModel(VehicleRepo vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
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