package com.kinses38.parklet.view.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.ViewModel.LandingViewModel;
import com.kinses38.parklet.data.model.entity.User;

/**
 *  Landing page activity class responsible for sending users to appropriate activity depending
 *  on whether they are authenticated or not. Reroutes unauthenticated users to the AuthActivity
 *  and authenticated ones to their main activity
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

    private void goToAuthActivity(){
        Intent intent = new Intent(LandingActivity.this, AuthActivity.class);
        startActivity(intent);
    }

    private void goToMainActivity(User user){
        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
        //TODO constant
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
