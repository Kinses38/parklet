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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.UserRepo;
import com.kinses38.parklet.databinding.FragmentHomeBinding;
import com.kinses38.parklet.utilities.HomeAdapter;
import com.kinses38.parklet.utilities.ParkLetFirebaseMessagingService;
import com.kinses38.parklet.utilities.ViewModelFactory;
import com.kinses38.parklet.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private TextView textView;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ViewModelFactory viewModelFactory;
    private ParkLetFirebaseMessagingService messagingService;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private HomeAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewModelFactory = new ViewModelFactory(new UserRepo(), new BookingRepo());
        homeViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(HomeViewModel.class);

        Bundle bundle = this.getArguments();
        initBindings();
        initRecyclerView();
        initProfileView(bundle);
        observerUsersBookings();

        return binding.getRoot();
    }

    private void initBindings() {
        textView = binding.textHome;
        binding.setHomeFrag(this);
        binding.setHasBookings(false);
    }

    private void initRecyclerView() {
        adapter = new HomeAdapter(getActivity());
        recyclerView = binding.homeRecycler;
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    //Todo refactor out to viewmodel
    private void initProfileView(Bundle bundle) {
        if (bundle != null) {
            User user = (User) bundle.getSerializable("User");
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

    private void createNewUserProfile(User user) {
        homeViewModel.createUserProfile(user);
    }

    //https://firebase.google.com/docs/cloud-messaging/android/client#retrieve-the-current-registration-token
    private void getCurrentFcmToken(User user) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            String fcmToken = task.getResult().getToken();
            user.setFcmToken(fcmToken);
            homeViewModel.updateUserFcmToken(user);
        });
    }


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