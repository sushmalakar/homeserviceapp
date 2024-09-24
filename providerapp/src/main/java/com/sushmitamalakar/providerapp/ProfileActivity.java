package com.sushmitamalakar.providerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.providerapp.model.Provider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView titleName, profileName, profileMobile, profileEmail;
    private CircleImageView userIconImageView;
    private Button editProfileBtn;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("providers");

        // Initialize Views
        titleName = findViewById(R.id.titleNameTextView);
        profileName = findViewById(R.id.nameTextView);
        profileMobile = findViewById(R.id.mobileTextView);
        profileEmail = findViewById(R.id.emailTextView);
        userIconImageView = findViewById(R.id.userIconImageView); // Initialize CircleImageView
        editProfileBtn = findViewById(R.id.editProfileButton);

        // Load user data
        loadUserData();

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                finish();
            }
        });
    }

    private void loadUserData() {
        FirebaseUser provider = auth.getCurrentUser();
        if (provider != null) {
            String providerId = provider.getUid();

            // Fetch user data from Firebase
            databaseReference.child(providerId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Provider provider = dataSnapshot.getValue(Provider.class);
                        if (provider != null) {
                            // Update UI with provider data
                            titleName.setText(provider.getFullName());
                            profileName.setText(provider.getFullName());
                            profileMobile.setText(provider.getMobileNo());
                            profileEmail.setText(provider.getEmail());

                            // Load profile image
                            if (provider.getImageUrl() != null && !provider.getImageUrl().isEmpty()) {
                                Glide.with(ProfileActivity.this).load(provider.getImageUrl()).into(userIconImageView);
                            }
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