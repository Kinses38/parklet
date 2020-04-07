package com.kinses38.parklet.DI.modules;

import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.PropertyRepo;
import com.kinses38.parklet.data.repository.UserRepo;
import com.kinses38.parklet.data.repository.VehicleRepo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Responsible for providing singleton instances of repositories to be reused for the lifetime of
 * the application. If one exists already it is shared amongst viewmodels which require it.
 */
@Module
public class RepoModule {

    @Singleton
    @Provides
    BookingRepo providesBookingRepo() {
        return new BookingRepo();
    }

    @Singleton
    @Provides
    VehicleRepo vehicleRepo() {
        return new VehicleRepo();
    }

    @Singleton
    @Provides
    PropertyRepo propertyRepo() {
        return new PropertyRepo();
    }

    @Singleton
    @Provides
    UserRepo userRepo() {
        return new UserRepo();
    }
}
