package com.kinses38.parklet.viewmodels;

import android.location.Address;

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

import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class PropertyViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private PropertyRepo propertyRepo = Mockito.mock(PropertyRepo.class);

    @Mock
    Observer<List<Property>> propertiesObserver;

    @Mock
    Observer<Property> propertyObserver;

    @Mock
    Observer<String> resultObserver;

    private PropertyViewModel propertyViewModel;
    private Address address = Mockito.mock(Address.class);
    private static double dailyRate = 5.00;
    private static String availWeekends = "yes";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        propertyViewModel = new PropertyViewModel(propertyRepo);
    }

//    @Test
//    public void setPropertyTest() {
//        Property property = new Property();
//        property.setAddressLine("105");
//        MutableLiveData<String> result = new MutableLiveData<>("1");
//        when(address.getAddressLine(0)).thenReturn("105");
//        when(address.getPostalCode()).thenReturn("2");
//        when(address.getLongitude()).thenReturn(1.0);
//        when(address.getLatitude()).thenReturn(2.0);
//        when(propertyRepo.create(property)).thenReturn(result);
//        propertyViewModel.setProperty(address, dailyRate, availWeekends).observeForever(resultObserver);
//        verify(resultObserver).onChanged(result.getValue());
//    }

    @Test
    public void addPropertyTest(){
        MutableLiveData<String> result = new MutableLiveData<>("Property Added");
        Property property = new Property();
        when(propertyRepo.create(property)).thenReturn(result);
        propertyViewModel.addProperty(property).observeForever(resultObserver);
        verify(resultObserver).onChanged(result.getValue());
    }

    @Test
    public void getPropertiesTest(){
        Property property = new Property();
        property.setAddressLine("105 Ridgewood Green");
        List propertyList = new ArrayList();
        propertyList.add(property);
        MutableLiveData<List<Property>> propertiesList = new MutableLiveData<>(propertyList);

        when(propertyRepo.selectAll()).thenReturn(propertiesList);
        propertyViewModel.getProperties().observeForever(propertiesObserver);
        verify(propertiesObserver, description("Properties not observed")).onChanged(propertyList);
    }

    @Test
    public void setPropertyToWriteTest(){
        Property property =  new Property();
        property.setAddressLine("105");
        MutableLiveData<Property> propertyToWrite = new MutableLiveData<>(property);
        propertyViewModel.getPropertyToWriteMutableLiveData().observeForever(propertyObserver);
        propertyViewModel.setPropertyToWrite(property);
        verify(propertyObserver, description("Property to write does not match")).onChanged(property);

    }
}
