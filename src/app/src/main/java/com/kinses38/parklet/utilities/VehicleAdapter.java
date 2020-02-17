package com.kinses38.parklet.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Activity context;
    private List<Vehicle> vehicles;

    static class VehicleViewHolder extends RecyclerView.ViewHolder{
      TextView rv_car_reg, rv_car_make, rv_car_model;

      VehicleViewHolder(@NonNull View vehicleView){
          super(vehicleView);
          rv_car_reg = vehicleView.findViewById(R.id.rv_car_reg);
          rv_car_make = vehicleView.findViewById(R.id.rv_car_make);
          rv_car_model = vehicleView.findViewById(R.id.rv_car_model);
      }

    }

    public VehicleAdapter(Activity context){
        this.context = context;
        this.vehicles = new ArrayList<>();
    }

    @NonNull
    @Override
    public VehicleAdapter.VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.recycler_layout, parent, false);
        return new VehicleViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.VehicleViewHolder viewHolder, int position) {
        Vehicle vehicle = vehicles.get(position);
        viewHolder.rv_car_reg.setText(String.format("%s %s", context.getString(R.string.car_reg), vehicle.getReg()));
        viewHolder.rv_car_make.setText(String.format("%s %s", context.getString(R.string.car_make), vehicle.getMake()));
        viewHolder.rv_car_model.setText(String.format("%s %s", context.getString(R.string.car_model), vehicle.getModel()));


    }

    public void refreshList(List<Vehicle> newVehicles){
//      this.vehicles.clear();
        this.vehicles = newVehicles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }
}
