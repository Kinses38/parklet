package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;
import com.kinses38.parklet.databinding.FragmentBookingBinding;
import com.kinses38.parklet.utilities.ParkLetCalendarView;
import com.kinses38.parklet.utilities.ViewModelFactory;
import com.kinses38.parklet.viewmodels.BookingViewModel;

import java.util.List;

public class BookingFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private FragmentBookingBinding binding;
    private BookingViewModel bookingViewModel;
    private ViewModelFactory viewModelFactory;
    private ParkLetCalendarView calendarView;
    private Spinner spinner;

    private String renterVehicleReg;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false);
        //TODO dagger dependency injection to sort this Repo mess?
        viewModelFactory = new ViewModelFactory(new BookingRepo(), new VehicleRepo());
        bookingViewModel = new ViewModelProvider(this, viewModelFactory).get(BookingViewModel.class);
        Property propertyToBook = BookingFragmentArgs.fromBundle(requireArguments()).getPropertyToBook();

        initBindings(propertyToBook);
        observeBookings();
        observeVehicles();

        calendarView.initCalendar(propertyToBook.getWeekendBookings());
        Log.i(TAG, propertyToBook.getAddressLine());
        return binding.getRoot();

    }

    private void observeBookings() {
        bookingViewModel.getBookingsForProperty(binding.getPropertyToBook().getPropertyUID()).observe(getViewLifecycleOwner(), bookingDates -> {
            if (!bookingDates.isEmpty()) {
                calendarView.refreshCalendar(bookingDates);
                } else {
                calendarView.refreshCalendar();
            }});
    }

    private void observeVehicles() {
        bookingViewModel.getUserVehicles().observe(getViewLifecycleOwner(), vehicles -> {
            if (!vehicles.isEmpty()) {
                binding.setHasVehicle(true);
                ArrayAdapter<Vehicle> adapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, vehicles);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                binding.setHasVehicle(false);
                //TODO show button to navigate to vehicle page instead
            }
        });
    }

    private void initBindings(Property property) {
        binding.setBookingFrag(this);
        calendarView = binding.calendarView;
        binding.setPropertyToBook(property);
        spinner = binding.vehicleSpinner;
        binding.setHasVehicle(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renterVehicleReg = ((Vehicle) parent.getSelectedItem()).getReg();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not needed.
            }
        });
    }

    private void submitBooking() {
        Property propertyToBook = binding.getPropertyToBook();
        List<Long> datesTimeStamp = calendarView.getAndConvertDates();
        Log.i(TAG, "Dates gathered");
        Booking booking = new Booking(propertyToBook.getOwnerUID(),
                propertyToBook.getPropertyUID(),
                renterVehicleReg,
                propertyToBook.getDailyRate(),
                propertyToBook.getDailyRate() * calendarView.getSelectedCount(),
                datesTimeStamp);
        bookingViewModel.createBooking(booking);
        calendarView.clearSelectedDates();
    }

    public void onClick(View v) {
        Log.i("ID here: ", "" + v.getId());
        switch (v.getId()) {
            case R.id.confirm_booking_button:
                Log.i(TAG, "Confirm booking button clicked");
                submitBooking();
                break;
            default:
                break;
        }
    }

}