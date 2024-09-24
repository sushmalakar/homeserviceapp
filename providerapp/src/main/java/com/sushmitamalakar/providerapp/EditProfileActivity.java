package com.sushmitamalakar.providerapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sushmitamalakar.providerapp.model.Provider;
import com.sushmitamalakar.providerapp.utils.ValidationUtils;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editName, editEmail, editMobile;
    private CircleImageView profileImage;
    private FloatingActionButton floatingImageButton;
    private Uri imageUri;
    private Bitmap bitmap;
    private Button saveChanges;
    private DatabaseReference databaseReference;
    private String userId;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String imageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        databaseReference = FirebaseDatabase.getInstance().getReference("providers");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        editName = findViewById(R.id.editFullNameEditText);
        editMobile = findViewById(R.id.editMobileEditText);
        editEmail = findViewById(R.id.editEmailEditText);
        profileImage = findViewById(R.id.profileImageView);
        floatingImageButton = findViewById(R.id.cameraImageView);
        saveChanges = findViewById(R.id.saveChangesButton);

        // Load current user data
        loadUserData();

        floatingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    if (imageUri != null) {
                        uploadImage();
                    } else {
                        saveUserData();
                    }
                }
            }
        });
    }

    private void loadUserData() {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobileNo = snapshot.child("mobileNo").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    editName.setText(fullName);
                    editEmail.setText(email);
                    editMobile.setText(mobileNo);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(EditProfileActivity.this).load(imageUrl).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        String fullName = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String mobileNo = editMobile.getText().toString().trim();

        boolean isValidFullName = ValidationUtils.validateFullName(fullName, editName);
        boolean isValidEmail = ValidationUtils.validateEmail(email, editEmail);
        boolean isValidMobileNo = ValidationUtils.validateMobileNo(mobileNo, editMobile);

        return isValidFullName && isValidEmail && isValidMobileNo;
    }

    private void saveUserData() {
        String fullName = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String mobileNo = editMobile.getText().toString().trim();

        Provider updatedUser = new Provider(fullName, email, mobileNo, imageUrl);

        databaseReference.child(userId).setValue(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            pickImageFromGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            profileImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void uploadImage() {
        if (imageUri != null) {
            String fileName = userId + "_" + System.currentTimeMillis() + ".jpg";
            final StorageReference fileReference = storageReference.child("profile_pics/" + fileName);

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    saveUserData(); // Update user data with the new image URL
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
