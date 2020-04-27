package com.kinses38.parklet.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.VehicleRepo;

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

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class VehicleViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private VehicleRepo vehicleRepo = Mockito.mock(VehicleRepo.class);

    @Mock
    Observer<List<Vehicle>> vehiclesObserver;

    @Mock
    Observer<String> resultObserver;

    private String make = "Ford";
    private String model = "Focus";
    private String reg = "192-D-1234";

    private VehiclesViewModel vehiclesViewModel;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        vehiclesViewModel = new VehiclesViewModel(vehicleRepo);
    }

    @Test
    public void submitVehicleTest(){

        MutableLiveData<String> result = new MutableLiveData<>("Model of vehicle required\n");
        Vehicle vehicle = new Vehicle("Ford", "Focus", "98-D-1234");
        when(vehicleRepo.create(vehicle)).thenReturn(result);
        vehiclesViewModel.submitVehicle(make, "", reg).observeForever(resultObserver);
        verify(resultObserver).onChanged(result.getValue());
    }

    @Test
    public void validateVehicleTest(){
        String noMake = "Make of vehicle required\n";
        String noModel = "Model of vehicle required\n";
        String noReg = "Registration number required\n";

        assertEquals(vehiclesViewModel.validateVehicle("", model, reg), noMake);
        assertEquals(vehiclesViewModel.validateVehicle(make, "", reg), noModel);
        assertEquals(vehiclesViewModel.validateVehicle(make, model, ""), noReg);

        assertEquals(vehiclesViewModel.validateVehicle("","",""), noMake+noModel+noReg);

    }

    @Test
    public void createNewVehicleTest(){
        MutableLiveData<String> result = new MutableLiveData<>("Created");
        Vehicle vehicle = new Vehicle(make, model, reg);
        when(vehicleRepo.create(vehicle)).thenReturn(result);
        vehiclesViewModel.createNewVehicle(vehicle).observeForever(resultObserver);
        verify(resultObserver).onChanged("Created");
    }

    @Test
    public void getAllVehiclesTest(){
        Vehicle vehicle = new Vehicle("Ford", "Focus", "98-D-1234");
        List vehicleList = new ArrayList();
        vehicleList.add(vehicle);
        MutableLiveData<List<Vehicle>> vehiclesList = new MutableLiveData<>(vehicleList);
        //ViewModel expecting MutableLiveData list of vehicles back.
        when(vehicleRepo.selectAll()).thenReturn(vehiclesList);
        vehiclesViewModel.getVehicles().observeForever(vehiclesObserver);
        //Observer expecting just a list of vehicles.
        verify(vehiclesObserver, description("Failed to get user vehicles")).onChanged(vehicleList);
    }
}
