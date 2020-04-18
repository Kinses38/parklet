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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kinses38.parklet.ParkLet;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.databinding.FragmentHomeBinding;
import com.kinses38.parklet.utilities.HomeAdapter;
import com.kinses38.parklet.viewmodels.HomeViewModel;
import com.kinses38.parklet.viewmodels.ViewModelFactory;

import javax.inject.Inject;

/**
 *  Home page of app. First page loaded if user is successfully authenticated.
 *  Allows creation of new vehicle bookings and management of current bookings for home owners and
 *  renters.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private TextView textView;
    private HomeViewModel homeViewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private HomeAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate through Databinding util to allow use of databinding in layout files.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        ParkLet.getParkLetApp().getUserRepoComponent().inject(this);
        //provide ViewModel through injected ViewModel Factory.
        homeViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(HomeViewModel.class);

        Bundle bundle = this.getArguments();
        initBindings();
        initRecyclerView();
        initProfileView(bundle);
        observerUsersBookings();

        return binding.getRoot();
    }

    /**
     * Set relevant bindings for Home Fragment layout file.
     * Set context of fragment to this.
     * Set HasBookings to false to hide RecyclerView until bookings are loaded/found.
     */
    private void initBindings() {
        textView = binding.textHome;
        binding.setHomeFrag(this);
        binding.setHasBookings(false);
    }

    /**
     *  Initialise recyclerview for display of current Bookings.
     */
    private void initRecyclerView() {
        adapter = new HomeAdapter(getActivity());
        recyclerView = binding.homeRecycler;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }


    /**
     * Fetch signed in user from bundle and if they are new start process of creating new profile.
     * Updates the Firebase Cloud Messaging token in the case of an app uninstall or the token being revoked.
     * @param bundle
     */
    private void initProfileView(Bundle bundle) {
        if (bundle != null) {
            User user = (User) bundle.getSerializable("User");
            if(user != null){
                String welcome = getString(R.string.user_welcome, user.getFirstName());
                if (user.checkIsNew()) {
                    Log.i(TAG, "New user profile: " + user.getName());
                    textView.setText(welcome);
                    createNewUserProfile(user);
                } else {
                    textView.setText(welcome);
                    Log.i(TAG, "Old user profile:" + user.getName());
                }
                getCurrentFcmToken(user);
            }
        }
    }

    /**
     * If new user then call for the ViewModel to initialise their profile through the user Repo.
     * @param user
     */
    private void createNewUserProfile(User user) {
        homeViewModel.createUserProfile(user);
    }

    /**
     * Get the most up to date device token for firebase cloud messaging and associate with users profile.
     * @param user
     */
    //https://firebase.google.com/docs/cloud-messaging/android/client#retrieve-the-current-registration-token
    private void getCurrentFcmToken(User user) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            String fcmToken = task.getResult().getToken();
            user.setFcmToken(fcmToken);
            homeViewModel.updateUserFcmToken(user);
        });
    }

    /**
     * Fetch all users bookings, both vehicle and properties and pass to recycler.
     * setHasBookings updated so layout file knows whether to display the recycler
     * if new bookings have arrived.
     */
    private void observerUsersBookings() {
        homeViewModel.getUsersBookings().observe(getViewLifecycleOwner(), bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                Log.i(TAG, "Bookings found");
                adapter.refreshList(bookings);
                recyclerView.setAdapter(adapter);
                binding.setHasBookings(true);
            } else {
                binding.setHasBookings(false);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_booking_button:
                Log.i(TAG, "create booking button clicked");
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_nav_home_to_nav_map);
                break;
            default:
                break;
        }

    }
}