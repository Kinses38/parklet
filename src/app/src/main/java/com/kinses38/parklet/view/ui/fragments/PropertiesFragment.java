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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.databinding.FragmentPropertiesBinding;
import com.kinses38.parklet.utilities.InputHandler;
import com.kinses38.parklet.utilities.UserPropertyAdapter;
import com.kinses38.parklet.viewmodels.PropertyViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PropertiesFragment extends Fragment implements View.OnClickListener {

    private PropertyViewModel propertyViewModel;
    private FragmentPropertiesBinding propertiesBinding;

    private RecyclerView recyclerView;
    private UserPropertyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView propertyAddress, textProperties, addressLine, dailyRate;
    private RadioGroup weekends;

    private Address address;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        propertiesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_properties, container, false);
        propertyViewModel = new ViewModelProvider(requireActivity()).get(PropertyViewModel.class);
        propertiesBinding.setLifecycleOwner(this);

        initRecyclerView();
        initBindings();
        initPropertyObserver();

        return propertiesBinding.getRoot();
    }

    private void initRecyclerView() {
        adapter = new UserPropertyAdapter(getActivity());
        recyclerView = propertiesBinding.getRoot().findViewById(R.id.property_recycler);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initBindings() {
        propertiesBinding.setPropertyFrag(this);
        propertiesBinding.setFormClicked(false);
        propertiesBinding.setViewModel(propertyViewModel);
        weekends = propertiesBinding.weekendRadioGroup;
        propertyAddress = propertiesBinding.addressInput;
        textProperties = propertiesBinding.textProperties;
        addressLine = propertiesBinding.addressLineOne;
        dailyRate = propertiesBinding.dailyRate;

        propertyAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getGeo();
                }
            }
        });
    }

    private void initPropertyObserver(){
        propertyViewModel.getProperties().observe(getViewLifecycleOwner(), new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                if(!properties.isEmpty()){
                    propertiesBinding.setHasProperty(true);
                    adapter.refreshList(properties);
                    recyclerView.setAdapter(adapter);
                }else{
                    propertiesBinding.setHasProperty(false);
                }

            }
        });
    }

    private void getGeo() {
        String addressText = propertyAddress.getText().toString();
        Log.i("Address", addressText);
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List addressList = geocoder.getFromLocationName(addressText, 1);
            if (!addressList.isEmpty()) {
                Log.i("Address list", addressList.toString());
                address = (Address) addressList.get(0);
                setAddressText(address);
            } else {
                Toast.makeText(getActivity(), String.format("Cannot find address %s", addressText), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("Geocode failed", addressText);
        }
    }

    private void setAddressText(Address address) {
        String finalAddress = address.getAddressLine(0);
        addressLine.setText(finalAddress);
    }

    //TODO disable save button until fields filled
    private void saveProperty(){
        int radioSelected = weekends.getCheckedRadioButtonId();
        RadioButton r = (RadioButton)propertiesBinding.getRoot().findViewById(radioSelected);
        String availableWeekends = r.getText().toString();
        Double rate = Double.parseDouble(dailyRate.getText().toString());

        propertyViewModel.setProperty(address, rate, availableWeekends);

    }

    private void resetForm(){
        propertyAddress.setText("");
        textProperties.setText("");
        addressLine.setText("");
        dailyRate.setText(R.string.default_daily_rate);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.property_form_toggle_button:
                propertiesBinding.setFormClicked(!propertiesBinding.getFormClicked());
                Log.i("Form clicked", "yep");
                break;
            case R.id.property_form_save:
                InputHandler.hideKeyboard(requireActivity());
                saveProperty();
                propertiesBinding.setFormClicked(!propertiesBinding.getFormClicked());
                resetForm();
                Log.i("Property save", "clicked");
                break;
            case R.id.property_form_cancel:
                InputHandler.hideKeyboard(requireActivity());
                propertiesBinding.setFormClicked(!propertiesBinding.getFormClicked());
                resetForm();
                Log.i("Property cancel", "clicked");
                break;
            default:
                break;
        }
    }


}
