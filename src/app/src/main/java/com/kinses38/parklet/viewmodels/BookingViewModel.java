package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.repository.BookingRepo;

import java.util.List;

/**
 * Responsible for the publishing new bookings to the Booking repository and and fetching all bookings relating to a homeowner/renter/property.
 * Shared with homepage so user can see upcoming bookings. Lifecycle is tied to main activity but only when either home or booking fragment is active.
 **/

public class BookingViewModel extends ViewModel {

    private LiveData<List<Booking>> propertyBookings;
    private BookingRepo bookingRepo = new BookingRepo();

    //Todo create booking object here?
    public void createBooking(Booking booking) {
        bookingRepo.create(booking);
    }


    //Todo conversion of longs to dates here?
    public LiveData<List<Booking>> getBookingsForProperty(String propertyUID) {
        if (propertyBookings == null) {
            propertyBookings = new MutableLiveData<>();
        }
        propertyBookings = bookingRepo.selectAllForProperty(propertyUID);

        return propertyBookings;
    }

}