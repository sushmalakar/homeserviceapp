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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    private static final String TAG = "AdminLoginActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference adminRef;
    private EditText loginEmailEditText;
    private EditText loginPassEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference("admin");

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

        // Validate email and password
        if (email.isEmpty()) {
            loginEmailEditText.setError("Email is required");
            loginEmailEditText.requestFocus();
            isValid = false;
        } else if (!isValidEmail(email)) {
            loginEmailEditText.setError("Invalid email format");
            loginEmailEditText.requestFocus();
            isValid = false;
        }

        if (password.isEmpty()) {
            loginPassEditText.setError("Password is required");
            loginPassEditText.requestFocus();
            isValid = false;
        }

        // Proceed only if validations pass
        if (isValid) {
            // Check if the email exists in the "admin" table
            checkIfEmailIsAdmin(email, password);
        }
    }

    private void checkIfEmailIsAdmin(String email, String password) {
        adminRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String adminEmail = snapshot.getValue(String.class);

                    // Check if the entered email matches the admin email
                    if (email.equals(adminEmail)) {
                        // Attempt Firebase Authentication with the provided credentials
                        signInWithEmailAndPassword(email, password);
                    } else {
                        // Email does not match the admin email
                        Toast.makeText(AdminLoginActivity.this, "Access Denied: You are not an admin.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Admin email not found in database.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "DatabaseError: " + error.getMessage());
                Toast.makeText(AdminLoginActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithEmail:success");

                            Toast.makeText(AdminLoginActivity.this, "Authentication Successful. Welcome, Admin!", Toast.LENGTH_SHORT).show();

                            // Navigate to Admin Dashboard
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

    private boolean isValidEmail(String email) {
        // Simple email validation
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}


//package com.sushmitamalakar.homeserviceadmin;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class AdminLoginActivity extends AppCompatActivity {
//
//    private static final String TAG = "AdminLoginActivity";
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference adminRef; // Reference to the "admin" table
//    private EditText loginEmailEditText;
//    private EditText loginPassEditText;
//    private Button loginButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_login);
//
//        // Initialize Firebase Auth and Database
//        mAuth = FirebaseAuth.getInstance();
//        adminRef = FirebaseDatabase.getInstance().getReference("admin"); // Reference to "admin" table
//
//        // Initialize UI elements
//        loginEmailEditText = findViewById(R.id.loginEmailEditText);
//        loginPassEditText = findViewById(R.id.loginPassEditText);
//        loginButton = findViewById(R.id.loginButton);
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loginUser();
//            }
//        });
//    }
//
//    private void loginUser() {
//        String email = loginEmailEditText.getText().toString().trim();
//        String password = loginPassEditText.getText().toString().trim();
//
//        boolean isValid = true;
//
//        // Check if email is empty
//        if (email.isEmpty()) {
//            loginEmailEditText.setError("Email is required");
//            loginEmailEditText.requestFocus();
//            isValid = false;
//        }
//        // Check if email format is valid
//        else if (!isValidEmail(email)) {
//            loginEmailEditText.setError("Invalid email format");
//            loginEmailEditText.requestFocus();
//            isValid = false;
//        }
//
//        // Check if password is empty
//        if (password.isEmpty()) {
//            loginPassEditText.setError("Password is required");
//            loginPassEditText.requestFocus();
//            isValid = false;
//        }
//
//        // Proceed only if all validations pass
//        if (isValid) {
//            // Sign in with Firebase Authentication
//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                Log.d(TAG, "signInWithEmail:success");
//
//                                if (user != null) {
//                                    checkIfUserIsAdmin(user.getUid()); // Check the admin table
//                                }
//                            } else {
//                                // If sign in fails
//                                Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                Toast.makeText(AdminLoginActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//        }
//    }
//
//    private void checkIfUserIsAdmin(String uid) {
//        // Check if the user's UID exists in the "admin" table
//        adminRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    // User is an admin
//                    Log.d(TAG, "Admin verified");
//
//                    Toast.makeText(AdminLoginActivity.this, "Authentication Successful. Welcome, Admin!", Toast.LENGTH_SHORT).show();
//
//                    // Navigate to Admin Dashboard
//                    Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
//                    startActivity(intent);
//                    finish(); // Close the login activity
//                } else {
//                    // User is not an admin
//                    Toast.makeText(AdminLoginActivity.this, "Access Denied: You are not an admin.", Toast.LENGTH_LONG).show();
//                    mAuth.signOut(); // Log the user out
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "DatabaseError: " + error.getMessage());
//                Toast.makeText(AdminLoginActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private boolean isValidEmail(String email) {
//        // Simple email validation
//        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
//    }
//}

