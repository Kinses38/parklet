package com.kinses38.parklet;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.kinses38.parklet.DI.components.BookingRepoComponent;
import com.kinses38.parklet.DI.components.DaggerBookingRepoComponent;
import com.kinses38.parklet.DI.components.DaggerPropertyRepoComponent;
import com.kinses38.parklet.DI.components.DaggerUserRepoComponent;
import com.kinses38.parklet.DI.components.DaggerVehicleRepoComponent;
import com.kinses38.parklet.DI.components.PropertyRepoComponent;
import com.kinses38.parklet.DI.components.UserRepoComponent;
import com.kinses38.parklet.DI.components.VehicleRepoComponent;

import static android.os.Build.DEVICE;

public class ParkLet extends Application {

    private static ParkLet parkLetApp;
    private BookingRepoComponent bookingRepoComponent;
    private VehicleRepoComponent vehicleRepoComponent;
    private PropertyRepoComponent propertyRepoComponent;
    private UserRepoComponent userRepoComponent;

    /**
     *  Override App instance and use as entry point to ParkLet App. Enables firebase auth &
     *  DB instance to be shared at app level and, firebase caching through persistence enabled.
     *  Dagger 2 components made available at app level through static ParkletApp instance for dependency injection
     */
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        //Disable persistence for testing.
        String TESTMODE = "robolectric";
        if(!DEVICE.equals(TESTMODE)){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        parkLetApp = this;

        bookingRepoComponent = DaggerBookingRepoComponent.builder().build();
        vehicleRepoComponent = DaggerVehicleRepoComponent.builder().build();
        propertyRepoComponent = DaggerPropertyRepoComponent.builder().build();
        userRepoComponent = DaggerUserRepoComponent.builder().build();

    }

    public static ParkLet getParkLetApp() {
        return parkLetApp;
    }

    public BookingRepoComponent getBookingRepoComponent() {
        return bookingRepoComponent;
    }

    public VehicleRepoComponent getVehicleRepoComponent() {
        return vehicleRepoComponent;
    }

    public PropertyRepoComponent getPropertyRepoComponent() {
        return propertyRepoComponent;
    }

    public UserRepoComponent getUserRepoComponent() {
        return userRepoComponent;
    }
}
