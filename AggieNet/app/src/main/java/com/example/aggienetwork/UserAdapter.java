package com.example.aggienetwork;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class UserAdapter {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    // Callback interface for registration events
    public interface RegistrationCallback {
        void onRegistrationSuccess();

        void onRegistrationFailure(String errorMessage);
    }

    public UserAdapter() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://aggienet-users.firebaseio.com/").getReference("Users");
    }

    public void registerUser(String email, String password, String username, String name, Integer noofposts, RegistrationCallback registrationCallback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Username", username);
                    map.put("Name", name);
                    map.put("email", email);
                    map.put("noofposts", noofposts);
                    map.put("Id", mAuth.getCurrentUser().getUid());
                    map.put("timestamp", ServerValue.TIMESTAMP);

                    databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Database storage successful
                                registrationCallback.onRegistrationSuccess();
                            } else {
                                // Database storage failed
                                registrationCallback.onRegistrationFailure(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
    }
}
