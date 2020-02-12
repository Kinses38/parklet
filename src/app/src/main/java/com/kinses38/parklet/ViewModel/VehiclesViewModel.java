package com.kinses38.parklet.ViewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.VehicleRepo;

import java.util.List;

public class VehiclesViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private VehicleRepo vehicleRepo = new VehicleRepo();

    public VehiclesViewModel(Application application) {
        super(application);
    }


    public LiveData<String> getText() {
        return mText;
    }

    public void createNewVehicle(Vehicle vehicle){
        vehicleRepo.createNewVehicle(vehicle);
    }

    public LiveData<List<Vehicle>> getVehicles(){
        return vehicleRepo.getVehicles();
    }
}