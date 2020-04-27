package com.kinses38.parklet.viewmodels;

import android.location.Address;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.repository.PropertyRepo;

import java.util.List;

public class PropertyViewModel extends ViewModel {

    private PropertyRepo propertyRepo;
    private MutableLiveData<Property> propertyToWriteMutableLiveData = new MutableLiveData<>();

    /**
     * Provided by ViewModel Factory
     *
     * @param propertyRepo singleton repo injected by Dagger.
     */
    PropertyViewModel(PropertyRepo propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    /**
     * Create property Object to pass to PropertyRepo to save.
     *
     * @param address           Full address object of property
     * @param dailyRate         Users set rate for renting for a day
     * @param availableWeekends boolean whether property is avail to rent on weekends
     */
    public LiveData<String> setProperty(Address address, Double dailyRate, String availableWeekends) {
        String addressLine = address.getAddressLine(0);
        String eircode = address.getPostalCode();
        Double longitude = address.getLongitude();
        Double latitude = address.getLatitude();
        Property property = new Property(addressLine, eircode, dailyRate, longitude, latitude);
        property.parseWeekend(availableWeekends);
        return addProperty(property);
    }

    /**
     * Fetch all properties belonging to current user
     *
     * @return livedata list of users properties.
     */
    public LiveData<List<Property>> getProperties() {
        return propertyRepo.selectAll();
    }

    @VisibleForTesting
    LiveData<String> addProperty(Property property) {
        return propertyRepo.create(property);
    }

    public void remove(Property property) {
        propertyRepo.remove(property);
    }

    /**
     * Set by Property RecyclerView, when user wishes to create a property NFC tag
     *
     * @param property the property in which the tag is to be written for.
     */
    public void setPropertyToWrite(Property property) {
        propertyToWriteMutableLiveData.postValue(property);
    }

    /**
     * Observed by main activity. Passes the property for which the NFC tag is to be written for.
     * Best practices of sharing a ViewModel between activity/fragment.
     *
     * @return the property to write a tag for.
     */
    public MutableLiveData<Property> getPropertyToWriteMutableLiveData() {
        return propertyToWriteMutableLiveData;
    }
}
