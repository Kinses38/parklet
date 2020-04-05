package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.repository.PropertyRepo;

import java.util.List;

public class MapViewModel extends ViewModel {

    private PropertyRepo propertyRepo = new PropertyRepo();
    private MutableLiveData<Double> averagePrice = new MutableLiveData<>(0.0);


    public MapViewModel() {

    }

    public LiveData<List<Property>> queryPropertiesInRange(double lon, double lat, double range) {
        MutableLiveData<List<String>> propertyKeysLiveData = propertyRepo
                .selectAllInRange(lon, lat, range);
        averagePrice = getPricingForArea(lon, lat, range);
        LiveData<List<Property>> propertiesInRangeLiveData = getPropertiesInRange(propertyKeysLiveData);

        MediatorLiveData<List<Property>> combinedResult = new MediatorLiveData();
        combinedResult.addSource(averagePrice, price
                -> combinedResult.setValue(updateProperties(propertiesInRangeLiveData, averagePrice.getValue())));
        combinedResult.addSource(propertiesInRangeLiveData, properties
                -> combinedResult.postValue(updateProperties(propertiesInRangeLiveData, averagePrice.getValue())));
        return combinedResult;
    }

    private LiveData<List<Property>> getPropertiesInRange(LiveData<List<String>> propertyKeys) {
        LiveData propertiesInRange = Transformations
                .switchMap(propertyKeys, keys -> propertyRepo.selectProperty(keys));
        return propertiesInRange;
    }

    private MutableLiveData<Double> getPricingForArea(double lon, double lat, double range) {
        //TODO constants
        if(range <= 2){
            return propertyRepo.getAverage(lon, lat, 6);
        }else if (range > 2 && range < 4){
            return  propertyRepo.getAverage(lon, lat, 5);
        }else {
            return propertyRepo.getAverage(lon, lat, 4);
        }
    }

    private List<Property> updateProperties(LiveData<List<Property>> propertiesLiveData, double average) {
        List<Property> properties = propertiesLiveData.getValue();
        if (properties != null && !properties.isEmpty()) {
            for (Property property : properties) {
                property.setAverageComparison(average);
            }
        }
        return properties;
    }


}