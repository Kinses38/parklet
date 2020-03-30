package com.kinses38.parklet.view.ui.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.Property;
import com.kinses38.parklet.data.model.entity.User;
import com.kinses38.parklet.utilities.DialogListener;
import com.kinses38.parklet.view.ui.fragments.NfcWriteDialogFragment;
import com.kinses38.parklet.viewmodels.PropertyViewModel;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, DialogListener {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    private PropertyViewModel propertyViewModel;
    private NfcAdapter nfcAdapter;
    private boolean writeMode;
    private Property propertyToWrite;
    private NfcWriteDialogFragment nfcWriteDialogFragment;

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth  = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        initViewModel();
        initToolbar();
        initDrawer();
        initObserver();
        initNfc();



        initGoogleSignInClient();
    }

    private void initViewModel(){
        propertyViewModel = new ViewModelProvider(this).get(PropertyViewModel.class);
    }

    private void initObserver(){
        propertyViewModel.getPropertyToWriteMutableLiveData().observe(this, property -> {
            if (property != null){
                propertyToWrite = property;
                showNfcWriteDialog();
            }
        });
    }

    private void initNfc(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private void showNfcWriteDialog(){
        // https://developer.android.com/reference/androidx/fragment/app/DialogFragment
        FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag("NfcWriteDialogFragment");
        if(previous != null && previous.isAdded()){
            fragTrans.remove(previous);
        }

        nfcWriteDialogFragment = NfcWriteDialogFragment.newInstance();
        nfcWriteDialogFragment.show(fragTrans, "NfcWriteDialogFragment");
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        Tag propertyTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(propertyTag != null){
            Ndef ndef = Ndef.get(propertyTag);
            if(writeMode){
                nfcWriteDialogFragment.writeNfc(ndef, propertyToWrite );
                Toast.makeText(this, "This is NFC in write mode", Toast.LENGTH_SHORT).show();
            }else{
                //TODO check-in functionality
                Toast.makeText(this, "This is NFC in read mode", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_vehicles, R.id.nav_properties, R.id.nav_nfc).setDrawerLayout(drawer).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        Bundle bundle = new Bundle();

        bundle.putSerializable("User", getUserFromIntent());
        navController.setGraph(R.navigation.mobile_navigation, bundle);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView,navController);
    }

    //TODO Constants
    private User getUserFromIntent(){
        return (User) getIntent().getSerializableExtra("User");
    }

    private void initGoogleSignInClient(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            goToAuthActivity();
        }
    }

    private void goToAuthActivity(){
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(nfcAdapter != null && !nfcAdapter.isEnabled()){
            Toast.makeText(this, "Please enable NFC", Toast.LENGTH_SHORT).show();
        }
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[] {tagDetected};
        // I think this is the only intent filter needed for our app, nothing more specific
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 999,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        if(nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(nfcAdapter != null){
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    private void signOut(){
        firebaseAuth.signOut();
        googleSignInClient.signOut();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
       switch(menuItem.getItemId()){
           case R.id.sign_out_button:
               signOut();
               break;
       }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onDialogDisplayed(){
        //TODO check this doesn't interfere with other dialog, i.e. check-in dialog
        writeMode = true;
        Log.i("NFCTEST", "WRITEMODE" + writeMode);
        Log.i("NFCTEST", "DIALOGDISPLAY");
    }

    /**
     * Setting propertyToWrite to null prevents the dialog from being re-shown incorrectly after it was dismissed
     * ie: On screen orientation change. The propertyViewModel retains the property on the activity being destroyed
     * and will emit it through livedata as a new event even if the user is no longer writing the tag.
     */
    @Override
    public void onDialogDismissed(){
        writeMode = false;
        propertyViewModel.setPropertyToWrite(null);
        Log.i("NFCTEST", "WRITEMODE" + writeMode);
        Log.i("NFCTEST", "DIALOGDISMISSED");
    }
}
