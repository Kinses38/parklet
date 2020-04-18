package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.kinses38.parklet.ParkLet;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.databinding.FragmentBookingBinding;
import com.kinses38.parklet.utilities.ParkLetCalendarView;
import com.kinses38.parklet.viewmodels.BookingViewModel;
import com.kinses38.parklet.viewmodels.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

/**
 * Responsible for showing current available dates to book for a property, dates prepared by
 * Booking ViewModel. Allows user to select dates and vehicles for their booking.
 * Uses ParkletView Calendar to allow selection of dates.
 */
public class BookingFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private FragmentBookingBinding binding;
    private BookingViewModel bookingViewModel;
    private ParkLetCalendarView calendarView;
    private Spinner spinner;

    private String renterVehicleReg;
    @Inject
    ViewModelFactory viewModelFactory;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false);

        ParkLet.getParkLetApp().getBookingRepoComponent().inject(this);
        ParkLet.getParkLetApp().getVehicleRepoComponent().inject(this);
        bookingViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(BookingViewModel.class);

        Property propertyToBook = BookingFragmentArgs.fromBundle(requireArguments())
                .getPropertyToBook();

        initBindings(propertyToBook);
        observeBookings();
        observeVehicles();
        observeBookingStatus();

        //Initialise calendar and check if current property allows weekend bookings.
        calendarView.initCalendar(propertyToBook.getWeekendBookings());
        Log.i(TAG, propertyToBook.getAddressLine());
        return binding.getRoot();

    }

    /**
     * Call to booking ViewModel to query for all the bookings relevant to this property and update
     * the calendar view appropriately.
     */
    private void observeBookings() {
        bookingViewModel.getBookingsForProperty(binding.getPropertyToBook().getPropertyUID())
                .observe(getViewLifecycleOwner(), bookingDates -> {
                    if (!bookingDates.isEmpty()) {
                        calendarView.refreshCalendar(bookingDates);
                    } else {
                        calendarView.refreshCalendar();
                    }
                });
    }

    /**
     * Call to booking ViewModel to get the current user's vehicle(s).
     * Binds the vehicles to a spinner dropdown menu if the user currently has vehicles.
     * Otherwise informs user they must have a vehicle to make a booking.
     */
    private void observeVehicles() {
        bookingViewModel.getUserVehicles().observe(getViewLifecycleOwner(), vehicles -> {
            if (!vehicles.isEmpty()) {
                binding.setHasVehicle(true);
                ArrayAdapter<Vehicle> adapter = new ArrayAdapter<>(requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item, vehicles);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                //set layout visibility for dropdown to hidden and give warning to user instead.
                binding.setHasVehicle(false);
            }
        });
    }

    /**
     * If the user successfully confirms the booking after review, inform them then navigate back
     * to the home fragment page.
     */
    private void observeBookingStatus() {
        bookingViewModel.getBookingStatus().observe(getViewLifecycleOwner(), status -> {
            if (status) {
                bookingViewModel.setBookingStatus(false);
                Toast.makeText(getActivity(), "Booking successfully made", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_booking_to_nav_home);
            }
        });
    }

    /**
     * Initialise all relevant bindings.
     * Passes the current property to layout file for DataBinding.
     * Initialise spinner and listener for selection of vehicle for renting under.
     *
     * @param property the current property the user is interested in renting.
     */
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

    /**
     * Create the booking object and pass to confirmation dialog for the user
     * to review before confirming.
     */
    private void submitBooking() {
        Property propertyToBook = binding.getPropertyToBook();
        List<Long> datesTimeStamp = calendarView.getAndConvertDates();
        Log.i(TAG, "Dates gathered");
        Booking booking = new Booking(
                propertyToBook.getOwnerUID(),
                propertyToBook.getOwnerName(),
                propertyToBook.getPropertyUID(),
                propertyToBook.getAddressLine(),
                renterVehicleReg,
                propertyToBook.getDailyRate(),
                propertyToBook.getDailyRate() * calendarView.getSelectedCount(),
                datesTimeStamp);
        bookingViewModel.setBookingDetails(booking);
        confirmationDialog();
        calendarView.clearSelectedDates();

    }

    /**
     * Create the confirmation dialog for booking confirmation.
     * Observes the current booking from booking viewModel after passing it from this class
     */
    private void confirmationDialog() {
        FragmentTransaction fragTrans = getChildFragmentManager().beginTransaction();
        ConfirmationFragmentDialog dialog = ConfirmationFragmentDialog.newInstance();
        dialog.show(fragTrans, dialog.getClass().getSimpleName());
    }

    public void onClick(View v) {
        Log.i("ID here: ", "" + v.getId());
        switch (v.getId()) {
            case R.id.submit_booking:
                Log.i(TAG, "booking submitted.");
                submitBooking();
                break;
            default:
                break;
        }
    }
}