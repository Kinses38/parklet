package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;

import java.util.List;

/**
 * Responsible for the publishing new bookings to the Booking repository and and fetching all bookings relating to a homeowner/renter/property.
 * Shared with homepage so user can see upcoming bookings. Lifecycle is tied to main activity but only when either home or booking fragment is active.
 **/

public class BookingViewModel extends ViewModel {

    private LiveData<List<Booking>> propertyBookings;
    private LiveData<List<Vehicle>> renterVehicles;

    private VehicleRepo vehicleRepo = new VehicleRepo();
    private BookingRepo bookingRepo = new BookingRepo();

    public void createBooking(Booking booking) {
        bookingRepo.create(booking);
    }


    //Todo expose only dates taken here? Rather than full booking object
    public LiveData<List<Booking>> getBookingsForProperty(String propertyUID) {
        if (propertyBookings == null) {
            propertyBookings = new MutableLiveData<>();
        }
        propertyBookings = bookingRepo.selectAllForProperty(propertyUID);

        return propertyBookings;
    }

    public LiveData<List<Vehicle>> getUserVehicles() {
        if (renterVehicles == null) {
            renterVehicles = new MutableLiveData<>();
        }
        renterVehicles = vehicleRepo.selectAll();

        return renterVehicles;
    }
}