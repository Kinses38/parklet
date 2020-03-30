package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.UserRepo;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private UserRepo userRepo;
    private BookingRepo bookingRepo;
    MediatorLiveData<List<Booking>> allBookings;


    public HomeViewModel(UserRepo userRepo, BookingRepo bookingRepo) {
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    public void createUserProfile(User user) {
        userRepo.setNewUser(user);
    }


    public LiveData<List<Booking>> getUsersBookings() {
        if (allBookings == null) {
            allBookings = new MediatorLiveData();
        }
        //Bookings where user is the Renter
        LiveData userRentals = bookingRepo.selectAllUserRentals();
        //Bookings where user is the Owner
        LiveData propertyBookings = bookingRepo.selectAllUsersPropertiesBooking();

        //Combine
        allBookings.addSource(userRentals, rentals -> allBookings
                .setValue(mergeBookingsLiveData(userRentals, propertyBookings)));
        allBookings.addSource(propertyBookings, rentals -> allBookings
                .setValue(mergeBookingsLiveData(userRentals, propertyBookings)));

        return allBookings;
    }

    @Nullable
    private List<Booking> mergeBookingsLiveData(LiveData<List<Booking>> userRentals,
                                                LiveData<List<Booking>> propertyRentals) {
        List<Booking> user = userRentals.getValue();
        List<Booking> property = propertyRentals.getValue();
        if (user == null || property == null) {
            return null;
        }
        List<Booking> combinedBookings = new ArrayList<>();
        combinedBookings.addAll(user);
        combinedBookings.addAll(property);

        return combinedBookings;
    }

    public void cancelBooking(String currentUserUID, Booking booking){
        if(currentUserUID.equals(booking.getOwnerUID())){
            booking.setOwnerCancelled(true);

        }else if(currentUserUID.equals(booking.getRenterUID())){
            booking.setRenterCancelled(true);
        }
        bookingRepo.cancelBooking(booking);
    }

    public LiveData<String> updateCheckInStatus(String propertyUID){
        //get current date and concatenate with todays date to check firebase for booking
        long timestamp = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String bookingQuery = propertyUID + timestamp;
        LiveData<String> checkInStatus = bookingRepo.updateCheckInStatus(bookingQuery);
        return checkInStatus;
        //return status to person
    }
}