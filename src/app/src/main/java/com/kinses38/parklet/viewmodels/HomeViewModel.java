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

/**
 * Responsible for calling to User Repo to create new User profiles and updating FCM Tokens.
 * Retrieving bookings relevant to the user through Booking repo.
 */
public class HomeViewModel extends ViewModel {

    private UserRepo userRepo;
    private BookingRepo bookingRepo;
    private MediatorLiveData<List<Booking>> allBookings;


    /**
     * Provided by ViewModel Factory
     *
     * @param userRepo    singleton repo injected by Dagger
     * @param bookingRepo singleton repo injected by Dagger
     */
    HomeViewModel(UserRepo userRepo, BookingRepo bookingRepo) {
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    public void createUserProfile(User user) {
        userRepo.setNewUser(user);
    }

    public void updateUserFcmToken(User user) {
        userRepo.updateUserFcmToken(user);
    }

    /**
     * Retrieve all relevant bookings to user through MediatorLiveData.
     * Notifies observers when either have been updated.
     *
     * @return combination of bookings where user is homeowner or renter to Home Fragment.
     */
    public LiveData<List<Booking>> getUsersBookings() {
        if (allBookings == null) {
            allBookings = new MediatorLiveData();
        }
        //Bookings where user is the Renter
        LiveData userRentals = bookingRepo.selectAllUserRentals();
        //Bookings where user is the Owner
        LiveData propertyBookings = bookingRepo.selectAllUsersPropertiesBooking();

        //Set all bookings to be sourced from both userRentals and propertyBookings.
        allBookings.addSource(userRentals, rentals -> allBookings
                .setValue(mergeBookingsLiveData(userRentals, propertyBookings)));
        allBookings.addSource(propertyBookings, rentals -> allBookings
                .setValue(mergeBookingsLiveData(userRentals, propertyBookings)));

        return allBookings;
    }

    /**
     * Helper function to combine result of both calls to Booking Repo.
     *
     * @param userRentals     All bookings where user has rented a driveway.
     * @param propertyRentals All bookings where user has had their driveway Rented
     * @return livedata of concatenated bookings.
     */
    @Nullable
    private List<Booking> mergeBookingsLiveData(LiveData<List<Booking>> userRentals,
                                                LiveData<List<Booking>> propertyRentals) {
        List<Booking> user = userRentals.getValue();
        List<Booking> property = propertyRentals.getValue();
        /* If neither has results yet return null.
           If a user has no bookings then an empty list will be returned when complete */
        if (user == null || property == null) {
            return null;
        }
        List<Booking> combinedBookings = new ArrayList<>();
        combinedBookings.addAll(user);
        combinedBookings.addAll(property);

        return combinedBookings;
    }

    /**
     * When user cancels a booking, check if they are renter or home owner and update
     * appropriate field in booking object
     *
     * @param currentUserUID user who requested a cancellation
     * @param booking        the booking being cancelled.
     */
    public void cancelBooking(String currentUserUID, Booking booking) {
        if (currentUserUID.equals(booking.getOwnerUID())) {
            booking.setOwnerCancelled(true);

        } else if (currentUserUID.equals(booking.getRenterUID())) {
            booking.setRenterCancelled(true);
        }
        bookingRepo.cancelBooking(booking);
    }

    /**
     * Attempt to check-in or out user at property by querying the booking lookup table for entry
     * matching the propertyID combined with the currentDate
     *
     * @param propertyUID Id of property read from the nfc tag.
     * @return Status indicating whether they are checked in, out or failure.
     */
    public LiveData<String> updateCheckInStatus(String propertyUID) {
        //get current date and concatenate with today's date to check firebase for booking
        long timestamp = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String bookingQuery = propertyUID + timestamp;
        LiveData<String> checkInStatus = bookingRepo.updateCheckInStatus(bookingQuery);
        //return status to user
        return checkInStatus;
    }
}