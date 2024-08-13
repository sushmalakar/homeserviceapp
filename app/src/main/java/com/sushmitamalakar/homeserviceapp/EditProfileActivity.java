package com.sushmitamalakar.homeserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceapp.model.User;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editName, editEmail, editMobile;
    private Button saveChanges;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        editName = findViewById(R.id.editFullNameEditText);
        editMobile = findViewById(R.id.editMobileEditText);
        editEmail = findViewById(R.id.editEmailEditText);
        saveChanges = findViewById(R.id.saveChangesButton);

        // Load current user data
        loadUserData();

        // Save changes on button click
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    private void loadUserData() {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get user data
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobileNo = snapshot.child("mobileNo").getValue(String.class);

                    // Set data to EditTexts
                    editName.setText(fullName);
                    editEmail.setText(email);
                    editMobile.setText(mobileNo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        String fullName = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String mobileNo = editMobile.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || mobileNo.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a user object
        User updatedUser = new User(fullName, email, mobileNo);

        // Update data in Firebase
        databaseReference.child(userId).setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
