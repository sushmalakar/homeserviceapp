package com.sushmitamalakar.homeserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.sushmitamalakar.homeserviceapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, mobileEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView redirectToLoginTextView;
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        fullNameEditText = findViewById(R.id.regFullNameEditText);
        emailEditText = findViewById(R.id.regEmailEditText);
        mobileEditText = findViewById(R.id.regMobileEditText);
        passwordEditText = findViewById(R.id.regPassEditText);
        confirmPasswordEditText = findViewById(R.id.regConfirmPassEditText);
        registerButton = findViewById(R.id.registerButton);
        redirectToLoginTextView = findViewById(R.id.redirectToLoginButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUserValidation();
            }
        });

        redirectToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUserValidation() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String mobileNo = mobileEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            fullNameEditText.setError("Full name is required");
            fullNameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Please re-enter a valid email", Toast.LENGTH_LONG).show();
            emailEditText.setError("Valid email is required");
            emailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mobileNo)) {
            Toast.makeText(RegisterActivity.this, "Please enter your mobile no.", Toast.LENGTH_SHORT).show();
            mobileEditText.setError("Mobile no. is required");
            mobileEditText.requestFocus();
            return;
        }
        if (mobileNo.length() != 10) {
            Toast.makeText(RegisterActivity.this, "Mobile no. should be 10 digits", Toast.LENGTH_SHORT).show();
            mobileEditText.setError("Mobile no. should be 10 digits");
            mobileEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters", Toast.LENGTH_LONG).show();
            passwordEditText.setError("Password too weak");
            passwordEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            confirmPasswordEditText.setError("Password confirmation is required");
            confirmPasswordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            confirmPasswordEditText.setError("Passwords should match");
            confirmPasswordEditText.requestFocus();
            passwordEditText.clearComposingText();
            confirmPasswordEditText.clearComposingText();
            return;
        }

        // Register the user
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();

                    if(user!=null){
                        String userId = user.getUid();

                        User newUser = new User(fullName, email, mobileNo);

                        //Saving data to Realtime Database
                        databaseReference.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                }
                            }
                        });
                    }

                } else {
                    // Log the error
                    Log.e(TAG, "Registration failed", task.getException());

                    // Display appropriate error messages
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        passwordEditText.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters.");
                        passwordEditText.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailEditText.setError("Your email is invalid or already in use. Kindly reenter.");
                        emailEditText.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        emailEditText.setError("User is already registered with this email. Use another email.");
                        emailEditText.requestFocus();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}


// If all validations pass, register the user
//        registerUser(fullName, email, mobileNo, password, confirmPassword);
//    }

//    private void registerUser(String fullName, String email, String mobileNo,String password, String confirmPassword) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//                    FirebaseUser firebaseUser = auth.getCurrentUser();
//
//                    //Enter User Data into the Firebase Realtime DB
//                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fullName, email, mobileNo, password, confirmPassword);
//
//                    firebaseUser.sendEmailVerification();
//                } else {
//                    try{
//                        throw task.getException();
//                    }catch (FirebaseAuthWeakPasswordException e){
//                        passwordEditText.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters.");
//                        passwordEditText.requestFocus();
//                    }catch (FirebaseAuthInvalidCredentialsException e){
//                        emailEditText.setError("Your email is invalid or already in use. Kindly reenter.");
//                        emailEditText.requestFocus();
//                    }catch (FirebaseAuthUserCollisionException e){
//                        emailEditText.setError("User is already registered with this email. Use another email");
//                        emailEditText.requestFocus();
//                    }catch(Exception e){
//                        Log.e(TAG, e.getMessage());
//                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//    }
//}
