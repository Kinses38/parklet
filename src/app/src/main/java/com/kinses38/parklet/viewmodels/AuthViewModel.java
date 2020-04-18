package com.kinses38.parklet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.data.repository.AuthRepo;

/**
 * Responsible for interacting with Auth Repository for authenticating new users
 * and returning results. Observed by Auth activity.
 * Based on tutorial provided by Alex Mamo
 * https://medium.com/firebase-tips-tricks/how-to-create-a-clean-firebase-authentication-using-mvvm-37f9b8eb7336
 */
public class AuthViewModel extends ViewModel {


    private AuthRepo authRepo;
    private LiveData<User> authenticatedUserLiveData;

    public AuthViewModel() {
        authRepo = new AuthRepo();
    }

    /**
     * Sign in user through firebase Auth and initialise authenticatedUserLiveData to the result.
     *
     * @param authCredential
     */
    public void signInWithGoogle(AuthCredential authCredential) {
        authenticatedUserLiveData = authRepo.firebaseSignin(authCredential);
    }

    /**
     * Get result of authentication operation.
     *
     * @return the livedata containing the users authenticated profile info to be
     * observed by Auth Activity.
     */
    public LiveData<User> getAuthenticatedUserLiveData() {
        return authenticatedUserLiveData;
    }
}
