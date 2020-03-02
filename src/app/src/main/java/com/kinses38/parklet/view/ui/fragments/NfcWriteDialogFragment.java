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

import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.databinding.FragmentNfcWriteDialogBinding;
import com.kinses38.parklet.utilities.DialogListener;
import com.kinses38.parklet.utilities.NfcUtil;
import com.kinses38.parklet.view.ui.activities.MainActivity;

public class NfcWriteDialogFragment extends DialogFragment {

    private TextView writeDialog;
    private FragmentNfcWriteDialogBinding binding;
    private DialogListener dialogListener;

    public static NfcWriteDialogFragment newInstance() {
        return new NfcWriteDialogFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nfc_write_dialog, container, true);
        writeDialog = binding.rvNfcWriteTag;

        writeDialog.setText("Hold the tag to your phone!");
        return binding.getRoot();

    }

    public void writeNfc(Ndef ndef, Property propertyToWrite) {
        String propertyUid = propertyToWrite.getPropertyUID();
        if (NfcUtil.writeNfc(ndef, propertyUid)) {
            writeDialog.setText("Successfully wrote tag for: \n" + propertyToWrite.getAddressLine());
        } else {
            writeDialog.setText("Write Failed!");
        }
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