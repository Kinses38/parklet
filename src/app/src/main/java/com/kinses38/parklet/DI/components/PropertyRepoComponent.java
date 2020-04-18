package com.kinses38.parklet.DI.components;

import com.kinses38.parklet.DI.modules.RepoModule;
import com.kinses38.parklet.view.ui.activities.MainActivity;
import com.kinses38.parklet.view.ui.fragments.MapFragment;
import com.kinses38.parklet.view.ui.fragments.PropertiesFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepoModule.class})
public interface PropertyRepoComponent {

    void inject(MainActivity mainActivity);

    void inject(PropertiesFragment propertiesFragment);

    void inject(MapFragment mapFragment);
}
