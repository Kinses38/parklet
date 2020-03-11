package com.kinses38.parklet.view.ui.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.databinding.FragmentMapBinding;
import com.kinses38.parklet.utilities.InputHandler;
import com.kinses38.parklet.viewmodels.MapViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private MapView mapView;
    private GoogleMap map;
    private FragmentMapBinding mapBinding;
    private MapViewModel mapViewModel;

    private TextView mapSearchInput, mapRangeInput;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        mapViewModel = new ViewModelProvider(getActivity()).get(MapViewModel.class);
        mapBinding.setLifecycleOwner(this);
        initBindings();

        /*
          Map tutorial
          https://developers.google.com/maps/documentation/android-sdk/map-with-marker
          https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
         */

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        //Has to be before observer or provide defaults
        //Call geo after submit, then call initObserver with parameters?
        getGeo();
        return mapBinding.getRoot();
    }

    private void initObserver(LatLng latLng, Double range) {
        //observe this with defaults used by user location????
        mapViewModel.getPropertiesInRange(latLng.longitude, latLng.latitude, range).observe(getViewLifecycleOwner(), new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                if (!properties.isEmpty()) {
                    updateMap(properties, latLng);
                }else{
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    Toast.makeText(getActivity(), "No properties in this area, try expanding your Search", Toast.LENGTH_LONG).show();
                    //TODO handle not found case
                }
            }
        });
    }

    private void initBindings() {
        mapView = mapBinding.mapBlock;
        mapBinding.setMapFrag(this);
        mapBinding.setFormClicked(false);
        mapSearchInput = mapBinding.mapSearchInput;
        mapRangeInput = mapBinding.mapRangeInput;
    }


    //Variadic method, takes 0 or more arguments, 0 or 1 in this case
    private LatLng getGeo(String... areaToSearch) {
        //Need to add suffix Ireland for general queries to work. Might be solved with querying user location and storing.
        LatLng latLng;
        String addressText = " Ireland";
        if (areaToSearch.length != 0) {
            addressText = areaToSearch[0] + addressText;
        }
        try {
            Toast.makeText(getActivity(), String.format("Searching for properties in %s", addressText), Toast.LENGTH_SHORT).show();
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List addressList = geocoder.getFromLocationName(addressText, 1);
            if (!addressList.isEmpty()) {
                Log.i(TAG, "Address: " + addressList.toString());
                Address address = (Address) addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                return latLng;
            } else {
                Toast.makeText(getActivity(), String.format("Cannot find area %s", addressText), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "GeoCode failed for area: " + addressText);
        }
        return null;
    }


    // required to notify above callback mapView is ready.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        //TODO fix permissions issue;
//        map.setMyLocationEnabled(true);
        // The map should default to their position showing properties nearby? Or is that too intensive to start off?
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.463314, -6.231833), 10));


    }


    private void updateMap(List<Property> properties, LatLng latLng) {
        //clear all currently existing markers
        map.clear();
        List<MarkerOptions> markers = createMarkers(properties);
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        for(MarkerOptions marker: markers){
            map.addMarker(marker.visible(true));
        //TODO get upper bounds for camera here
        }
    }


    private List<MarkerOptions> createMarkers(List<Property> properties){
        List<MarkerOptions> propertyMarkers= new ArrayList<>();
        for(Property property: properties){
            String [] brokenAddress = property.getAddressLine().split(",");
            propertyMarkers.add(new MarkerOptions()
                    .position(new LatLng(property.getLatitude(), property.getLongitude()))
                    .title(String.format("%s, %s", brokenAddress[0], brokenAddress[1]))
                    .snippet(String.format(Locale.getDefault(),"%s %.2f", getString(R.string.daily_rate) , property.getDailyRate()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .draggable(false)
                    .visible(false));

        }
        return propertyMarkers;
    }

    //DONE Get area and range input from user. 2 x Edit Texts. Button or watcher?
    //DONE Convert area to long/lat (GeoCode)
    //DONE Query firebase for nearby properties within that range
    //DONE Get the properties that match
    //DONE Process them for markers for maps
    //DONE Function to update and move map camera to area with zoom
    // populate recyclerview with properties.
    // Break view up between recycler and map view


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

    private void submitSearchValues() {
        LatLng latLng;
        String areaToSearch = mapSearchInput.getText().toString();
        double range = Double.parseDouble(mapRangeInput.getText().toString());

        latLng = getGeo(areaToSearch);
        if (latLng != null) {
            initObserver(latLng, range);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_search_submit:
                Log.i(TAG, "search button clicked");
                //TODO Check form is not empty
                InputHandler.hideKeyboard(requireActivity());
                //Form boolean necessary here?
                mapBinding.setFormClicked(!mapBinding.getFormClicked());
                submitSearchValues();
                break;
            default:
                break;
        }
    }
}