package com.kinses38.parklet.view.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kinses38.parklet.R;
import com.kinses38.parklet.data.model.entity.User;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User user = getUserFromIntent();
        initGoogleSignInClient();
        initMessageTextView();
        setMessageToMessageTextView(user);
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

    private void initMessageTextView(){
        messageTextView = findViewById(R.id.message_text_view);
    }

    //TODO place holder for profile information.
    private void setMessageToMessageTextView(User user){
        String message = "You are logged in as: \n" +user.getName() + "\n" + user.getEmail();
        messageTextView.setText(message);
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
}
