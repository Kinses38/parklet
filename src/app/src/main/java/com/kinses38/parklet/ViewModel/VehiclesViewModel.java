package com.kinses38.parklet.ViewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

public class VehiclesViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;

    public VehiclesViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("You can view, edit and delete vehicles here");
    }


    public LiveData<String> getText() {
        return mText;
    }
}