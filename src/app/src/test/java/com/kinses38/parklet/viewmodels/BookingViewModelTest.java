package com.kinses38.parklet.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class BookingViewModelTest {

    BookingRepo bookingRepo = Mockito.mock(BookingRepo.class);
    VehicleRepo vehicleRepo = Mockito.mock(VehicleRepo.class);

    @Mock
    Observer<List<Date>> observer;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    BookingViewModel bookingViewModel = new BookingViewModel(bookingRepo, vehicleRepo);
    static Booking futureBooking;
    static Booking pastBooking;
    static Booking cancelledBookingOne;
    static Booking cancelledBookingTwo;
    static List<Booking> allPropertyBookings;
    static List<Booking> allCancelledBookings;

    @Before
    public void setup() {
        //Booking object one
        long Thurs26March2030 = 1900752610000L;
        List<Long> futureDate = new ArrayList<>();
        futureDate.add(Thurs26March2030);
        //Booking object two
        long sun22March = 1584896528000L;
        List<Long> pastDate = new ArrayList<>();
        pastDate.add(sun22March);

        futureBooking = new Booking();
        futureBooking.setBookingDates(futureDate);
        pastBooking = new Booking();
        pastBooking.setBookingDates(pastDate);

        allPropertyBookings = new ArrayList<>();
        allPropertyBookings.add(futureBooking);
        allPropertyBookings.add(pastBooking);

        cancelledBookingOne = new Booking();
        cancelledBookingOne.setBookingDates(futureDate);
        cancelledBookingOne.setRenterCancelled(true);


        cancelledBookingTwo = new Booking();
        cancelledBookingTwo.setBookingDates(futureDate);
        cancelledBookingTwo.setOwnerCancelled(true);


        allCancelledBookings = new ArrayList<>();
        allCancelledBookings.add(cancelledBookingOne);
        allCancelledBookings.add(cancelledBookingTwo);


//       bookingViewModel.getBookingsForProperty("test").observeForever(observer);
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
    public void convertAndFilterRemoveOldTest(){
        //Removes dates before today's date
        int actual = bookingViewModel.convertAndFilter(allPropertyBookings).size();
        int expected = 1;

        assertEquals("Correct Length",expected, actual);
    }

    @Test
    public void convertAndFilterRemoveCancelledTest(){
        int actual = bookingViewModel.convertAndFilter(allCancelledBookings).size();
        int expected = 0;
        assertEquals("Cancelled Bookings removed", expected, actual);
    }

    //Todo
//    @Test
//    public void getPropertiesNotNullTest(){
//       assertNotNull( bookingViewModel.getBookingsForProperty("test"));
//
//    }

}