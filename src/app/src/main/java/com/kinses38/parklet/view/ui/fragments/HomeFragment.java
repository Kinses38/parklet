package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.kinses38.parklet.R;
import com.kinses38.parklet.databinding.FragmentHomeBinding;
import com.kinses38.parklet.viewmodels.HomeViewModel;
import com.kinses38.parklet.data.model.entity.User;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextView textView;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding fragmentHomeBinding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        Bundle bundle = this.getArguments();

        //TODO button to create booking, block until user has a vehicle? Where to implement the check?

        //TODO get all current bookings for user.
        // Separate them into two different recyclerview? Or allow filter between property bookings and car bookings?

        initBindings();
        initProfileView(bundle);

        return fragmentHomeBinding.getRoot();
    }

    private void initBindings() {
        textView = fragmentHomeBinding.textHome;
        fragmentHomeBinding.setHomeFrag(this);
    }

    private void initProfileView(Bundle bundle) {
        if (bundle != null) {
            User user = (User) bundle.getSerializable("User");
            String welcome = getString(R.string.user_welcome, user.getFirstName());
            if (user.checkIsNew()) {
                //TODO tags
                Log.i("HomeFragment", "New user profile: " + user.getName());
                textView.setText(welcome);
                createNewUserProfile(user);
            } else {
                textView.setText(welcome);
                Log.i("Homefragment", "Old user profile:" + user.getName());
            }
        }
    }

    private void createNewUserProfile(User user) {
        homeViewModel.createUserProfile(user);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_booking_button:
                Log.i("Home", "create booking button clicked");
                //TODO do we need burger menu once we go to map view?
                Navigation.findNavController(fragmentHomeBinding.getRoot()).navigate(R.id.action_nav_home_to_nav_map);
                break;
            default:
                break;
        }

    }
}