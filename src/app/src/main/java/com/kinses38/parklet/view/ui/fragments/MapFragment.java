package com.kinses38.parklet.view.ui.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.databinding.FragmentMapBinding;
import com.kinses38.parklet.viewmodels.MapViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final String TAG = this.getClass().getSimpleName();

    private MapView mapView;
    private GoogleMap map;
    private FragmentMapBinding mapBinding;
    private MapViewModel mapViewModel;
    private Address address;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        mapViewModel = new ViewModelProvider(getActivity()).get(MapViewModel.class);
        mapBinding.setLifecycleOwner(this);

        /*

          Map tutorial
          https://developers.google.com/maps/documentation/android-sdk/map-with-marker
          https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2

         */

        mapView = mapBinding.mapBlock;
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        //Has to be before observer or provide defaults
        getGeo();
        initObserver();
        return mapBinding.getRoot();
    }

    private void initObserver() {
        //observe this with defaults used by user location????
        mapViewModel.getPropertiesInRange(address.getLongitude(), address.getLatitude(), 15).observe(getViewLifecycleOwner(), new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                if(!properties.isEmpty()){
                    for(Property property : properties){
                        Log.i(TAG, "Properties found nearby: " + property.getAddressLine());
                    }
                }
            }
        });
    }


    //TODO placeholder for now. Needs a thread. Need progress
    private void getGeo() {
        //Need to add suffix Ireland for general queries to work. Might be solved with querying user location and storing.
        // What happens if user is searching for properties abroad though? Useless use case?
        String addressText = "Swords" + "Ireland";
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List addressList = geocoder.getFromLocationName(addressText, 1);
            if (!addressList.isEmpty()) {
                Log.i(TAG, "Address: " + addressList.toString());
                address = (Address) addressList.get(0);
                Toast.makeText(getActivity(), String.format("Searching for %s", addressText), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), String.format("Cannot find address %s", addressText), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "GeoCode failed for address: " + addressText);
        }
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
    //DONE Convert area to long/lat (GeoCode)
    //DONE Query firebase for nearby properties within that range
    //DONE Get the properties that match
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
