package com.kinses38.parklet.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;
import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.data.repository.AuthRepo;

public class AuthViewModel extends AndroidViewModel {

    //TODO refactor to contain authactivity logic for signin events
    private AuthRepo authRepo;
    private LiveData<User> authenticatedUserLiveData;

    public AuthViewModel(Application application){
        super(application);
        authRepo = new AuthRepo();
    }

    public void signInWithGoogle(AuthCredential authCredential){
        authenticatedUserLiveData = authRepo.firebaseSignin(authCredential);
    }

    public LiveData<User> getAuthenticatedUserLiveData() {
        return authenticatedUserLiveData;
    }
}
