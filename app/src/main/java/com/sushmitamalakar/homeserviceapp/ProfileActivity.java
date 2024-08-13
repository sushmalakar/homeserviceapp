package com.sushmitamalakar.homeserviceapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceapp.model.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView titleName, profileName, profileMobile, profileEmail;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize TextViews
        titleName = findViewById(R.id.titleNameTextView);
        profileName = findViewById(R.id.nameTextView);
        profileMobile = findViewById(R.id.mobileTextView);
        profileEmail = findViewById(R.id.emailTextView);

        // Load user data
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Fetch user data from Firebase
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            // Update UI with user data
                            titleName.setText(user.getFullName());
                            profileName.setText(user.getFullName());
                            profileMobile.setText(user.getMobileNo());
                            profileEmail.setText(user.getEmail());
                        }
                    } else {
                        // Handle case where user data does not exist
                        Toast.makeText(ProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                    Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle case where the user is not logged in
            Toast.makeText(ProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
