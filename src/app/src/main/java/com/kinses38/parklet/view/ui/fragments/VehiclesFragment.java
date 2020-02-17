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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.Nullable;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.databinding.FragmentVehiclesBinding;
import com.kinses38.parklet.utilities.InputHandler;
import com.kinses38.parklet.utilities.VehicleAdapter;
import com.kinses38.parklet.viewmodels.VehiclesViewModel;

import java.util.List;

public class VehiclesFragment extends Fragment implements View.OnClickListener {

    private VehiclesViewModel vehiclesViewModel;
    private FragmentVehiclesBinding vehiclesLandingBinding;

    private RecyclerView recyclerView;
    private VehicleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vehiclesLandingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicles, container, false);
        vehiclesViewModel = new ViewModelProvider(getActivity()).get(VehiclesViewModel.class);

        initRecyclerView();
        initVehicleObserver();
        //Bind this fragment to allow onClick binding
        vehiclesLandingBinding.setVehicleFrag(this);
        //Binding boolean value to hide form
        vehiclesLandingBinding.setFormClicked(false);
        vehiclesLandingBinding.setVehicleViewModel(vehiclesViewModel);

        //TODO duplicate bug is caused by loop in vehicle repo. Redo DB schema
        return vehiclesLandingBinding.getRoot();
    }

    private void initVehicleObserver() {
        vehiclesViewModel.getVehicles().observe(getViewLifecycleOwner(), new Observer<List<Vehicle>>() {
            @Override
            public void onChanged(@Nullable List<Vehicle> vehicles) {
                if (!vehicles.isEmpty()) {
                    vehiclesLandingBinding.setHasVehicle(true);
                    adapter.refreshList(vehicles);
                    recyclerView.setAdapter(adapter);
                    Log.i("Livedata: ", String.valueOf(this.hashCode()));
                }
                else{
                    vehiclesLandingBinding.setHasVehicle(false);
                }
            }
        });
    }

    private void initRecyclerView(){
        adapter = new VehicleAdapter(getActivity());
        recyclerView = vehiclesLandingBinding.getRoot().findViewById(R.id.vehicle_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_form_toggle_button:
                vehiclesLandingBinding.setFormClicked(!vehiclesLandingBinding.getFormClicked());
                Log.i("1st button", "toggle form");
                break;
            case R.id.vehicle_form_cancel_button:
                Log.i("3rd button", "cancel");
                InputHandler.hideKeyboard(getActivity());
                vehiclesLandingBinding.setFormClicked(!vehiclesLandingBinding.getFormClicked());
            default:
                break;
        }
    }
}