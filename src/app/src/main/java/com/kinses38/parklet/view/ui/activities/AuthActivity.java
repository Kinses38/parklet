package com.kinses38.parklet.view.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.viewmodels.AuthViewModel;

/**
 * AuthActivity class responsible for presenting unauthorised users with sign-in options for
 * registering for the app
 * <p>
 * Based on tutorial provided by Alex Mamo
 * https://medium.com/firebase-tips-tricks/how-to-create-a-clean-firebase-authentication-using-mvvm-37f9b8eb7336
 */

public class AuthActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initSignInButton();
        initAuthViewModel();
        initGoogleSignInClient();
    }

    /**
     * Intent to launch google sign in window after using presses sign in button.
     * Allows user to select which gmail account to authenticate with
     */
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        //TODO Need to introduce constants
        startActivityForResult(signInIntent, 123);
    }

    private void initSignInButton() {
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(v -> signIn());
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    /**
     * Requests email through google sign in options to allow user to choose which account to use.
     */
    private void initGoogleSignInClient() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    /**
     * Attempt to sign in with users selected account from the sign-in intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO constants for request
        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                Log.d("Auth activity error", e.getMessage());
            }
        }
    }

    /**
     * Retrieve credentials from selected account.
     *
     * @param googleSignInAccount the account to fetch the auth credentials from.
     */
    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCred = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCred(googleAuthCred);
    }

    /**
     * Sign into apps firebase auth instance using retrieved credentials.
     * Observes the result from the Auth ViewModel and logs user in and goes to main Activity
     *
     * @param googleAuthCred
     */
    private void signInWithGoogleAuthCred(AuthCredential googleAuthCred) {
        authViewModel.signInWithGoogle(googleAuthCred);
        authViewModel.getAuthenticatedUserLiveData().observe(this, this::goToMainActivity);
    }

    /**
     * Start main activity with serialised user extra to create users profile on firebaseDB.
     * @param user
     */
    private void goToMainActivity(User user) {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
        finish();
    }
}
