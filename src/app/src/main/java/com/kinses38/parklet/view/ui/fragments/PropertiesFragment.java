package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kinses38.parklet.R;

public class PropertiesFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_properties,container, false);

        final TextView textView = root.findViewById(R.id.text_properties);
        textView.setText("This is the properties view");

        return root;
    }
}
