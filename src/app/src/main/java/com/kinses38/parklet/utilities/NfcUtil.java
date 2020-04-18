package com.kinses38.parklet.utilities;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Responsible for writing and reading ParkLet NFC tags.
 */
public class NfcUtil {
    private final static String TAG = NfcUtil.class.getSimpleName();

    /**
     * Writes ndef record array to nfc tag, consisting of the property ID for NFC based check-in,
     * and an application record for ParkLet to launch the app when the phone is touched to a ParkLet
     * written tag.
     *
     * @param ndef
     * @param propertyUid the id of the property
     * @return the status of the write attempt.
     */
    public static boolean writeNfc(Ndef ndef, String propertyUid) {
        boolean successState = false;
        if (ndef != null) {
            try {
                NdefMessage tag =
                        new NdefMessage(new NdefRecord[]{NdefRecord.createMime("text" + "/plain",
                                propertyUid.getBytes(Charset.forName("US-ASCII"))),
                                //Only launches app, doesn't launch NFC read intent.
                                NdefRecord.createApplicationRecord("com.kinses38.parklet")});
                ndef.connect();
                ndef.writeNdefMessage(tag);
                ndef.close();
                successState = true;
                Log.i(TAG, "Tag written: " + propertyUid);
            } catch (IOException e) {
                return successState;
            } catch (FormatException e) {
                e.printStackTrace();
                return successState;
            }
        }
        return successState;
    }

    /**
     * Retrieve propertyId for check-in from tag.
     * @param ndef
     * @return the id of the property or null in the case of an unwritten tag.
     */
    public static String readNfc(Ndef ndef) {
        if (ndef != null) {
            NdefMessage message = ndef.getCachedNdefMessage();
            if (message.getRecords().length != 0) {
                NdefRecord propertyUIDRecord = message.getRecords()[0];
                String propertyUID = new String(propertyUIDRecord.getPayload());
                Log.i(TAG, propertyUID);
                return propertyUID;
            }
        }
        return null;
    }
}