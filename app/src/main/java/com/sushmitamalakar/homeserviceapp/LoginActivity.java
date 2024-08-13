package com.sushmitamalakar.homeserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailLogin, passwordLogin;
    private Button loginButton;
    private TextView redirectToRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        emailLogin = findViewById(R.id.loginEmailEditText);
        passwordLogin = findViewById(R.id.loginPassEditText);
        loginButton = findViewById(R.id.loginButton);
        redirectToRegister = findViewById(R.id.redirectToRegisterTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail() && validatePassword()) {
                    checkUser();
                }
            }
        });

        redirectToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private boolean validateEmail() {
        String val = emailLogin.getText().toString().trim();
        if (val.isEmpty()) {
            emailLogin.setError("Email cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            emailLogin.setError("Please enter a valid email");
            return false;
        } else {
            emailLogin.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = passwordLogin.getText().toString().trim();
        if (val.isEmpty()) {
            passwordLogin.setError("Password cannot be empty");
            return false;
        } else {
            passwordLogin.setError(null);
            return true;
        }
    }

    private void checkUser() {
        String userEmail = emailLogin.getText().toString().trim();
        String userPassword = passwordLogin.getText().toString().trim();

        // Using Firebase Authentication for sign in
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // User signed in successfully, now redirect to the dashboard
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Retrieve additional user information if needed from Firebase Realtime Database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        Query userQuery = databaseReference.orderByChild("email").equalTo(userEmail);

                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                    String namefromDB = userSnapshot.child("fullName").getValue(String.class);
                                    String emailFromDB = userSnapshot.child("email").getValue(String.class);
                                    String mobileNoFromDB = userSnapshot.child("mobileNo").getValue(String.class);

                                    Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                    intent.putExtra("fullName", namefromDB);
                                    intent.putExtra("email", emailFromDB);
                                    intent.putExtra("mobileNo", mobileNoFromDB);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
