package com.kinses38.parklet.view.ui.fragments;

import android.annotation.SuppressLint;
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

import com.kinses38.parklet.ParkLet;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.databinding.FragmentConfirmationDialogBinding;
import com.kinses38.parklet.viewmodels.BookingViewModel;
import com.kinses38.parklet.viewmodels.ViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 *  Shows a summary of the booking the user created or errors
 */
public class ConfirmationFragmentDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private TextView bookingErrors, bookingTotalDays, bookingTotalPrice, bookingSummaryText;
    private FragmentConfirmationDialogBinding binding;
    private Booking booking;
    private BookingViewModel bookingViewModel;
    private boolean errorState;
    @Inject
    ViewModelFactory viewModelFactory;

    static ConfirmationFragmentDialog newInstance() {
        ConfirmationFragmentDialog dialog = new ConfirmationFragmentDialog();
        return dialog;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_confirmation_dialog, container, true);
        ParkLet.getParkLetApp().getVehicleRepoComponent().inject(this);
        ParkLet.getParkLetApp().getBookingRepoComponent().inject(this);
        bookingViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(BookingViewModel.class);
        initBinding();
        booking = bookingViewModel.getBooking();

        validateBooking(booking);
        bookingSummary(booking);
        return binding.getRoot();

    }

    /**
     *  Binds any errors in the booking, no vehicle or dates selected.
     *  Binds total price, days booked and address of property.
     */
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

        Log.i(TAG, errors);
    }

    /**
     * Formating dates to human readable form for summary.
     * @param dates objects belonging to candidate booking
     * @return String formatted dates.
     */
    private String summariseDates(List<Long> dates) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM");
        String daysBooked = dates.stream().map(Date::new).map(format::format)
                .collect(Collectors.joining(",\n"));
        return daysBooked;
    }

    /**
     * Binds string summary of dates and price of booking to the textview, IFF no errors are present.
     * @param booking the candidate booking
     */
    @SuppressLint("SetTextI18n")
    private void bookingSummary(Booking booking) {
        if (!errorState) {
            bookingSummaryText.setText(R.string.summaryText);
            bookingTotalDays.setText(getString(R.string.totalDaysBooked) + summariseDates(booking.getBookingDates()));
            bookingTotalPrice.setText(String.format(getString(R.string.total_price), booking.getPriceTotal()));
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.booking_confirm_button:
                Log.i(TAG, "Booking confirmed");
                bookingViewModel.createBooking(booking);
                this.dismiss();
                break;
            case R.id.booking_cancel:
                this.dismiss();
                Log.i(TAG, "booking not placed");
                break;
            default:
                break;
        }
    }

}