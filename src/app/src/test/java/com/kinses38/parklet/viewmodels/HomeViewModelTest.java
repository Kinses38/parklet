package com.kinses38.parklet.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.kinses38.parklet.data.model.entity.Booking;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.UserRepo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private BookingRepo bookingRepo = Mockito.mock(BookingRepo.class);
    @Mock
    private UserRepo userRepo = Mockito.mock(UserRepo.class);
    private HomeViewModel homeViewModel;

    @Mock
    Observer<String> stringObserver;

    @Mock
    Observer<List<Booking>> bookingListObserver;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        homeViewModel = new HomeViewModel(userRepo, bookingRepo);
    }

    @Test
    public void updateCheckInStatusTest() {
        MutableLiveData<String> response = new MutableLiveData<>("You are booked in");
        when(bookingRepo.updateCheckInStatus(anyString())).thenReturn(response);
        homeViewModel.updateCheckInStatus(anyString()).observeForever(stringObserver);
        verify(stringObserver).onChanged("You are booked in");
    }

    @Test
    public void cancelBookingTest() {
        String currentUserID = "1234";
        Booking ownerCancelledBooking = new Booking();
        ownerCancelledBooking.setOwnerUID(currentUserID);
        homeViewModel.cancelBooking(currentUserID, ownerCancelledBooking);
        assertTrue("Owner cancelled", ownerCancelledBooking.isOwnerCancelled());
        assertFalse("Renter did not cancel", ownerCancelledBooking.isRenterCancelled());

        Booking renterCancelledBooking = new Booking();
        renterCancelledBooking.setRenterUID(currentUserID);
        homeViewModel.cancelBooking(currentUserID, renterCancelledBooking);
        assertTrue("Renter cancelled", renterCancelledBooking.isRenterCancelled());
        assertFalse("Owner did not cancel", renterCancelledBooking.isOwnerCancelled());

    }

    @Test
    public void mergeBookingsLiveDataTest() {
        Booking bookingOne = new Booking();
        Booking bookingTwo = new Booking();

        List<Booking> ownerBookings = new ArrayList<>();
        List<Booking> renterBookings = new ArrayList<>();

        ownerBookings.add(bookingOne);
        renterBookings.add(bookingTwo);

        MutableLiveData<List<Booking>> ownerLiveList = new MutableLiveData<>(ownerBookings);
        MutableLiveData<List<Booking>> renterLiveList = new MutableLiveData<>(renterBookings);

        List<Booking> combinedList;
        combinedList = homeViewModel.mergeBookingsLiveData(ownerLiveList, renterLiveList);
        assertThat("Size is correct", combinedList.size() == 2);
        assertTrue(combinedList.contains(bookingTwo));
        assertTrue(combinedList.contains(bookingOne));

    }

    @Test
    public void getUsersBookingsTest() {
        Booking bookingOne = new Booking();
        Booking bookingTwo = new Booking();

        List<Booking> ownerBookings = new ArrayList<>();
        List<Booking> renterBookings = new ArrayList<>();

        ownerBookings.add(bookingOne);
        renterBookings.add(bookingTwo);

        MutableLiveData<List<Booking>> ownerLiveList = new MutableLiveData<>(ownerBookings);
        MutableLiveData<List<Booking>> renterLiveList = new MutableLiveData<>(renterBookings);


        when(bookingRepo.selectAllUserRentals()).thenReturn(renterLiveList);
        when(bookingRepo.selectAllUsersPropertiesBooking()).thenReturn(ownerLiveList);
        homeViewModel.getUsersBookings().observeForever(bookingListObserver);


        ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
        verify(bookingListObserver, times(2)).onChanged(arg.capture());
        List<Booking> result = arg.getValue();
        assertThat(result, containsInAnyOrder(bookingOne, bookingTwo));
    }

}
