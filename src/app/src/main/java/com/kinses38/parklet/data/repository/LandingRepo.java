package com.kinses38.parklet.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kinses38.parklet.data.model.entity.User;

/**
 *  Landing repository class to interact with firebase auth and check if user is currently
 *  registered for the app
 */

public class LandingRepo {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user = new User();

    public MutableLiveData<User> queryIfUserIsAuthenticated(){
        MutableLiveData<User> isUserAuthorisedMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseAuthUser = firebaseAuth.getCurrentUser();
        if(firebaseAuthUser == null){
            user.setAuthenticated(false);
            isUserAuthorisedMutableLiveData.setValue(user);
        } else {
            user.setAuthenticated(true);
            user.setUuid(firebaseAuthUser.getUid());
            user.setName(firebaseAuthUser.getDisplayName());
            user.setEmail(firebaseAuthUser.getEmail());
            isUserAuthorisedMutableLiveData.setValue(user);
        }

        return isUserAuthorisedMutableLiveData;
    }
}
