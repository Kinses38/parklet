package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.Nullable;
import com.kinses38.parklet.ParkLet;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.databinding.FragmentVehiclesBinding;
import com.kinses38.parklet.utilities.InputHandler;
import com.kinses38.parklet.utilities.VehicleAdapter;
import com.kinses38.parklet.viewmodels.VehiclesViewModel;
import com.kinses38.parklet.viewmodels.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class VehiclesFragment extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private FragmentVehiclesBinding vehiclesLandingBinding;

    private RecyclerView recyclerView;
    private VehicleAdapter adapter;
    private TextView carMake, carModel, carReg;

    private VehiclesViewModel vehiclesViewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vehiclesLandingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicles, container, false);
        ParkLet.getParkLetApp().getVehicleRepoComponent().inject(this);
        vehiclesViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(VehiclesViewModel.class);
        initRecyclerView();
        initBindings();
        initVehicleObserver();

        return vehiclesLandingBinding.getRoot();
    }

    /**
     * Binds:
     * This fragment instance to the vehicle layout Fragment
     * Boolean to form whether to display or not
     * TextViews for vehicle info.
     */
    private void initBindings() {
        //Bind this fragment to allow onClick binding
        vehiclesLandingBinding.setVehicleFrag(this);
        //Binding boolean value to hide form
        vehiclesLandingBinding.setFormClicked(false);
        //form data
        carMake = vehiclesLandingBinding.carMake;
        carModel = vehiclesLandingBinding.carModel;
        carReg = vehiclesLandingBinding.carReg;

    }

    /**
     * Recyclerview to display users vehicles and allow deletion.
     */
    private void initRecyclerView() {
        adapter = new VehicleAdapter(getActivity());
        recyclerView = vehiclesLandingBinding.getRoot().findViewById(R.id.vehicle_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

    }

    /**
     * Call to Vehicle ViewModel to retrieve users vehicles if they exist.
     * Displays recyclerview if they do, otherwise informs user to add a vehicle
     */
    private void initVehicleObserver() {
        vehiclesViewModel.getVehicles().observe(getViewLifecycleOwner(), new Observer<List<Vehicle>>() {
            @Override
            public void onChanged(@Nullable List<Vehicle> vehicles) {
                if (!vehicles.isEmpty()) {
                    vehiclesLandingBinding.setHasVehicle(true);
                    adapter.refreshList(vehicles);
                    recyclerView.setAdapter(adapter);
                    Log.i(TAG, String.valueOf(this.hashCode()));
                } else {
                    vehiclesLandingBinding.setHasVehicle(false);
                }
            }
        });
    }

    /**
     * Retrieve vehicle details from form and then pass to Vehicle ViewModel.
     */
    private void saveVehicle() {
        String make = carMake.getText().toString();
        String model = carModel.getText().toString();
        String reg = carReg.getText().toString();
        vehiclesViewModel.submitVehicle(make, model, reg).observe(getViewLifecycleOwner(),
                result -> Toast.makeText(requireActivity(), result, Toast.LENGTH_LONG).show());
    }

    /**
     * Reset form after user saves vehicle or cancels.
     */
    private void resetTextViews() {
        carMake.setText("");
        carModel.setText("");
        carReg.setText("");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_form_toggle_button:
                vehiclesLandingBinding.setFormClicked(!vehiclesLandingBinding.getFormClicked());
                Log.i(TAG, "toggle form");
                break;
            case R.id.vehicle_form_save_button:
                saveVehicle();
                InputHandler.hideKeyboard(requireActivity());
                resetTextViews();
                vehiclesLandingBinding.setFormClicked(!vehiclesLandingBinding.getFormClicked());
                Log.i(TAG, "save");
                break;
            case R.id.vehicle_form_cancel_button:
                InputHandler.hideKeyboard(requireActivity());
                resetTextViews();
                vehiclesLandingBinding.setFormClicked(!vehiclesLandingBinding.getFormClicked());
                Log.i(TAG, "cancel");
                break;
            default:
                break;
        }
    }
}