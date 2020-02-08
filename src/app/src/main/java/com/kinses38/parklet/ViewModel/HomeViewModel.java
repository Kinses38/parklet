package com.kinses38.parklet.ViewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("\n \n \n \n" +"This is a placeholder for upcoming driveway bookings" +
                "\n \n \n \n" +
                "This is a placeholder for upcoming park bookings");
    }


    public LiveData<String> getText() {
        return mText;
    }
}