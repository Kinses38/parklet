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
    private ParkLetCalendarView calendarView;
    private Spinner spinner;

    private String renterVehicleReg;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false);
        //TODO dagger dependency injection to sort this Repo mess?
        ViewModelFactory viewModelFactory = new ViewModelFactory(new BookingRepo(),
                new VehicleRepo());
        bookingViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(BookingViewModel.class);
        Property propertyToBook = BookingFragmentArgs.fromBundle(requireArguments())
                .getPropertyToBook();

        initBindings(propertyToBook);
        observeBookings();
        observeVehicles();
        observeBookingStatus();

        calendarView.initCalendar(propertyToBook.getWeekendBookings());
        Log.i(TAG, propertyToBook.getAddressLine());
        return binding.getRoot();

    }

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

    private void observeVehicles() {
        bookingViewModel.getUserVehicles().observe(getViewLifecycleOwner(), vehicles -> {
            if (!vehicles.isEmpty()) {
                binding.setHasVehicle(true);
                ArrayAdapter<Vehicle> adapter = new ArrayAdapter<>(requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item, vehicles);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                binding.setHasVehicle(false);
                //TODO show button to navigate to vehicle page instead
            }
        });
    }

    private void observeBookingStatus(){
        bookingViewModel.getBookingStatus().observe(getViewLifecycleOwner(), status -> {
            if(status){
                bookingViewModel.setBookingStatus(false);
                Toast.makeText(getActivity(), "Booking successfully made", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_booking_to_nav_home);
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

    //Using share ViewModel this time to deal with fragment to fragment communication
    public void confirmationDialog() {
        FragmentTransaction fragTrans = getChildFragmentManager().beginTransaction();
        ConfirmationFragmentDialog dialog = ConfirmationFragmentDialog.newInstance();
        dialog.show(fragTrans, dialog.getClass().getSimpleName());
    }

    public void onClick(View v) {
        Log.i("ID here: ", "" + v.getId());
        switch (v.getId()) {
            case R.id.confirm_booking_button:
                Log.i(TAG, "Confirm booking button clicked");
                submitBooking();
                break;
            case R.id.booking_confirm_button:
                Log.i(TAG, "Dialog button working");
            default:
                break;
        }
    }
}