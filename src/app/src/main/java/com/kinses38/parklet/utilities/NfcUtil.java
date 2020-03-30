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
                NdefMessage tag =
                        new NdefMessage(new NdefRecord[] {NdefRecord.createMime("text" + "/plain",
                                propertyUid.getBytes(Charset.forName("US-ASCII"))),
                        //Only launches app, doesn't launch NFC read intent.
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
        if(ndef != null){
            NdefMessage message = ndef.getCachedNdefMessage();
            if(message.getRecords().length != 0){
                NdefRecord addressRecord = message.getRecords()[0];
                String propertyAddress = new String(addressRecord.getPayload());
                Log.i("NFCTEST", propertyAddress);
                return propertyAddress;
            }
        }
        return null;
    }
}