package com.kinses38.parklet.view.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.viewmodels.LandingViewModel;
import com.kinses38.parklet.data.model.entity.User;

/**
 *  Landing page activity class responsible for sending users to appropriate activity depending
 *  on whether they are authenticated or not. Reroutes unauthenticated users to the AuthActivity
 *  and authenticated ones to the main activity.
 *
 *  Based on tutorial provided by Alex Mamo
 *  https://medium.com/firebase-tips-tricks/how-to-create-a-clean-firebase-authentication-using-mvvm-37f9b8eb7336
 */

public class LandingActivity extends AppCompatActivity {

    private LandingViewModel landingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initLandingViewModel();
        checkIfUserIsAuthenticated();
    }

    private void initLandingViewModel(){
        landingViewModel = new ViewModelProvider(this).get(LandingViewModel.class);

    }

    /**
     * Observes the result of whether the user is authenticated through livedata provided by the
     * Landing ViewModel. If the user is authenticated start an intent to launch main activity,
     * otherwise an intent to launch auth Activity.
     */
    private void checkIfUserIsAuthenticated(){
       landingViewModel.checkIfUserIsAuthenticated();
       landingViewModel.getIsUserAuthenticatedLiveData().observe(this, user ->{
           if(!user.checkIsAuthenticated()){
               goToAuthActivity();
               finish();
           } else {
               goToMainActivity(user);
           }
       });
    }

    /**
     *  User has not yet been authenticated so start intent to allow them to sign in.
     */
    private void goToAuthActivity(){
        Intent intent = new Intent(LandingActivity.this, AuthActivity.class);
        startActivity(intent);
    }

    /**
     * User has authentication was successful, start main activity, passing the user as serialized extra
     * @param user passed to create new ParkLet user if this is their first time using the app.
     */
    private void goToMainActivity(User user){
        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
        //TODO constant
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
