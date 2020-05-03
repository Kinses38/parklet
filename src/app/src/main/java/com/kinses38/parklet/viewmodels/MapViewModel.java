package com.kinses38.parklet.viewmodels;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.repository.PropertyRepo;

import java.util.List;

/**
 * Responsible for populating the MapView with properties given the users query of area and range.
 */
public class MapViewModel extends ViewModel {

    private PropertyRepo propertyRepo;
    private MutableLiveData<Double> averagePrice = new MutableLiveData<>(0.0);

    /**
     * Provided by ViewModel Factory, PropertyRepo injected by Dagger
     *
     * @param propertyRepo singleton property Repository
     */
    MapViewModel(PropertyRepo propertyRepo) {
        this.propertyRepo = propertyRepo;
    }

    /**
     * Pass users query to Property Repository.
     * Query first for the keys of properties that fall in the range of the query.
     * Then query for the resulting properties.
     * Query corresponding geoHashBucket for the given areas average price.
     */
    public LiveData<List<Property>> queryPropertiesInRange(double lon, double lat, double range) {
        //Get all the property keys within the range.
        MutableLiveData<List<String>> propertyKeysLiveData = propertyRepo
                .selectAllInRange(lon, lat, range);
        //Fetch precomputed average price from GeoBucket
        averagePrice = getPricingForArea(lon, lat, range);
        //For the keys in range that are returned, get the properties.
        LiveData<List<Property>> propertiesInRangeLiveData = getPropertiesInRange(propertyKeysLiveData);

        /*
            Mediator combined result watches both propertiesInRange and averagePrice livedata
            for updates then updates each property with an average price comparison.
         */
        MediatorLiveData<List<Property>> combinedResult = new MediatorLiveData();
        combinedResult.addSource(averagePrice, price
                -> combinedResult.postValue(updateProperties(propertiesInRangeLiveData.getValue(), price)));
        combinedResult.addSource(propertiesInRangeLiveData, properties
                -> combinedResult.postValue(updateProperties(properties, averagePrice.getValue())));
        return combinedResult;
    }

    /**
     * Takes livedata of keys in range corresponding to propertyIDs and returns livedata list of properties.
     *
     * @param propertyKeys the ids of the properties in range
     * @return livedata list of properties that match the users query.
     */
    @VisibleForTesting
    LiveData<List<Property>> getPropertiesInRange(LiveData<List<String>> propertyKeys) {
        LiveData propertiesInRange = Transformations
                .switchMap(propertyKeys, keys -> propertyRepo.selectProperty(keys));
        return propertiesInRange;
    }

    /**
     * Maps the users range query to an approximation of area using GeoHashBuckets that
     * hold a precomputed average price of an area.
     *
     * @param lon   longitude of search area
     * @param lat   latitude of search area
     * @param range users given range of interest.
     * @return the average price from the GeoHashBucket.
     */
    @VisibleForTesting
    MutableLiveData<Double> getPricingForArea(double lon, double lat, double range) {
        //LiveData will post incorrectly for tests unless explicitly assigned here.
        MutableLiveData<Double> average;
        //TODO constants
        if (range <= 2) {
            average = propertyRepo.getAverage(lon, lat, 6);
            return average;
        } else if (range > 2 && range < 4) {
            average = propertyRepo.getAverage(lon, lat, 5);
            return average;
        } else {
            average = propertyRepo.getAverage(lon, lat, 4);
            return average;
        }
    }

    /**
     * Updates the properties average price comparison relative to the current area of search
     *
     * @param properties properties list to be updated
     * @param average            given average price for current area
     * @return properties with average price comparison set.
     */
    @VisibleForTesting
    List<Property> updateProperties(List<Property> properties, double average) {
        if (properties != null && !properties.isEmpty()) {
            for (Property property : properties) {
                property.setAverageComparison(average);
            }
        }
        return properties;
    }


}