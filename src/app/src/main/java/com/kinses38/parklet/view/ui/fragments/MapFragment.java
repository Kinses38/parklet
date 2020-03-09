package com.kinses38.parklet.view.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.kinses38.parklet.R;
import com.kinses38.parklet.databinding.FragmentMapBinding;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private FragmentMapBinding mapBinding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        /*
          Map tutorial
          https://developers.google.com/maps/documentation/android-sdk/map-with-marker
          https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
         */

        mapView = mapBinding.mapBlock;
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
        return mapBinding.getRoot();
    }


    // required to notify above callback mapview is ready.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        //TODO fix permissions issue;
//        map.setMyLocationEnabled(true);
        //Placeholder coords
        //TODO zoom level should be balanced by the range radius a user provides in their search?
        // The map should default to their position showing properties nearby? Or is that too intensive to start off?
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.463314, -6.231833), 10));


    }



    // Get area and range input from user. 2 x Edit Texts. Button or watcher?
    // Convert area to long/lat (GeoCode)
    // Query firebase for nearby properties within that range
    // Get the properties that match
    // Process them for markers for maps
    // move map camera to area with zoom
    // populate recyclerview with properties.


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
