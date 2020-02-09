package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.R;
import com.kinses38.parklet.ViewModel.HomeViewModel;
import com.kinses38.parklet.data.model.entity.User;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //TODO placeholder for querying firebase;
        Bundle bundle = this.getArguments();
        initProfileView(bundle);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> textView.setText(text));


        return root;
    }

    private void initProfileView(Bundle bundle){
        if(bundle != null){
            User user = (User)bundle.getSerializable("User");
            if(user.checkIsNew()){
                Log.i("HomeFragment", "New user profile: "+user.getName());
                createNewUserProfile(user);
            }else{
                Log.i("Homefragment", "Old user profile:" + user.getName());
                //TODO retrieve user profile
            }
        }
    }

    private void createNewUserProfile(User user){
        homeViewModel.createUserProfile(user);
    }
}