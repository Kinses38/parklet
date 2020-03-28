package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;
import com.kinses38.parklet.databinding.FragmentConfirmationDialogBinding;
import com.kinses38.parklet.utilities.ViewModelFactory;
import com.kinses38.parklet.viewmodels.BookingViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ConfirmationFragmentDialog extends DialogFragment implements View.OnClickListener {

    private TextView bookingErrors, bookingTotalDays, bookingTotalPrice, bookingSummaryText;
    private FragmentConfirmationDialogBinding binding;
    private Booking booking;
    private BookingViewModel bookingViewModel;
    private boolean errorState;

    public static ConfirmationFragmentDialog newInstance() {
        ConfirmationFragmentDialog dialog = new ConfirmationFragmentDialog();
        return dialog;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_confirmation_dialog, container, true);
        ViewModelFactory viewModelFactory = new ViewModelFactory(new BookingRepo(),
                new VehicleRepo());
        bookingViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(BookingViewModel.class);
        initBinding();
        booking = bookingViewModel.getBooking();

        validateBooking(booking);
        bookingSummary(booking);
        return binding.getRoot();

    }

    private void initBinding() {
        binding.setDialogFrag(this);
        bookingErrors = binding.bookingErrors;
        bookingSummaryText = binding.bookingSummaryText;
        bookingTotalDays = binding.bookingTotalDays;
        bookingTotalPrice = binding.bookingTotalPrice;
    }

    private void validateBooking(Booking booking) {
        String errors = "";
        if (booking.getRenterVehicleReg() == null) {
            errors = "You must have a vehicle selected \n";
            errorState = true;
        }
        if (booking.getBookingDates().size() == 0) {
            errors = (errors + "You must select a date");
            errorState = true;
        }
        binding.setHasError(errorState);
        bookingErrors.setText(errors);

        Log.i("TAG", errors);
    }

    private String summariseDates(List<Long> dates) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM");
        String daysBooked = dates.stream().map(Date::new).map(format::format)
                .collect(Collectors.joining(",\n"));
        return daysBooked;
    }

    public void bookingSummary(Booking booking) {
        if (!errorState) {
            bookingSummaryText.setText("Confirm all booking details are correct?");
            bookingTotalDays.setText("Days Booked: \n" + summariseDates(booking.getBookingDates()));
            bookingTotalPrice.setText(String
                    .format("Total price of booking: €%.2f ", booking.getPriceTotal()));
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.booking_confirm_button:
                Log.i("TAG", "Booking confirmed");
                bookingViewModel.createBooking(booking);
                this.dismiss();
                break;
            case R.id.booking_cancel:
                this.dismiss();
                Log.i("TAG", "booking not placed");
                break;
            default:
                break;
        }
    }

}