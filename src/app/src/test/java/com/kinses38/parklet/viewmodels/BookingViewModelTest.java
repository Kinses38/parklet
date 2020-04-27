package com.kinses38.parklet.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.model.entity.Vehicle;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class BookingViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private BookingRepo bookingRepo = Mockito.mock(BookingRepo.class);
    private VehicleRepo vehicleRepo = Mockito.mock(VehicleRepo.class);

    @Mock
    Observer<List<Date>> dateObserver;

    @Mock
    Observer<List<Vehicle>> vehicleObserver;

    private BookingViewModel bookingViewModel;
    private static List<Booking> allPropertyBookings;
    private static List<Booking> allCancelledBookings;

    @Before
    public void setup() {
        //Future booking
        long Thurs26March2030 = 1900752610000L;
        List<Long> futureDate = new ArrayList<>();
        futureDate.add(Thurs26March2030);
        //Past booking
        long sun22March = 1584896528000L;
        List<Long> pastDate = new ArrayList<>();
        pastDate.add(sun22March);

        Booking futureBooking = new Booking();
        futureBooking.setBookingDates(futureDate);
        Booking pastBooking = new Booking();
        pastBooking.setBookingDates(pastDate);

        allPropertyBookings = new ArrayList<>();
        allPropertyBookings.add(futureBooking);
        allPropertyBookings.add(pastBooking);

        Booking cancelledBookingOne = new Booking();
        cancelledBookingOne.setBookingDates(futureDate);
        cancelledBookingOne.setRenterCancelled(true);


        Booking cancelledBookingTwo = new Booking();
        cancelledBookingTwo.setBookingDates(futureDate);
        cancelledBookingTwo.setOwnerCancelled(true);


        allCancelledBookings = new ArrayList<>();
        allCancelledBookings.add(cancelledBookingOne);
        allCancelledBookings.add(cancelledBookingTwo);

        MockitoAnnotations.initMocks(this);
        bookingViewModel = new BookingViewModel(bookingRepo, vehicleRepo);

    }


    @Test
    public void convertAndFilterNotNullTest() {
        assertNotNull("Not null", bookingViewModel.convertAndFilter(allPropertyBookings));
    }

    @Test
    public void filteredDateIsEqualTest() {
        //Correctly converts timestamp to date
        Date actual = bookingViewModel.convertAndFilter(allPropertyBookings).get(0);
        Date expected = new Date(1900752610000L);

        assertEquals("Correct Date conversion:", expected, actual);
    }

    @Test
    public void convertAndFilterRemoveOldTest() {
        //Removes dates before today's date
        int actual = bookingViewModel.convertAndFilter(allPropertyBookings).size();
        int expected = 1;

        assertEquals("Correct Length", expected, actual);
    }

    @Test
    public void convertAndFilterRemoveCancelledTest() {
        int actual = bookingViewModel.convertAndFilter(allCancelledBookings).size();
        int expected = 0;
        assertEquals("Cancelled Bookings removed", expected, actual);
    }

    @Test
    public void getUserVehiclesTest() {
        Vehicle vehicle = new Vehicle("Ford", "Focus", "98-D-1234");
        List vehicleList = new ArrayList();
        vehicleList.add(vehicle);
        MutableLiveData<List<Vehicle>> vehiclesList = new MutableLiveData<>(vehicleList);
        //ViewModel expecting MutableLiveData list of vehicles back.
        when(vehicleRepo.selectAll()).thenReturn(vehiclesList);
        bookingViewModel.getUserVehicles().observeForever(vehicleObserver);
        //Observer expecting just a list of vehicles.
        verify(vehicleObserver, description("Failed to get user vehicles")).onChanged(vehicleList);
    }

    @Test
    public void getBookingDatesTest() {
        List dates = new ArrayList();
        Date expected = new Date(1900752610000L);
        dates.add(expected);
        MutableLiveData<List<Booking>> mutableLiveBookingList = new MutableLiveData<>(allPropertyBookings);
        when(bookingRepo.selectAllForProperty("TestPropertyUID")).thenReturn(mutableLiveBookingList);
        bookingViewModel.getBookingsForProperty("TestPropertyUID").observeForever(dateObserver);
        verify(dateObserver, description("Failed to get booking dates and convert")).onChanged(dates);
    }

    @Test
    public void createBookingTest() {
        bookingViewModel.setBookingStatus(false);
        assertFalse("Booking status should be false", bookingViewModel.getBookingStatus().getValue());
        Booking testBooking = new Booking();
        bookingViewModel.createBooking(testBooking);
        assert (bookingViewModel.getBookingStatus().getValue());
    }

    @Test
    public void setBookingDetails() {
        Booking booking = new Booking();
        booking.setBookingUID("1234");
        bookingViewModel.setBookingDetails(booking);
        assertNotNull("Booking should be set for summary", bookingViewModel.getBooking());
        assertEquals("Correct booking should be set", bookingViewModel.getBooking(), booking);
    }

}