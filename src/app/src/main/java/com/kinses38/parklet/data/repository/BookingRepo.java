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
import java.util.List;

public class BookingRepo {

    private final String TAG = this.getClass().getSimpleName();

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth ADB = FirebaseAuth.getInstance();

    public void create(Booking booking) {
        String bookingKey = DB.child("bookings").push().getKey();
        DatabaseReference bookingRef = DB.child("bookings/" + bookingKey);
        booking.setRenterUID(ADB.getCurrentUser().getUid());
        booking.setRenterName(ADB.getCurrentUser().getDisplayName());

        bookingRef.setValue(booking).addOnSuccessListener(aVoid -> Log.i(TAG, "Booking added"))
                .addOnFailureListener(e -> Log.i(TAG, "Booking not added" + e.getMessage()));
    }

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
}




