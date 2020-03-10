package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.repository.PropertyRepo;

import java.util.List;

public class MapViewModel extends ViewModel {

    private PropertyRepo propertyRepo = new PropertyRepo();

    public MapViewModel() {

    }

    //Call this when user submits, but should it call with defaults as well? Once user location permission issues are sorted?
    public LiveData<List<Property>> getPropertiesInRange(double lon, double lat, double range) {
        MutableLiveData<List<String>> propertyKeysLiveData = propertyRepo.selectAllInRange(lon, lat, range);

        LiveData<List<Property>> propertiesInRangeLiveData =
                Transformations.switchMap(propertyKeysLiveData, keys ->
                        propertyRepo.selectProperty(keys));
        return propertiesInRangeLiveData;
    }
}
