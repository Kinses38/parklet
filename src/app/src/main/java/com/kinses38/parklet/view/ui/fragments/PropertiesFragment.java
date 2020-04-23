package com.kinses38.parklet.view.ui.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kinses38.parklet.ParkLet;
import com.kinses38.parklet.R;
import com.kinses38.parklet.databinding.FragmentPropertiesBinding;
import com.kinses38.parklet.utilities.InputHandler;
import com.kinses38.parklet.utilities.UserPropertyAdapter;
import com.kinses38.parklet.viewmodels.PropertyViewModel;
import com.kinses38.parklet.viewmodels.ViewModelFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class PropertiesFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    @Inject
    ViewModelFactory viewModelFactory;
    private PropertyViewModel propertyViewModel;
    private FragmentPropertiesBinding propertiesBinding;

    private RecyclerView recyclerView;
    private UserPropertyAdapter adapter;
    private TextView propertyAddress, addressLine, dailyRate, dailyRateError;
    private RadioGroup weekends;

    private Address address;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        propertiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_properties, container, false);
        ParkLet.getParkLetApp().getPropertyRepoComponent().inject(this);
        propertyViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(PropertyViewModel.class);
        propertiesBinding.setLifecycleOwner(this);

        initRecyclerView();
        initBindings();
        initPropertyObserver();

        return propertiesBinding.getRoot();
    }

    /**
     * Initialise recyclerview in preparation to show users properties if they exist.
     */
    private void initRecyclerView() {
        adapter = new UserPropertyAdapter(getActivity());
        recyclerView = propertiesBinding.getRoot().findViewById(R.id.property_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Set bindings for:
     * Give property fragment layout this instance of property fragment.
     * Set boolean for whether form is visible
     * TextViews for the form
     * On focus change listener when the user has changed input
     * from the addressline to trigger geocode query.
     */
    private void initBindings() {
        propertiesBinding.setPropertyFrag(this);
        propertiesBinding.setFormClicked(false);
        propertiesBinding.setViewModel(propertyViewModel);
        weekends = propertiesBinding.weekendRadioGroup;
        propertyAddress = propertiesBinding.addressInput;
        addressLine = propertiesBinding.addressLineOne;
        dailyRate = propertiesBinding.dailyRate;
        dailyRateError = propertiesBinding.dailyRateError;

        propertyAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                getGeo();
            }
        });
    }

    /**
     * Call to property ViewModel to retrieve users properties,
     * if properties exist show the recyclerview otherwise show text informing the user to add a property.
     */
    private void initPropertyObserver() {
        propertyViewModel.getProperties().observe(getViewLifecycleOwner(), properties -> {
            if (!properties.isEmpty()) {
                propertiesBinding.setHasProperty(true);
                adapter.refreshList(properties);
                recyclerView.setAdapter(adapter);
            } else {
                propertiesBinding.setHasProperty(false);
            }

        });
    }

    /**
     * Takes a String address and attempts to look up the full address and LatLng using geocode API
     */
    private void getGeo() {
        String addressText = propertyAddress.getText().toString();
        Log.i(TAG, "Address: " + addressText);
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List addressList = geocoder.getFromLocationName(addressText, 1);
            if (!addressList.isEmpty()) {
                Log.i(TAG, "Address list" + addressList.toString());
                address = (Address) addressList.get(0);
                setAddressText(address);
            } else {
                Toast.makeText(getActivity(), String.format("Cannot find address %s", addressText), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocode failed: " + addressText);
        }
    }

    /**
     * Retrieve the summary of the address from the geocode result
     *
     * @param address full address object containing all info for that address.
     */
    private void setAddressText(Address address) {
        String finalAddress = address.getAddressLine(0);
        addressLine.setText(finalAddress);
    }

    //TODO disable save button until fields filled

    /**
     * Retrieve property info from PropertyFragment layout form and pass to viewModel.
     */
    private void saveProperty() {
        int radioSelected = weekends.getCheckedRadioButtonId();
        RadioButton r = propertiesBinding.getRoot().findViewById(radioSelected);
        String availableWeekends = r.getText().toString();
        Double rate = Double.parseDouble(dailyRate.getText().toString());

        propertyViewModel.setProperty(address, rate, availableWeekends)
                .observe(getViewLifecycleOwner(),
                        result -> Toast.makeText(requireActivity(), result, Toast.LENGTH_SHORT).show());
    }

    /**
     * Reset all fields in form after cancelling or submitting a property.
     */
    private void resetForm() {
        propertyAddress.setText("");
        addressLine.setText("");
        dailyRate.setText(R.string.default_daily_rate);
    }

    /**
     * Ensure address lookup succeeded/User entered Address.
     * Other form values are provided with defaults
     */
    private void validateProperty() {
        boolean isValid = true;
        if (addressLine.getText().toString().isEmpty()) {
            addressLine.setText(R.string.address_required);
            isValid = false;
        }
        if (dailyRate.getText().toString().isEmpty()) {
            dailyRateError.setText(R.string.no_price);
            isValid = false;

        }
        if (isValid) {
            saveProperty();
            propertiesBinding.setFormClicked(!propertiesBinding.getFormClicked());
            resetForm();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.property_form_toggle_button:
                propertiesBinding.setFormClicked(!propertiesBinding.getFormClicked());
                Log.i(TAG, "Form show button clicked");
                break;
            case R.id.property_form_save:
                InputHandler.hideKeyboard(requireActivity());
                validateProperty();
                Log.i(TAG, "Property saved, clicked");
                break;
            case R.id.property_form_cancel:
                InputHandler.hideKeyboard(requireActivity());
                propertiesBinding.setFormClicked(!propertiesBinding.getFormClicked());
                resetForm();
                Log.i(TAG, "Property cancel clicked");
                break;
            default:
                break;
        }
    }


}
