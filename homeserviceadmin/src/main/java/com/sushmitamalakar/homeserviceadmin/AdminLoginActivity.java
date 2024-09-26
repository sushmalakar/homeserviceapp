package com.sushmitamalakar.homeserviceadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {

    private static final String TAG = "AdminLoginActivity";

    private FirebaseAuth mAuth;
    private EditText loginEmailEditText;
    private EditText loginPassEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        loginEmailEditText = findViewById(R.id.loginEmailEditText);
        loginPassEditText = findViewById(R.id.loginPassEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = loginEmailEditText.getText().toString().trim();
        String password = loginPassEditText.getText().toString().trim();

        boolean isValid = true;

        // Check if email is empty
        if (email.isEmpty()) {
            loginEmailEditText.setError("Email is required");
            loginEmailEditText.requestFocus();
            isValid = false;
        }
        // Check if email format is valid
        else if (!isValidEmail(email)) {
            loginEmailEditText.setError("Invalid email format");
            loginEmailEditText.requestFocus();
            isValid = false;
        }

        // Check if password is empty
        if (password.isEmpty()) {
            loginPassEditText.setError("Password is required");
            loginPassEditText.requestFocus();
            isValid = false;
        }

        // Proceed only if all validations pass
        if (isValid) {
            // Sign in with Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(AdminLoginActivity.this, "Authentication Successful.", Toast.LENGTH_SHORT).show();

                                // Debugging log to confirm navigation
                                Log.d(TAG, "Navigating to AddServiceActivity");

                                Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                                startActivity(intent);
                                finish(); // Close the login activity
                            } else {
                                // If sign in fails
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(AdminLoginActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private boolean isValidEmail(String email) {
        // Simple email validation
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
