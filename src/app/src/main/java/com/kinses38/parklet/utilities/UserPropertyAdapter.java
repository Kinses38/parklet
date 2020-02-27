package com.kinses38.parklet.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserPropertyAdapter extends RecyclerView.Adapter<UserPropertyAdapter.UserPropertyViewHolder> {

    private Activity context;
    private List<Property> properties;
    PropertyViewModel propertyViewModel;

    static class UserPropertyViewHolder extends RecyclerView.ViewHolder{
        TextView rv_house_address, rv_daily_rate, rv_available_booking, rv_weekend_booking;

        UserPropertyViewHolder(@NonNull View userPropertyView){
            super(userPropertyView);
            rv_house_address = userPropertyView.findViewById(R.id.rv_house_address);
            rv_daily_rate = userPropertyView.findViewById(R.id.rv_daily_rate);
            rv_available_booking = userPropertyView.findViewById(R.id.rv_available_booking);
            rv_weekend_booking = userPropertyView.findViewById(R.id.rv_weekend_booking);
        }
    }

    public UserPropertyAdapter(Activity context){
        this.context = context;
        this.properties = new ArrayList<>();
        propertyViewModel = new ViewModelProvider((FragmentActivity)context).get(PropertyViewModel.class);
    }

    @NonNull
    @Override
    public UserPropertyAdapter.UserPropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View root = LayoutInflater.from(context).inflate(R.layout.property_recycler_layout, parent,false);
        return new UserPropertyViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPropertyAdapter.UserPropertyViewHolder viewHolder, int position){
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
}
