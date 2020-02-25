package com.kinses38.parklet.viewmodels;

import android.location.Address;

import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.repository.PropertyRepo;

public class PropertyViewModel extends ViewModel {

    private Property property;
    private PropertyRepo propertyRepo = new PropertyRepo();

    public PropertyViewModel() {
    }

    //todo save address in more human readable/ui friendly form.
    public void setProperty(Address address, Double dailyRate, String availableWeekends){
        String addressLine = address.getAddressLine(0);
        String eircode = address.getPostalCode();
        Double longitude = address.getLongitude();
        Double latitude = address.getLatitude();
        property = new Property(addressLine, eircode, dailyRate, longitude, latitude);
        addProperty(property);
    }

    private void addProperty(Property property){
        propertyRepo.create(property);
    }

}
