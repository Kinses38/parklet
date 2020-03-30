package com.kinses38.parklet.view.ui.fragments;

import android.content.Context;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.repository.BookingRepo;
import com.kinses38.parklet.data.repository.UserRepo;
import com.kinses38.parklet.databinding.FragmentNfcDialogBinding;
import com.kinses38.parklet.utilities.DialogListener;
import com.kinses38.parklet.utilities.NfcUtil;
import com.kinses38.parklet.utilities.ViewModelFactory;
import com.kinses38.parklet.view.ui.activities.MainActivity;
import com.kinses38.parklet.viewmodels.HomeViewModel;

public class NfcReadDialogFragment extends DialogFragment {

    private TextView nfcDialog;
    private DialogListener dialogListener;
    private String checkInProperty;
    private HomeViewModel homeViewModel;

    public static NfcReadDialogFragment newInstance() {
        return new NfcReadDialogFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentNfcDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nfc_dialog, container, true);
        ViewModelFactory viewModelFactory = new ViewModelFactory(new UserRepo(), new BookingRepo());
        homeViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(HomeViewModel.class);
        nfcDialog = binding.nfcText;
        checkPropertyTag();
        observeCheckIn();
        return binding.getRoot();

    }

    public void readNfc(Ndef ndef) {
        checkInProperty = NfcUtil.readNfc(ndef);
    }

    private void checkPropertyTag(){
        if(checkInProperty.length() == 0){
            nfcDialog.setText(R.string.empty_property_tag);
        }else{
            nfcDialog.setText(R.string.checkin_in_progress);
        }
    }

    private void observeCheckIn(){
        homeViewModel.updateCheckInStatus(checkInProperty).observe(getViewLifecycleOwner(),
                checkInStatus -> {
            if(checkInStatus != null){
                nfcDialog.setText(checkInStatus);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dialogListener = (MainActivity) context;
        dialogListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dialogListener.onDialogDismissed();
    }

}


