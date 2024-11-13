

package com.sushmitamalakar.providerapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailLogin, passwordLogin;
    private Button loginButton;
    private TextView redirectToRegister;
    private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();


        emailLogin = findViewById(R.id.loginEmailEditText);
        passwordLogin = findViewById(R.id.loginPassEditText);
        loginButton = findViewById(R.id.loginButton);
        redirectToRegister = findViewById(R.id.redirectToRegisterTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        // Set up the login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail() && validatePassword()) {
                    checkUser();
                }
            }
        });

        // Redirect to registration screen
        redirectToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Redirect to forgot password screen
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    // Method to validate email input
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

    // Method to validate password input
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

    // Method to authenticate user with Firebase and redirect to dashboard
    private void checkUser() {
        String userEmail = emailLogin.getText().toString().trim().toLowerCase();
        String userPassword = passwordLogin.getText().toString().trim();

        // Using Firebase Authentication for sign-in
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Get the current user's UID
                        String uid = authResult.getUser().getUid();

                        // Reference to the "providers" table in Firebase Realtime Database
                        DatabaseReference providerRef = FirebaseDatabase.getInstance().getReference("providers");

                        // Check if the email exists in the "providers" table
                        providerRef.orderByChild("email").equalTo(userEmail)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // Email exists in the "providers" table
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                            // Save user data in SharedPreferences
                                            SharedPreferences sharedPreferences = getSharedPreferences("ProviderAppPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("uid", uid);
                                            editor.putString("providerId", uid);
                                            editor.apply();

                                            // Navigate to ProviderDashboardActivity
                                            Intent intent = new Intent(LoginActivity.this, ProviderDashboardActivity.class);
                                            startActivity(intent);
                                            finish();  // Finish LoginActivity to prevent going back to it
                                        } else {
                                            // Email not found in the "providers" table, log out the user
                                            auth.signOut();
                                            Toast.makeText(LoginActivity.this, "Access Denied: Email not registered as provider", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
