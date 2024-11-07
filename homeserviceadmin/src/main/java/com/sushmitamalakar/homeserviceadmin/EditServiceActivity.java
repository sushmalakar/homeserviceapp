package com.sushmitamalakar.homeserviceadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditServiceActivity extends AppCompatActivity {
    private EditText serviceNameEditText;
    private ImageView serviceImageView;
    private Button updateServiceButton;
    private Uri imageUri;
    private String serviceId;
    private String imageURL;
    private StorageReference storageReference;

    // Activity result launcher for selecting new image
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        serviceImageView.setImageURI(imageUri); // Show selected image
                    } else {
                        Toast.makeText(EditServiceActivity.this, "No Image selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);

        serviceNameEditText = findViewById(R.id.serviceEditText);
        serviceImageView = findViewById(R.id.serviceImageView);
        updateServiceButton = findViewById(R.id.updateServiceButton);

        // Get the passed service details from the Intent
        serviceId = getIntent().getStringExtra("ServiceId");
        String serviceName = getIntent().getStringExtra("ServiceName");
        imageURL = getIntent().getStringExtra("ServiceImage");

        // Set the existing details
        serviceNameEditText.setText(serviceName);
        Glide.with(this).load(imageURL).into(serviceImageView);

        // Handle image view click to select a new image
        serviceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        // Handle save button click to update the service
        updateServiceButton.setOnClickListener(v -> {
            String updatedServiceName = serviceNameEditText.getText().toString().trim();
            if (!updatedServiceName.isEmpty()) {
                if (imageUri != null) {
                    uploadImageAndUpdateService(serviceId, updatedServiceName); // If image is selected, upload and update
                } else {
                    updateService(serviceId, updatedServiceName, imageURL); // Only update service name if no new image is selected
                }
            } else {
                Toast.makeText(EditServiceActivity.this, "Service name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Upload the new image to Firebase Storage and update the service
    private void uploadImageAndUpdateService(String serviceId, String updatedServiceName) {
        storageReference = FirebaseStorage.getInstance().getReference()
                .child("service_pics")
                .child(imageUri.getLastPathSegment());

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String newImageUrl = uri.toString();
                                updateService(serviceId, updatedServiceName, newImageUrl); // Update with the new image URL
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditServiceActivity.this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Update the service in Firebase with the new service name and image URL
    private void updateService(String serviceId, String updatedServiceName, String newImageUrl) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("services").child(serviceId);
        databaseReference.child("serviceTitle").setValue(updatedServiceName);
        databaseReference.child("serviceImage").setValue(newImageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditServiceActivity.this, "Service updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful update
                    } else {
                        Toast.makeText(EditServiceActivity.this, "Failed to update service", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
