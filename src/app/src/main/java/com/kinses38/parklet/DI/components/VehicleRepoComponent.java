package com.kinses38.parklet.DI.components;

import com.kinses38.parklet.DI.modules.RepoModule;
import com.kinses38.parklet.view.ui.fragments.BookingFragment;
import com.kinses38.parklet.view.ui.fragments.ConfirmationFragmentDialog;
import com.kinses38.parklet.view.ui.fragments.VehiclesFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepoModule.class})
public interface VehicleRepoComponent {

    void inject(BookingFragment bookingFragment);
    void inject(ConfirmationFragmentDialog confirmationFragmentDialog);
    void inject(VehiclesFragment vehiclesFragment);
}
