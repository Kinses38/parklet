package com.kinses38.parklet.data.repository;

import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kinses38.parklet.data.model.entity.User;

/**
 * Authorisation repository class to interact with firebase auth services to authorise new users.
 */

public class AuthRepo {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public MutableLiveData<User> firebaseSignin(AuthCredential googleAuthCred){

        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCred).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser !=null){
                    String uuid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    User user = new User(uuid, name, email);
                    user.setNew(isNewUser);
                    authenticatedUserMutableLiveData.setValue(user);
                }
            }else {
                Log.d("Firebase Auth error",task.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

}
