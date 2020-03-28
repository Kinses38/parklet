package com.kinses38.parklet.viewmodels;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Responsible for the publishing new bookings to the Booking repository and and fetching all
 * bookings relating to a homeowner/renter/property.
 *
 **/

public class BookingViewModel extends ViewModel {

    private LiveData<List<Booking>> propertyBookings;
    private LiveData<List<Vehicle>> renterVehicles;

    private VehicleRepo vehicleRepo;
    private BookingRepo bookingRepo;

    private Booking booking;

    public BookingViewModel(BookingRepo bookingRepo, VehicleRepo vehicleRepo){
        this.bookingRepo = bookingRepo;
        this.vehicleRepo = vehicleRepo;
    }
    public void createBooking(Booking booking) {
        bookingRepo.create(booking);
    }

    public void setBookingDetails(Booking booking){
        this.booking = booking;
    }

    public Booking getBooking(){
        return this.booking;
    }

    public LiveData<List<Date>> getBookingsForProperty(String propertyUID) {
        if (propertyBookings == null) {
            propertyBookings = new MutableLiveData<>();
        }
        propertyBookings = bookingRepo.selectAllForProperty(propertyUID);
        LiveData<List<Date>> allDatesOfBookings = Transformations.map(propertyBookings, bookings -> convertAndFilter(bookings));

        return allDatesOfBookings;
    }

    public LiveData<List<Vehicle>> getUserVehicles() {
        if (renterVehicles == null) {
            renterVehicles = new MutableLiveData<>();
        }
        renterVehicles = vehicleRepo.selectAll();

        return renterVehicles;
    }

    @VisibleForTesting
    List<Date> convertAndFilter(List<Booking> bookings) {
        final int oneDay = -1;
        Calendar yesterday = Calendar.getInstance();
        //Get yesterdays date.
        yesterday.add(Calendar.DATE, oneDay);
        List<Date> allDates = bookings.stream()
                .filter(booking -> !booking.isRenterCancelled())
                .filter(booking -> !booking.isOwnerCancelled())
                .map(Booking::getBookingDates)
                .flatMap(Collection::stream)
                .map(Date::new)
                .filter(date -> !date.before(yesterday.getTime()))
                .collect(Collectors.toList());

        return allDates;
    }
}