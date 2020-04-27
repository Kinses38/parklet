package com.kinses38.parklet.viewmodels;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    public LiveData<String> submitVehicle(String make, String model, String reg) {
        MutableLiveData<String> result = new MutableLiveData<>();
        String errors;
        if ((errors = validateVehicle(make, model, reg)).isEmpty()) {
            Vehicle vehicle = new Vehicle(make, model, reg);
            result = createNewVehicle(vehicle);
        } else {

            result.postValue(errors);
        }
        return result;
    }

    /**
     * Ensure all fields are filled. Validation could be performed on car reg using regex but
     * what of English/NI regs?
     *
     * @param make  car make
     * @param model car model
     * @param reg   car reg
     * @return String errors
     */
    @VisibleForTesting
    String validateVehicle(String make, String model, String reg) {
        StringBuilder errorBuilder = new StringBuilder("");
        if (make.length() == 0)
            errorBuilder.append("Make of vehicle required\n");
        if (model.length() == 0)
            errorBuilder.append("Model of vehicle required\n");
        if (reg.length() == 0) {
            errorBuilder.append("Registration number required\n");
        }
        return errorBuilder.toString();
    }

    @VisibleForTesting
    MutableLiveData<String> createNewVehicle(Vehicle vehicle) {
        return vehicleRepo.create(vehicle);
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