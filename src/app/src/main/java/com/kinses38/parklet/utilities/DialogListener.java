package com.kinses38.parklet.utilities;

/**
 * Interface to allow activities to observe when a dialog fragment that has been attached is displayed.
 */
public interface DialogListener {
    void onDialogDisplayed();
    void onDialogDismissed();
}
