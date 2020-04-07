package com.kinses38.parklet.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.PropertyRepo;
import com.kinses38.parklet.data.repository.UserRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Based on android architecture component samples
 * https://github.com/android/architecture-components-samples/blob/master/BasicRxJavaSample/app
 * /src/main/java/com/example/android/observability/ui/ViewModelFactory.java
 * Necessary to be able to mock ViewModel dependencies in unit tests as we cannot instantiate a
 * ViewModel with parameters using ViewModelProvider.
 */
@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    /*
        Dagger supports only injecting one constructor so to get around this, inject the field.
        With one constructor marked as injectable, all will be. However we need to inject the parameters
        separately if they differ from the injected constructor's ones. Injectable fields cannot be private.
     */
    @Inject
    UserRepo userRepo;
    @Inject
    BookingRepo bookingRepo;
    @Inject
    VehicleRepo vehicleRepo;
    @Inject
    PropertyRepo propertyRepo;

    //Booking viewModel
    @Inject
    public ViewModelFactory(BookingRepo bookingRepo, VehicleRepo vehicleRepo) {
        this.bookingRepo = bookingRepo;
        this.vehicleRepo = vehicleRepo;
    }

    //Home viewModel
    public ViewModelFactory(UserRepo userRepo, BookingRepo bookingRepo) {
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    //Vehicle viewModel
    public ViewModelFactory(VehicleRepo vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    //Map & Property viewModel
    public ViewModelFactory(PropertyRepo propertyRepo) {
        this.propertyRepo = propertyRepo;
    }


    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BookingViewModel.class)) {
            return (T) new BookingViewModel(bookingRepo, vehicleRepo);
        }
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(userRepo, bookingRepo);
        }
        if (modelClass.isAssignableFrom(VehiclesViewModel.class)) {
            return (T) new VehiclesViewModel(vehicleRepo);
        }
        if (modelClass.isAssignableFrom(PropertyViewModel.class)) {
            return (T) new PropertyViewModel(propertyRepo);
        }
        if (modelClass.isAssignableFrom(MapViewModel.class)){
            return (T) new MapViewModel(propertyRepo);
        }

        throw new IllegalArgumentException("Unknown ViewModelClass");
    }
}
