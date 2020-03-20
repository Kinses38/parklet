package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.applandeo.materialcalendarview.CalendarView;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.databinding.FragmentBookingBinding;
import com.kinses38.parklet.viewmodels.BookingViewModel;

import java.util.Calendar;

public class BookingFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private FragmentBookingBinding binding;
    private BookingViewModel bookingViewModel;
    private CalendarView calendarView;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        initBindings();
        initCalendar();
        Property propertyToBook = BookingFragmentArgs.fromBundle(getArguments()).getPropertyToBook();
        Log.i(TAG, propertyToBook.getAddressLine());
        return binding.getRoot();

    }

    private void observeBookings(Property property) {
        //Todo get all bookings for this property and populate calendar. Filter by date, or retired old dates on firebase side.
    }

    private void initBindings() {
        calendarView = binding.calendarView;
    }

    //Todo: placeholder, calendar have its own utility class?
    private void initCalendar() {
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DATE, -1);
        calendarView.setMinimumDate(minDate);

    }
}