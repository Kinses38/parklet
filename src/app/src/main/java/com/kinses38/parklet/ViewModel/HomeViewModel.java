package com.kinses38.parklet.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.data.repository.UserRepo;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private UserRepo userRepo = new UserRepo();

    public HomeViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to ParkLet");
    }


    public LiveData<String> getText() {
        return mText;
    }

    public void createUserProfile(User user){
        userRepo.setNewUser(user);
    }
}