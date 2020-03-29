package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.repository.PropertyRepo;

import java.util.List;
import java.util.stream.Collectors;

public class MapViewModel extends ViewModel {

    private PropertyRepo propertyRepo = new PropertyRepo();

    public MapViewModel() {

    }

    public LiveData<List<Property>> getPropertiesInRange(double lon, double lat, double range) {
        MutableLiveData<List<String>> propertyKeysLiveData = propertyRepo
                .selectAllInRange(lon, lat, range);

        LiveData<List<Property>> propertiesInRangeLiveData = Transformations
                .switchMap(propertyKeysLiveData, keys -> propertyRepo.selectProperty(keys));

        return propertiesInRangeLiveData;
    }


}
