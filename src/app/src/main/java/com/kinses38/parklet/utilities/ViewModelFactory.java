package com.kinses38.parklet.utilities;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.PropertyRepo;
import com.kinses38.parklet.data.repository.UserRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;
import com.kinses38.parklet.viewmodels.BookingViewModel;
import com.kinses38.parklet.viewmodels.HomeViewModel;

/**
 * Based on android architecture component samples
 * https://github.com/android/architecture-components-samples/blob/master/BasicRxJavaSample/app
 * /src/main/java/com/example/android/observability/ui/ViewModelFactory.java
 * Necessary to be able to mock ViewModel dependencies in unit tests as we cannot instantiate a
 * ViewModel with parameters using ViewModelProvider.
 */

public class ViewModelFactory implements ViewModelProvider.Factory {

    private BookingRepo bookingRepo;
    private VehicleRepo vehicleRepo;
    private UserRepo userRepo;
    private PropertyRepo propertyRepo;

    public ViewModelFactory(BookingRepo bookingRepo, VehicleRepo vehicleRepo) {
        this.bookingRepo = bookingRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public ViewModelFactory(UserRepo userRepo, BookingRepo bookingRepo) {
        this.userRepo = userRepo;
        this.vehicleRepo = vehicleRepo;
        this.bookingRepo = bookingRepo;
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

        throw new IllegalArgumentException("Unknown ViewModelClass");
    }
}
