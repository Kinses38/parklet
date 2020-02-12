package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.R;
import com.kinses38.parklet.ViewModel.VehiclesViewModel;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.databinding.FragmentVehiclesBinding;

import java.util.List;

public class VehiclesFragment extends Fragment {

    private VehiclesViewModel vehiclesViewModel;
    private FragmentVehiclesBinding vehiclesLandingBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vehiclesViewModel = new ViewModelProvider(this).get(VehiclesViewModel.class);

        vehiclesLandingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicles, container, false);

//        vehiclesViewModel.createNewVehicle(new Vehicle("BMW", "430","11-D-1234"));

        initVehicleObserver();
        //TODO add vehicle with "You have not added a vehicle yet"

        //TODO show current vehicles with option to edit or delete. Listview style collapsible?

        return vehiclesLandingBinding.getRoot();
    }

    private void initVehicleObserver() {
        vehiclesViewModel.getVehicles().observe(getViewLifecycleOwner(), new Observer<List<Vehicle>>() {
            @Override
            public void onChanged(List<Vehicle> vehicles) {
                for(Vehicle v: vehicles){
                    vehiclesLandingBinding.setVehicle(v);
                    Log.i("FragVeh", v.getModel());
                }
            }
        });
    }
}