package com.sushmitamalakar.provider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sushmitamalakar.provider.model.Provider;
import com.sushmitamalakar.provider.utils.ValidationUtils;


public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, mobileEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView redirectToLoginTextView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("providers");

        // Bind UI elements
        fullNameEditText = findViewById(R.id.regFullNameEditText);
        emailEditText = findViewById(R.id.regEmailEditText);
        mobileEditText = findViewById(R.id.regMobileEditText);
        passwordEditText = findViewById(R.id.regPassEditText);
        confirmPasswordEditText = findViewById(R.id.regConfirmPassEditText);
        registerButton = findViewById(R.id.registerButton);
        redirectToLoginTextView = findViewById(R.id.redirectToLoginButton);

        // Set button listeners
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    registerUser();
                }
            }
        });

        redirectToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private boolean validateInputs() {
        boolean valid = true;

        if (!ValidationUtils.validateFullName(fullNameEditText.getText().toString().trim(), fullNameEditText)) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!ValidationUtils.validateEmail(emailEditText.getText().toString().trim(), emailEditText)) {
            Toast.makeText(this, "Please re-enter a valid email", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!ValidationUtils.validateMobileNo(mobileEditText.getText().toString().trim(), mobileEditText)) {
            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!ValidationUtils.validatePassword(passwordEditText.getText().toString().trim(), passwordEditText)) {
            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!ValidationUtils.validateConfirmPassword(passwordEditText.getText().toString().trim(), confirmPasswordEditText.getText().toString().trim(), confirmPasswordEditText, passwordEditText)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void registerUser() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String mobileNo = mobileEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser provider = auth.getCurrentUser();
                    if (provider != null) {
                        String providerId = provider.getUid();
                        String defaultImageUrl = ""; // Set this to an empty string or any default value
                        Provider newUser = new Provider(fullName, email, mobileNo, defaultImageUrl);
//                        User newUser = new User(fullName, email, mobileNo);
                        databaseReference.child(providerId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                }
                            }
                        });
                    }
                } else {
                    handleRegistrationError(task.getException());
                }
            }
        });
    }

    private void handleRegistrationError(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            Toast.makeText(this, "Your password is too weak", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
