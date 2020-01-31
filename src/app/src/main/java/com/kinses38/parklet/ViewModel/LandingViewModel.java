package com.kinses38.parklet.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.data.repository.LandingRepo;

public class LandingViewModel extends AndroidViewModel {

    private LandingRepo landingRepo;
    private LiveData<User> isUserAuthenticatedLiveData;


    public LandingViewModel(Application application){
        super(application);
        landingRepo = new LandingRepo();
    }

    public void checkIfUserIsAuthenticated(){
        isUserAuthenticatedLiveData = landingRepo.queryIfUserIsAuthenticated();
    }

    public LiveData<User> getIsUserAuthenticatedLiveData(){
        return isUserAuthenticatedLiveData;
    }

}
