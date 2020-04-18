package com.kinses38.parklet.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kinses38.parklet.data.model.entity.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for creation, updating and cancellation of all Bookings.
 */
public class BookingRepo {

    private final String TAG = this.getClass().getSimpleName();

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth ADB = FirebaseAuth.getInstance();

    //TODO enforce rule

    /**
     * Creates a new booking for user. Atomically creates booking object entry under bookings node,
     * and entries under the booking table node. All writes must succeed or none will be written, achieved using
     * a map of requests to be written to real time database.
     *
     * @param booking
     */
    public void create(Booking booking) {
        String bookingKey = DB.child("bookings").push().getKey();
        booking.setRenterUID(ADB.getCurrentUser().getUid());
        booking.setRenterName(ADB.getCurrentUser().getDisplayName());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("bookings/" + bookingKey, booking);
        for (Long timestamp : booking.getBookingDates()) {
            requestMap.put("bookingTable/" + booking.getPropertyUID() + timestamp, bookingKey);
        }
        DB.updateChildren(requestMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "Booking Created");
            } else {
                Log.i(TAG, "Booking Failed");
            }
        });
    }

    /**
     * Query all bookings belonging to a singular property
     *
     * @param propertyUID the id of property in question
     * @return livedata of all if any bookings associated with this property.
     */
    public MutableLiveData<List<Booking>> selectAllForProperty(String propertyUID) {
        MutableLiveData<List<Booking>> propertyBookingMutableLiveData = new MutableLiveData<>();
        DatabaseReference allBookings = DB.child("bookings/");

        allBookings.orderByChild("propertyUID").equalTo(propertyUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Booking> bookings = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Booking booking = ds.getValue(Booking.class);
                            booking.setBookingUID(ds.getKey());
                            bookings.add(booking);
                        }
                        propertyBookingMutableLiveData.postValue(bookings);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, databaseError.getMessage());
                    }
                });

        return propertyBookingMutableLiveData;
    }

    /**
     * All bookings belonging to the current user as a renter.
     *
     * @return livedata of all if any bookings associated with the user as renter.
     */
    public MutableLiveData<List<Booking>> selectAllUserRentals() {
        MutableLiveData<List<Booking>> userRentalsMutableLiveData = new MutableLiveData<>();
        DatabaseReference allBookings = DB.child("bookings/");

        allBookings.orderByChild("renterUID").equalTo(ADB.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Booking> userRentals = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Booking booking = ds.getValue(Booking.class);
                            booking.setBookingUID(ds.getKey());
                            userRentals.add(booking);
                        }
                        userRentalsMutableLiveData.postValue(userRentals);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, databaseError.getMessage());
                    }
                });

        return userRentalsMutableLiveData;
    }

    /**
     * Return every booking belonging to every Property belonging to the current user.
     *
     * @return livedata of all if any bookings belong to that all of the user's properties.
     */
    public MutableLiveData<List<Booking>> selectAllUsersPropertiesBooking() {
        MutableLiveData<List<Booking>> userPropertiesBookings = new MutableLiveData<>();
        DatabaseReference allBookings = DB.child("bookings/");

        allBookings.orderByChild("ownerUID").equalTo(ADB.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Booking> propertiesBookings = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Booking booking = ds.getValue(Booking.class);
                            booking.setBookingUID(ds.getKey());
                            propertiesBookings.add(booking);
                        }
                        userPropertiesBookings.postValue(propertiesBookings);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, databaseError.getMessage());
                    }
                });

        return userPropertiesBookings;
    }

    /**
     * Update the booking to be cancelled whether by the renter or homeowner.
     * RealTime database recognises the updated fields and will only update that.
     *
     * @param booking to be cancelled.
     */
    //TODO atomic multipath delete table and then update booking.
    public void cancelBooking(Booking booking) {
        DatabaseReference allBookings = DB.child("bookings/");
        allBookings.child(booking.getBookingUID()).setValue(booking)
                .addOnSuccessListener(aVoid -> Log.i(TAG, "Booking cancelled"))
                .addOnFailureListener(e -> Log.i(TAG, e.getMessage()));
    }

    /**
     * Attempt to check-in or out at a property. Using the booking query consisting of the propertyID
     * and current Days date, check the booking table for an entry. If nothing is found, inform
     * the user there is no booking for property.
     * Otherwise check the current check-in status and update.
     *
     * @param bookingQuery the propertyID with epoch of current days date to use as a key to search for that days booking.
     * @return the status of the check-in. Subscribed to by booking ViewModel.
     */
    public LiveData<String> updateCheckInStatus(String bookingQuery) {
        MutableLiveData<String> status = new MutableLiveData<>();
        status.postValue("Checking for booking");
        //Query Booking table first for booking on that Day
        DB.child("bookingTable/").orderByKey().equalTo(bookingQuery).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String bookingKey = ds.getValue(String.class);
                        //If booking exists flip the current check-in status.
                        DB.child("bookings/").orderByKey().equalTo(bookingKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //Todo refactor check-in logic to booking. Check renter id and cancellation/complete status.
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Booking booking = ds.getValue(Booking.class);
                                    booking.setRenterAtProperty(!booking.isRenterAtProperty());
                                    DB.child("bookings/").child(bookingKey).child("renterAtProperty")
                                            .setValue(booking.isRenterAtProperty());
                                    if (booking.isRenterAtProperty()) {
                                        status.postValue("You are checked in!");
                                    } else {
                                        status.postValue("You are checked out!");
                                    }
                                    Log.i(TAG, "CheckInStatus updated");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i(TAG, databaseError.getMessage());
                            }
                        });
                    }
                } else {
                    status.postValue("Booking not found!");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                status.postValue("There was an error, try again");
            }
        });
        return status;
    }
}




