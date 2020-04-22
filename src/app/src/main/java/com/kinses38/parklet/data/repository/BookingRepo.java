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
    private final static String BOOKINGSNODE = "bookings/";
    private final static String BOOKINGTABLENODE = "bookingTable/";
    private final static String OWNERUIDCHILD = "ownerUID";
    private final static String RENTERUIDCHILD = "renterUID";
    private final static String PROPERTYUIDCHILD = "propertyUID";
    private final static String RENTERARRIVEDCHILD = "renterAtProperty";
    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth ADB = FirebaseAuth.getInstance();

    /**
     * Creates a new booking for user. Atomically creates booking object entry under bookings node,
     * and entries under the booking table node. All writes must succeed or none will be written, achieved using
     * a map of requests to be written to real time database.
     *
     * @param booking object containing all particulars of the requested booking.
     */
    public void create(Booking booking) {
        String bookingKey = DB.child(BOOKINGSNODE).push().getKey();
        booking.setRenterUID(ADB.getCurrentUser().getUid());
        booking.setRenterName(ADB.getCurrentUser().getDisplayName());

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(BOOKINGSNODE + bookingKey, booking);
        for (Long timestamp : booking.getBookingDates()) {
            requestMap.put(BOOKINGTABLENODE + booking.getPropertyUID() + timestamp, bookingKey);
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
        DatabaseReference allBookings = DB.child(BOOKINGSNODE);

        allBookings.orderByChild(PROPERTYUIDCHILD).equalTo(propertyUID)
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
        DatabaseReference allBookings = DB.child(BOOKINGSNODE);

        allBookings.orderByChild(RENTERUIDCHILD).equalTo(ADB.getCurrentUser().getUid())
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
        DatabaseReference allBookings = DB.child(BOOKINGSNODE);

        allBookings.orderByChild(OWNERUIDCHILD).equalTo(ADB.getCurrentUser().getUid())
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
     * Sets bookingTable entries to null to remove. Completed atomically, all must succeed or
     * all fail. Removal of booking table entry ensures the renter cannot check-in or clash with someone
     * who booked the same property for the same day after a cancellation.
     *
     * @param booking to be cancelled.
     */
    public void cancelBooking(Booking booking) {
        Map<String, Object> requestMap = new HashMap<>();
        for (Long date : booking.getBookingDates()) {
            //Get each day entry for the booking table
            requestMap.put(BOOKINGTABLENODE + booking.getPropertyUID() + date, null);
        }
        requestMap.put(BOOKINGSNODE + booking.getBookingUID(), booking);
        DB.updateChildren(requestMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, booking.getBookingUID() + " cancelled");
            } else {
                Log.i(TAG, "Could not cancel");
            }
        });
    }

    /**
     * Attempt to check-in or out at a property. Using the booking query consisting of the propertyID
     * and current Days date, check the booking table for an entry. If nothing is found, inform
     * the user there is no booking for property.
     * Otherwise, confirm the booking is still valid and for the current user.
     * Then update the current checkin status
     *
     * @param bookingQuery the propertyID with epoch of current days date to use as a key to search for that days booking.
     * @return the status of the check-in. Subscribed to by booking ViewModel.
     */
    public LiveData<String> updateCheckInStatus(String bookingQuery) {
        MutableLiveData<String> status = new MutableLiveData<>();
        String currentUser = ADB.getCurrentUser().getUid();
        status.postValue("Checking for booking");
        DB.child(BOOKINGTABLENODE).child(bookingQuery).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String bookingKey = dataSnapshot.getValue(String.class);
                    DB.child(BOOKINGSNODE).child(bookingKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Booking booking = dataSnapshot.getValue(Booking.class);
                                if (booking.checkerRenter(currentUser) && !booking.isBookingCancelled()) {
                                    status.postValue(booking.updateCheckIn());
                                    DB.child(BOOKINGSNODE)
                                            .child(bookingKey)
                                            .child(RENTERARRIVEDCHILD)
                                            .setValue(booking.isRenterAtProperty());
                                } else {
                                    status.postValue("Booking not valid");
                                }
                            } else {
                                status.postValue("Booking not found");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i(TAG, databaseError.getMessage());
                            status.postValue("There was an error, please try again");
                        }
                    });
                } else {
                    status.postValue("Booking not found");
                    Log.i(TAG, "No booking for property on this day");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, databaseError.getMessage());
                status.postValue("There was an error, please try again");
            }
        });
        return status;
    }
}