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
 * Responsible for the publishing new bookings to the Booking repository, fetching all
 * bookings for a given property and users vehicles for renting with.
 * Observed by the Booking Fragment
 **/

public class BookingViewModel extends ViewModel {

    private LiveData<List<Booking>> propertyBookings;
    private LiveData<List<Vehicle>> renterVehicles;
    private boolean status;
    private MutableLiveData<Boolean> bookingIsConfirmed = new MutableLiveData<>();

    private VehicleRepo vehicleRepo;
    private BookingRepo bookingRepo;

    private Booking booking;

    BookingViewModel(BookingRepo bookingRepo, VehicleRepo vehicleRepo) {
        this.bookingRepo = bookingRepo;
        this.vehicleRepo = vehicleRepo;
    }

    /**
     * Call to Booking repo to create new booking with relevant details.
     *
     * @param booking relevant details
     */
    public void createBooking(Booking booking) {
        bookingRepo.create(booking);
        //Booking fragment observes status to know when to navigate back to home page.
        status = true;
        bookingIsConfirmed.setValue(status);
    }

    public LiveData<Boolean> getBookingStatus() {
        return bookingIsConfirmed;
    }

    public void setBookingStatus(boolean status) {
        this.status = status;
        bookingIsConfirmed.setValue(status);
    }

    /**
     * Set details of booking for user to review in confirmation window before submitting.
     *
     * @param booking
     */
    public void setBookingDetails(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() {
        return this.booking;
    }

    /**
     * Query all bookings for the given property so user can check available dates.
     *
     * @param propertyUID the id of the property the user is observing.
     * @return a list of dates of each booking for that property.
     */
    public LiveData<List<Date>> getBookingsForProperty(String propertyUID) {
        if (propertyBookings == null) {
            propertyBookings = new MutableLiveData<>();
        }
        propertyBookings = bookingRepo.selectAllForProperty(propertyUID);
        LiveData<List<Date>> allDatesOfBookings = Transformations.map(propertyBookings,
                bookings -> convertAndFilter(bookings));

        return allDatesOfBookings;
    }

    /**
     * Get current users vehicles to make the booking under.
     *
     * @return all eligible vehicles belonging to the user.
     */
    public LiveData<List<Vehicle>> getUserVehicles() {
        if (renterVehicles == null) {
            renterVehicles = new MutableLiveData<>();
        }
        renterVehicles = vehicleRepo.selectAll();

        return renterVehicles;
    }

    /**
     * Helper function to convert epochs to dates,
     * filter any date before the current days date as its not relevant.
     * If a booking has been cancelled, the date will be show as available.
     *
     * @param bookings all bookings belonging to a property
     * @return list of java dates corresponding to all the bookings for the given property.
     */
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