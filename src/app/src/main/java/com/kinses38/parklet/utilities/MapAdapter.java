package com.kinses38.parklet.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.viewmodels.MapViewModel;

import java.util.ArrayList;
import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.MapViewHolder> {

    private Activity context;
    private List<Property> properties;
    MapViewModel mapViewModel;

    static class MapViewHolder extends RecyclerView.ViewHolder{
        TextView rv_house_address, rv_daily_rate, rv_available_booking, rv_weekend_booking;
        ImageButton selectProperty;

        MapViewHolder(@NonNull View mapView){
            super(mapView);
            rv_house_address = mapView.findViewById(R.id.rv_house_address);
            rv_daily_rate = mapView.findViewById(R.id.rv_daily_rate);
            rv_available_booking = mapView.findViewById(R.id.rv_available_booking);
            rv_weekend_booking = mapView.findViewById(R.id.rv_weekend_booking);
        }
    }

    public MapAdapter(Activity context){
        this.context = context;
        this.properties = new ArrayList<>();
        mapViewModel = new ViewModelProvider((FragmentActivity)context).get(MapViewModel.class);
    }

    @NonNull
    @Override
    public MapAdapter.MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View root = LayoutInflater.from(context).inflate(R.layout.map_property_recycler, parent,false);
        return new MapViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MapAdapter.MapViewHolder viewHolder, int position){
        Property property = properties.get(position);
        //TODO break addressline up into two for formatting.
        viewHolder.rv_house_address.setText(String.format("%s \n%s", context.getText(R.string.house_address), property.getAddressLine()));
        viewHolder.rv_daily_rate.setText(String.format("%s %.2f", context.getString(R.string.daily_rate), property.getDailyRate()));
        //TODO Parse to yes
        viewHolder.rv_available_booking.setText(String.format("%s %s", context.getString(R.string.available_to_book), property.getTakingBookings()));
        viewHolder.rv_weekend_booking.setText(String.format("%s %s", context.getString(R.string.available_weekends), property.getWeekendBookings()));
    }

    public void refreshList(List<Property> newProperties){
        this.properties = newProperties;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return properties.size();
    }

    public Property getCurrentItem(int position){
        return properties.get(position);
    }

    public int findItem(LatLng latLng){
        Property matchingProperty;
        for(Property p : properties){
            if(p.getLatLng().equals(latLng)){
                matchingProperty = p;
                return properties.indexOf(matchingProperty);
            }
        }
        return -1;
    }
}
