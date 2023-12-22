package com.example.aggienetwork;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aggienetwork.Fragments.ProfileFragment;
import com.example.aggienetwork.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import redis.clients.jedis.Jedis;


public class EditProfileActivity extends AppCompatActivity {

    private ImageView updateImage;
    private EditText Name;
    private EditText Username;
    private EditText Bio;
    private Button savebutton;
    private FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> activityResultLauncher;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        updateImage= findViewById(R.id.updateImage);
        Name = (EditText) findViewById(R.id.name);
        Username = (EditText) findViewById(R.id.username);
        Bio = (EditText) findViewById(R.id.Bio);
        savebutton = (Button) findViewById(R.id.savebutton);

        mAuth = FirebaseAuth.getInstance();

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this,ImageSetupActivity.class));
            }
        });
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String name = Name.getText().toString();
            String username = Username.getText().toString();
            String bio = Bio.getText().toString();

            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(username)){
                Toast.makeText(EditProfileActivity.this, "Empty Columns", Toast.LENGTH_LONG).show();
            }
            else{
                saveUser(name,username,bio);
            }
        }
        });



    }

    private void saveUser(String name, String username, String bio) {
        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to the Firebase Realtime Database node where user information is stored
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://aggienet-users.firebaseio.com/").getReference().child("Users").child(userId);

        // Create a HashMap to update the user's information
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("username", username);
        userUpdates.put("bio", bio);

        // Update the user's information in the database
        userRef.updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // User information updated successfully
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfileActivity.this, MainActivity.class));


                } else {
                    // Failed to update user information
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                saveBioToRedis(userId, bio);
            }
        }).start();
    }

    private void saveBioToRedis(String userId, String bio) {
        try {
            // Connect to Redis server
            Jedis jedis = new Jedis("redis-12466.c282.east-us-mz.azure.cloud.redislabs.com", 12466);
            jedis.auth("aggienet123"); // if required

            // Save bio to Redis
            jedis.set(userId + ":bio", bio);

            // Close the connection
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors (e.g., Redis server not available)
        }
    }

}