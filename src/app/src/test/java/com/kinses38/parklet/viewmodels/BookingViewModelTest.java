package com.kinses38.parklet.viewmodels;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BookingViewModelTest {

    BookingRepo bookingRepo = Mockito.mock(BookingRepo.class);
    VehicleRepo vehicleRepo = Mockito.mock(VehicleRepo.class);

    BookingViewModel bookingViewModel = new BookingViewModel(bookingRepo, vehicleRepo);
    static Booking bookingOne;
    static Booking bookingTwo;
    static List<Booking> allPropertyBookings;

    @Before
    public void setup() {
        //Booking object one
        long wed25March = 1585094400000L;
        List<Long> bookingDatesOne = new ArrayList<>();
        bookingDatesOne.add(wed25March);
        //Booking object two
        long sun22March = 1584896528000L;
        List<Long> bookingDatesTwo = new ArrayList<>();
        bookingDatesTwo.add(sun22March);


        bookingOne = new Booking();
        bookingOne.setBookingDates(bookingDatesOne);
        bookingTwo = new Booking();
        bookingTwo.setBookingDates(bookingDatesTwo);


        allPropertyBookings = new ArrayList<>();
        allPropertyBookings.add(bookingOne);
        allPropertyBookings.add(bookingTwo);
    }


    @Test
    public void convertAndFilterTest(){
        assertNotNull(bookingViewModel.convertAndFilter(allPropertyBookings));
        assertEquals("Correct Length",1,bookingViewModel.convertAndFilter(allPropertyBookings).size());
        assertEquals("Correct Date", new Date(1585094400000L), bookingViewModel.convertAndFilter(allPropertyBookings).get(0));

    }


}