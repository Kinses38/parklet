package com.kinses38.parklet.utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.UserRepo;
import com.kinses38.parklet.databinding.BookingRecyclerLayoutBinding;
import com.kinses38.parklet.viewmodels.HomeViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private Activity context;
    private List<Booking> bookings;
    private HomeViewModel homeViewModel;


    static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView rv_houseAddress, rv_total_price, rv_booking_dates, rv_owner_name, rv_renter_name,
                rv_booked_car;
        ImageButton rv_cancel_booking, rv_directions_button;
        FirebaseAuth ADB = FirebaseAuth.getInstance();
        BookingRecyclerLayoutBinding binding;

        HomeViewHolder(BookingRecyclerLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            rv_houseAddress = binding.rvHouseAddress;
            rv_total_price = binding.rvTotalPrice;
            rv_booking_dates = binding.rvBookingDates;
            rv_owner_name = binding.rvOwnerName;
            rv_renter_name = binding.rvRenterName;
            rv_cancel_booking = binding.rvCancelBooking;
            rv_directions_button = binding.rvDirectionsButton;
            rv_booked_car = binding.rvBookedCar;
            binding.setCurrentUser(ADB.getCurrentUser().getDisplayName());
            binding.executePendingBindings();
        }

        //Need this to dynamically update bindings for each property
        public void bind(Booking booking) {
            binding.setBooking(booking);
            if(booking.isOwnerCancelled() || booking.isRenterCancelled())
            {
                binding.setBookingCancelled(true);
            }
            binding.executePendingBindings();
        }
    }

    public HomeAdapter(Activity context) {
        this.context = context;
        this.bookings = new ArrayList<>();
        ViewModelFactory viewModelFactory = new ViewModelFactory(new UserRepo(), new BookingRepo());
        homeViewModel = new ViewModelProvider((FragmentActivity) context, viewModelFactory)
                .get(HomeViewModel.class);

    }

    @NonNull
    @Override
    public HomeAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        BookingRecyclerLayoutBinding binding = BookingRecyclerLayoutBinding
                .inflate(layoutInflater, parent, false);
        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.HomeViewHolder viewHolder, int position) {
        Booking booking = bookings.get(position);
        viewHolder.bind(booking);
        viewHolder.rv_houseAddress.setText(String
                .format("%s \n%s", context.getText(R.string.house_address), booking
                        .getPropertyAddress()));
        viewHolder.rv_total_price.setText(String
                .format("%s %.2f", context.getText(R.string.price_total), booking.getPriceTotal()));
        viewHolder.rv_booking_dates.setText(String
                .format("%s \n%s", context.getText(R.string.days_booked), formatDates(booking
                        .getBookingDates())));
        viewHolder.rv_owner_name.setText(String
                .format("%s %s", context.getText(R.string.property_owner), booking.getOwnerName()));
        viewHolder.rv_renter_name.setText(String
                .format("%s %s", context.getText(R.string.renter_name), booking.getRenterName()));
        viewHolder.rv_booked_car.setText(String
                .format("%s %s", context.getText(R.string.car_booked), booking
                        .getRenterVehicleReg()));
        viewHolder.rv_cancel_booking.setOnClickListener(v ->
                homeViewModel.cancelBooking(viewHolder.ADB.getCurrentUser().getUid(), booking));

        viewHolder.rv_directions_button.setOnClickListener(v ->
                showDirections(booking.getPropertyAddress()));
    }

    //https://developers.google.com/maps/documentation/urls/android-intents
    private void showDirections(@NonNull String address){
        Uri addressUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent gmapsIntent = new Intent(Intent.ACTION_VIEW, addressUri);
        gmapsIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(gmapsIntent);
    }

    public void refreshList(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    private String formatDates(List<Long> dates) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM");
        String daysBooked = dates.stream().map(Date::new).map(format::format)
                .collect(Collectors.joining(",\n"));
        return daysBooked;
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }
}
