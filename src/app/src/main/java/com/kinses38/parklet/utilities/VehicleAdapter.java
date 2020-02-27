package com.kinses38.parklet.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.viewmodels.VehiclesViewModel;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Activity context;
    private List<Vehicle> vehicles;
    VehiclesViewModel vehiclesViewModel;

    static class VehicleViewHolder extends RecyclerView.ViewHolder{
      TextView rv_car_reg, rv_car_make, rv_car_model;
      ImageButton rv_delete;

      VehicleViewHolder(@NonNull View vehicleView){
          super(vehicleView);
          rv_car_reg = vehicleView.findViewById(R.id.rv_car_reg);
          rv_car_make = vehicleView.findViewById(R.id.rv_car_make);
          rv_car_model = vehicleView.findViewById(R.id.rv_car_model);
          rv_delete = vehicleView.findViewById(R.id.rv_car_delete);
      }

    }

    public VehicleAdapter(Activity context){
        this.context = context;
        this.vehicles = new ArrayList<>();
        vehiclesViewModel= new ViewModelProvider((FragmentActivity) context).get(VehiclesViewModel.class);
    }

    @NonNull
    @Override
    public VehicleAdapter.VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.vehicle_recycler_layout, parent, false);
        return new VehicleViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.VehicleViewHolder viewHolder, int position) {
        Vehicle vehicle = vehicles.get(position);
        viewHolder.rv_car_reg.setText(String.format("%s %s", context.getString(R.string.car_reg), vehicle.getReg()));
        viewHolder.rv_car_make.setText(String.format("%s %s", context.getString(R.string.car_make), vehicle.getMake()));
        viewHolder.rv_car_model.setText(String.format("%s %s", context.getString(R.string.car_model), vehicle.getModel()));
        viewHolder.rv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vehicle deletedVehicle = vehicles.get(position);
                vehiclesViewModel.remove(deletedVehicle);
                vehicles.remove(position);
                Toast.makeText((FragmentActivity)context, String.format("Vehicle: %s has been removed", vehicle.getReg()),Toast.LENGTH_LONG).show();
                notifyDataSetChanged();
            }
        });


    }

    public void refreshList(List<Vehicle> newVehicles){
        this.vehicles = newVehicles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }
}
