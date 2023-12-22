package com.example.aggienetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements UserAdapter.RegistrationCallback{
    private EditText Username;
    private EditText Name;
    private EditText Email;
    private EditText Password;
    private Button Registerbutton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Username = (EditText) findViewById(R.id.username);
        Name =(EditText) findViewById(R.id.name);
        Email =(EditText) findViewById(R.id.email);
        Password =(EditText) findViewById(R.id.password);
        Registerbutton = (Button) findViewById(R.id.registerbutton);
        mAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://aggienet-users.firebaseio.com/").getReference("Users");

        userAdapter = new UserAdapter();

        Registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email= Email.getText().toString();
                String username = Username.getText().toString();
                String name = Name.getText().toString();
                String password = Password.getText().toString();
                Integer noofposts = 0;
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Empty FIelds", Toast.LENGTH_LONG).show();

                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Email.setError("Invalid EmailAddress");
                }else if (password.length() < 7){
                    Password.setError("Password must be greater than 7 digit");
                    Toast.makeText(RegisterActivity.this, "Small Password", Toast.LENGTH_LONG).show();
                }
                else{
                    userAdapter.registerUser(email, password, username, name, noofposts, RegisterActivity.this);

                }

            }
        });
    }

    @Override
    public void onRegistrationSuccess() {
        // Handle UI logic for successful registration
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, ImageSetupActivity.class));
    }

    @Override
    public void onRegistrationFailure(String errorMessage) {
        // Handle UI logic for registration failure
        Toast.makeText(this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
    }

}