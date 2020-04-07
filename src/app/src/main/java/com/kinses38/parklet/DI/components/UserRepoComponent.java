package com.kinses38.parklet.DI.components;

import com.kinses38.parklet.DI.modules.RepoModule;
import com.kinses38.parklet.view.ui.fragments.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepoModule.class})
public interface UserRepoComponent {

    void inject(HomeFragment homeFragment);
}
