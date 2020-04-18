package com.kinses38.parklet.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kinses38.parklet.data.model.entity.User;

/**
 * Landing repository class to interact with firebase auth and check if user is currently
 * registered for the app
 *
 * Based on tutorial provided by Alex Mamo
 * https://medium.com/firebase-tips-tricks/how-to-create-a-clean-firebase-authentication-using-mvvm-37f9b8eb7336
 */

public class LandingRepo {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user = new User();

    /**
     * Checks if user is currently authenticated in firebase Auth.
     *
     * @return the full user account info if authorised, otherwise user
     * object with only authenticated set to false. Subscribed to by Landing ViewModel.
     */
    public MutableLiveData<User> queryIfUserIsAuthenticated() {
        MutableLiveData<User> isUserAuthorisedMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseAuthUser = firebaseAuth.getCurrentUser();
        if (firebaseAuthUser == null) {
            user.setAuthenticated(false);
            isUserAuthorisedMutableLiveData.setValue(user);
        } else {
            user.setAuthenticated(true);
            user.setUid(firebaseAuthUser.getUid());
            user.setName(firebaseAuthUser.getDisplayName());
            user.setEmail(firebaseAuthUser.getEmail());
            isUserAuthorisedMutableLiveData.setValue(user);
        }

        return isUserAuthorisedMutableLiveData;
    }
}
