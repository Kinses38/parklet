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
import com.kinses38.parklet.ViewModel.VehiclesViewModel;
import com.kinses38.parklet.data.model.entity.User;

public class VehiclesFragment extends Fragment {

    private VehiclesViewModel vehiclesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vehiclesViewModel = new ViewModelProvider(this).get(VehiclesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //TODO reference to user uuid to query firebase. How to pass?
        //TODO add vehicle with "You have not added a vehicle yet"

        //TODO show current vehicles with option to edit or delete. Listview style collapsible?
        //TODO Does edit/add vehicle happen in place? Or new fragment?
        final TextView textView = root.findViewById(R.id.text_home);

        vehiclesViewModel.getText().observe(getViewLifecycleOwner(), text -> textView.setText(text));
        return root;
    }
}