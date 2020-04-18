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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kinses38.parklet.ParkLet;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.databinding.FragmentMapBinding;
import com.kinses38.parklet.utilities.InputHandler;
import com.kinses38.parklet.utilities.MapAdapter;
import com.kinses38.parklet.viewmodels.MapViewModel;
import com.kinses38.parklet.viewmodels.ViewModelFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Responsible for displaying MapView of properties within the search area of users interest.
 * Displays properties prepared by Map ViewModel as map markers with recyclerview summary.
 * Allows users to select property to book.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Inject
    ViewModelFactory viewModelFactory;
    private MapViewModel mapViewModel;
    private FragmentMapBinding mapBinding;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MapAdapter adapter;
    private RecyclerView.SmoothScroller smoothScroller;

    private MapView mapView;
    private GoogleMap map;
    private TextView mapSearchInput, mapRangeInput;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        ParkLet.getParkLetApp().getPropertyRepoComponent().inject(this);
        mapViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(MapViewModel.class);
        mapBinding.setLifecycleOwner(this);
        initBindings();
        initRecyclerView();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        getGeo();
        return mapBinding.getRoot();
    }

    /**
     * Pass the users search query to the Map ViewModel to find properties in range.
     * Updates the map to show properties that match the query,
     * otherwise displays no properties have been found matching the query
     *
     * @param latLng coordinates of the users area of interest
     * @param range  double km's of how far the user wants to search around the latLng coords.
     */
    private void searchPropertiesInRange(LatLng latLng, Double range) {
        mapViewModel.queryPropertiesInRange(latLng.longitude, latLng.latitude, range)
                .observe(getViewLifecycleOwner(), properties -> {
                    if (properties != null && !properties.isEmpty()) {
                        updateMap(properties);
                        mapBinding.setHasProperty(true);
                        adapter.refreshList(properties);
                        recyclerView.setAdapter(adapter);

                    } else {
                        map.clear();
                        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mapBinding.setHasProperty(false);
                    }
                });
    }

    /**
     * Initialise the recyclerview with a horizontal scroll,
     * and snaps to the nearest ViewHolder property item when the user is scrolling
     */
    private void initRecyclerView() {
        adapter = new MapAdapter(getActivity());
        recyclerView = mapBinding.mapPropertyRecycler;
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recyclerView);
        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        smoothScroller = new LinearSmoothScroller(requireContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Binds:
     * The MapView to googleMap for when onMapReady is called
     * This instance of MapFragment to the layout file
     * Search inputs to editTexts.
     */
    private void initBindings() {
        mapView = mapBinding.mapBlock;
        mapBinding.setMapFrag(this);
        mapSearchInput = mapBinding.mapSearchInput;
        mapRangeInput = mapBinding.mapRangeInput;
    }

    /**
     * Converts String address of users search query and returns LatLng coordinates object to be used
     * for checking properties in range.
     *
     * @param areaToSearch String Array address of users search area. Variadic/MultiArity to
     *                     allow initial map placement
     * @return LatLng object of coordinates.
     */
    private LatLng getGeo(String... areaToSearch) {
        LatLng latLng;
        String addressText = " Ireland";
        if (areaToSearch.length != 0) {
            addressText = areaToSearch[0] + addressText;
        }
        try {
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

    /**
     * Triggered when the map is ready to be displayed from the getMapAsync call in onCreateView.
     * Overrides default marker click listener to scroll the recyclerview to the corresponding property entry.
     * Camera position defaults to area over Dublin for now. (Todo user location)
     * https://developers.google.com/maps/documentation/android-sdk/map-with-marker
     * https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
     *
     * @param googleMap map instance ready to use.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setPadding(0, 0, 10, 400);
        map.setOnMarkerClickListener(marker -> {
            smoothScroller.setTargetPosition(adapter.findItem(marker.getPosition()));
            layoutManager.startSmoothScroll(smoothScroller);
            // Need to return false here otherwise info tag on markers do not display
            return false;
        });
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.463314, -6.231833), 10));
    }

    /**
     * Update map when properties matching users query are found.
     * Creates List of markers, calculate the bounds of camera with respect
     * to the markers currently on the map and move the camera to appropriate area.
     *
     * @param properties list of properties found matching users query.
     */
    private void updateMap(List<Property> properties) {
        //clear all currently existing markers
        map.clear();
        List<MarkerOptions> markers = createMarkers(properties);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLngBounds.Builder
        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            boundBuilder.include(marker.getPosition());
            map.addMarker(marker.visible(true));
        }
        LatLngBounds bounds = boundBuilder.build();
        final int boundsPadding = 400;
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, boundsPadding);
        map.animateCamera(update);

    }

    /**
     * Creates the map markers corresponding to each property and sets customisation.
     * Dynamically created markers cannot have a tag set to uniquely
     * identify them to match to the properties in the recyclerview, so instead the eircode is used.
     *
     * @param properties list of properties to convert to markers.
     * @return list of markers
     */
    private List<MarkerOptions> createMarkers(List<Property> properties) {
        List<MarkerOptions> propertyMarkers = new ArrayList<>();
        for (Property property : properties) {
            String[] brokenAddress = property.getAddressLine().split(",");
            propertyMarkers.add(new MarkerOptions()
                    .position(property.getLatLng())
                    .title(String.format("%s, %s", brokenAddress[0], brokenAddress[1]))
                    .snippet(formatPriceSnippet(property.getAverageComparison()))
                    .icon(BitmapDescriptorFactory.defaultMarker(formatPriceColor(property.getAverageComparison())))
                    .draggable(false)
                    .visible(false));
        }
        return propertyMarkers;
    }

    /**
     * Provides color customisation for markers to show how its priced in comparison to the
     * area average
     *
     * @param percentPriceComparison percent increase/decrease of property compared to area average.
     * @return color to be set on marker.
     */
    private Float formatPriceColor(Double percentPriceComparison) {
        if (percentPriceComparison <= -10) {
            //More expensive by 10%
            return BitmapDescriptorFactory.HUE_RED;
        } else if (percentPriceComparison > 10) {
            //Cheaper by more than 10%
            return BitmapDescriptorFactory.HUE_GREEN;
        }
        return BitmapDescriptorFactory.HUE_ORANGE;

    }

    /**
     * Provides text snippet of how the properties price compares to area average.
     * @param percentPriceComparison percent increase/decrease of property compared to area average.
     * @return String summary of price comparison
     */
    private String formatPriceSnippet(Double percentPriceComparison) {
        if (percentPriceComparison < 0) {
            return String.format(getString(R.string.average_expensive), Math.abs(percentPriceComparison));
        } else if (percentPriceComparison > 0) {
            return String.format(getString(R.string.expensive_cheaper), Math.abs(percentPriceComparison));
        }
        return "Priced on average";
    }

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

    /**
     * Fetch user query inputs from DataBinding xml.
     * Get LatLng object from geocode lookup.
     * Ready query parameters for passing to Map ViewModel.
     */
    private void submitSearchValues() {
        LatLng latLng;
        String areaToSearch = mapSearchInput.getText().toString();
        double range = Double.parseDouble(mapRangeInput.getText().toString());

        latLng = getGeo(areaToSearch);
        if (latLng != null) {
            searchPropertiesInRange(latLng, range);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_search_submit:
                Log.i(TAG, "search button clicked");
                //TODO Check form is not empty
                InputHandler.hideKeyboard(requireActivity());
                submitSearchValues();
                mapBinding.setSearchClicked(true);
                break;
            default:
                break;
        }
    }
}
