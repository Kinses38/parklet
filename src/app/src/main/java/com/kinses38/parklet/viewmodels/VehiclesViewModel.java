package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.VehicleRepo;

import java.util.List;

public class VehiclesViewModel extends ViewModel {

    private VehicleRepo vehicleRepo;

    /**
     * Provided by ViewModel Factory
     *
     * @param vehicleRepo singleton Repo injected by Dagger
     */
    VehiclesViewModel(VehicleRepo vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }


    /**
     * Create vehicle object to pass to vehicle repo to save
     *
     * @param make  String Brand of car
     * @param model String model of car
     * @param reg   String registration of car
     */
    public void submitVehicle(String make, String model, String reg) {
        Vehicle vehicle = new Vehicle(make, model, reg);
        createNewVehicle(vehicle);
    }

    private void createNewVehicle(Vehicle vehicle) {
        vehicleRepo.create(vehicle);
    }

    /**
     * Get all current user's vehicles
     *
     * @return livedata list of vehicles.
     */
    public LiveData<List<Vehicle>> getVehicles() {
        return vehicleRepo.selectAll();
    }

    public void remove(Vehicle vehicle) {
        vehicleRepo.remove(vehicle);
    }
}