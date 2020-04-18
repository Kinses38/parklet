package com.kinses38.parklet.DI.components;

import com.kinses38.parklet.DI.modules.RepoModule;
import com.kinses38.parklet.view.ui.fragments.BookingFragment;
import com.kinses38.parklet.view.ui.fragments.ConfirmationFragmentDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepoModule.class})
public interface BookingRepoComponent {

    void inject(BookingFragment bookingFragment);

    void inject(ConfirmationFragmentDialog confirmationFragmentDialog);
}
