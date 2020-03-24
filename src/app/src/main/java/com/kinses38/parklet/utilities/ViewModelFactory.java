package com.kinses38.parklet.utilities;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;
import com.kinses38.parklet.viewmodels.BookingViewModel;

/**
 * Based on android architecture component samples
 * https://github.com/android/architecture-components-samples/blob/master/BasicRxJavaSample/app/src/main/java/com/example/android/observability/ui/ViewModelFactory.java
 *
 * Necessary to be able to mock ViewModel dependencies in unit tests as we cannot instantiate a ViewModel with parameters using ViewModelProvider.
 */

public class ViewModelFactory implements ViewModelProvider.Factory {

    private BookingRepo bookingRepo;
    private VehicleRepo vehicleRepo;

    public ViewModelFactory(BookingRepo bookingRepo, VehicleRepo vehicleRepo){
        this.bookingRepo = bookingRepo;
        this.vehicleRepo = vehicleRepo;
    }


    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        if(modelClass.isAssignableFrom(BookingViewModel.class)){
            return  (T) new BookingViewModel(bookingRepo, vehicleRepo);
        }

        throw new IllegalArgumentException("Unknown ViewModelClass");
    }
}
