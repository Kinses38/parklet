package com.kinses38.parklet.utilities;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

public class NfcUtil {


    public static boolean writeNfc(Ndef ndef, String propertyUid) {
        boolean successState = false;
        if (ndef != null) {
            try {
                NdefMessage tag = new NdefMessage(
                        new NdefRecord[]{
                                NdefRecord.createMime("text/plain",
                                        propertyUid.getBytes(Charset.forName("US-ASCII"))),
                                //Only launches app, doesnt launch activity.
                                NdefRecord.createApplicationRecord("com.kinses38.parklet")});
                ndef.connect();
                ndef.writeNdefMessage(tag);
                ndef.close();
                successState = true;
                Log.i("NFCTEST", "Tag written: " + propertyUid);
            } catch (IOException e) {
                return successState;
            } catch (FormatException e) {
                e.printStackTrace();
                return successState;
            }
        }
        return successState;
    }

    public static String readNfc(Ndef ndef) {
        //TODO
        return null;
    }
}