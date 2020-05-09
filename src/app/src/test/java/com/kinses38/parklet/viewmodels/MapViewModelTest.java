package com.kinses38.parklet.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.repository.PropertyRepo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.isEqual;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private PropertyRepo propertyRepo = Mockito.mock(PropertyRepo.class);


    @Mock
    Observer<List<String>> keysObserver;
    @Mock
    Observer<List<Property>> propertyObserver;
    @Mock
    Observer<Double> averageObserver;

    private MapViewModel mapViewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mapViewModel = new MapViewModel(propertyRepo);
    }

    @Test
    public void getPropertiesInRangeTest() {
        List<String> keys = new ArrayList<>();
        keys.add("1234");
        List<Property> properties = new ArrayList<>();
        Property property = new Property();
        properties.add(property);
        MutableLiveData<List<String>> keysInRange = new MutableLiveData<>(keys);
        MutableLiveData<List<Property>> propertiesInRange = new MutableLiveData<>(properties);
        when(propertyRepo.selectProperty(keys)).thenReturn(propertiesInRange);
        mapViewModel.getPropertiesInRange(keysInRange).observeForever(propertyObserver);
        verify(propertyObserver, description("Property did not match")).onChanged(properties);
        properties.remove(0);
        verify(propertyObserver, description("Property not removed")).onChanged(properties);

    }

    @Test
    public void getPricingForAreaTest() {
        MutableLiveData<Double> streetAverage = new MutableLiveData<>(10.0);
        MutableLiveData<Double> estateAverage = new MutableLiveData<>(5.0);
        MutableLiveData<Double> townAverage = new MutableLiveData<>(15.0);
        double lon = 1.0;
        double lat = 2.0;
        double streetRange = 1.0;
        double estateRange = 3.0;
        double townRange = 6.0;
        //precision geohash of  6
        when(propertyRepo.getAverage(lon, lat, 6)).thenReturn(streetAverage);
        mapViewModel.getPricingForArea(lon, lat, streetRange).observeForever(averageObserver);
        verify(averageObserver).onChanged(10.00);

        //precision geohash of 5`
        when(propertyRepo.getAverage(lon, lat, 5)).thenReturn(estateAverage);
        mapViewModel.getPricingForArea(lon, lat, estateRange).observeForever(averageObserver);
        verify(averageObserver).onChanged(5.00);

        //precision geohash of 4
        when(propertyRepo.getAverage(lon, lat, 4)).thenReturn(townAverage);
        mapViewModel.getPricingForArea(lon, lat, townRange).observeForever(averageObserver);
        verify(averageObserver).onChanged(15.00);
    }

    @Test
    public void updatePropertiesTest(){
        List<Property> properties = new ArrayList<>();
        Property property = new Property();
        property.setDailyRate(5.00);
        properties.add(property);
        MutableLiveData<List<Property>> propertyMutableLiveData = new MutableLiveData<>(properties);
        double average = 10.00;

        assertEquals("Properties in = Properties out", mapViewModel.updateProperties(propertyMutableLiveData.getValue(), average), properties);
        assertEquals("Properties average comparison correct",properties.get(0).getAverageComparison(), 50.00);
        properties.get(0).setDailyRate(10.00);
        assertEquals("Properties in = Properties out", mapViewModel.updateProperties(propertyMutableLiveData.getValue(), average), properties);
        assertEquals("Properties average comparison correct",properties.get(0).getAverageComparison(), 0.00);

    }

    @Test
    public void queryPropertiesInRangeTest(){
        double expectedPriceComparison = 50.00;
        List<String> keys = new ArrayList<>();
        keys.add("1234");
        List<Property> properties = new ArrayList<>();
        Property property = new Property();
        property.setDailyRate(5.00);
        properties.add(property);

        MutableLiveData<Double> averagePrice = new MutableLiveData<>(10.00);
        MutableLiveData<List<String>> propertyKeys = new MutableLiveData<>(keys);
        MutableLiveData<List<Property>> propertiesInRange = new MutableLiveData<>(properties);

        when(propertyRepo.getAverage(10.0, 5.0, 6)).thenReturn(averagePrice);
        when(propertyRepo.selectAllInRange(10.0, 5.0, 1.0)).thenReturn(propertyKeys);
        when(propertyRepo.selectProperty(keys)).thenReturn(propertiesInRange);

        mapViewModel.queryPropertiesInRange(10.0, 5.0, 1.0).observeForever(propertyObserver);
        verify(propertyObserver).onChanged(properties);

        assertEquals(properties.get(0).getAverageComparison(), expectedPriceComparison);
    }
}
