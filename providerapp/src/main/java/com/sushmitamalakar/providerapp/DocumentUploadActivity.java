package com.sushmitamalakar.providerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sushmitamalakar.providerapp.databinding.ActivityDocumentUploadBinding;
import com.sushmitamalakar.providerapp.model.Document;

public class DocumentUploadActivity extends DrawerBaseActivity {

    ActivityDocumentUploadBinding activityDocumentUploadBinding;
    private Button addDocButton;
    private ImageView frontDocumentImageView, backDocumentImageView;
    private Uri frontImageUri, backImageUri;
    private String frontImageURL, backImageURL;
    private StorageReference storageReference;
    private String providerId;
    private boolean isFrontImageSelected = false; // Flag to track which image view was clicked

    private static final String PREFS_NAME = "ProviderAppPrefs";
    private static final String KEY_PROVIDER_ID = "providerId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDocumentUploadBinding = ActivityDocumentUploadBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Document Upload");
        setContentView(activityDocumentUploadBinding.getRoot());

        // Retrieve the provider ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        providerId = sharedPreferences.getString(KEY_PROVIDER_ID, null); // Set to null if not found

        // Initialize views
        frontDocumentImageView = findViewById(R.id.frontDocumentImageView);
        backDocumentImageView = findViewById(R.id.backDocumentImageView);
        addDocButton = findViewById(R.id.addDocButton);

        // Check if document already exists
        checkIfDocumentExists();

        // Register activity result launcher for image selection
        final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                if (isFrontImageSelected) {
                                    frontImageUri = selectedImageUri;
                                    frontDocumentImageView.setImageURI(frontImageUri);
                                } else {
                                    backImageUri = selectedImageUri;
                                    backDocumentImageView.setImageURI(backImageUri);
                                }
                            }
                        } else {
                            Toast.makeText(DocumentUploadActivity.this, "No Image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Set click listeners for image views to select images
        frontDocumentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFrontImageSelected = true; // Set the flag to indicate front image is being selected
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        backDocumentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFrontImageSelected = false; // Set the flag to indicate back image is being selected
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        // Upload document button click listener
        addDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    uploadImagesAndData();
                }
            }
        });
    }

    private void checkIfDocumentExists() {
        // Query Firebase to check if a document exists for the current provider
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("documents");
        databaseReference.orderByChild("providerId").equalTo(providerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Document already exists, redirect to ViewDocumentActivity
                    Toast.makeText(DocumentUploadActivity.this, "Document already uploaded", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DocumentUploadActivity.this, ViewDocumentActivity.class);
                    startActivity(intent);
                    finish(); // Finish this activity to prevent returning
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DocumentUploadActivity.this, "Error checking document: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Front Image URI
        if (frontImageUri == null) {
            Toast.makeText(DocumentUploadActivity.this, "Front Image Not Uploaded", Toast.LENGTH_SHORT).show();
            frontDocumentImageView.setBackgroundResource(R.drawable.error_background);
            isValid = false;
        } else {
            frontDocumentImageView.setBackgroundResource(0); // Reset if valid
        }

        // Validate Back Image URI
        if (backImageUri == null) {
            Toast.makeText(DocumentUploadActivity.this, "Back Image Not Uploaded", Toast.LENGTH_SHORT).show();
            backDocumentImageView.setBackgroundResource(R.drawable.error_background);
            isValid = false;
        } else {
            backDocumentImageView.setBackgroundResource(0);
        }

        return isValid;
    }

    private void uploadImagesAndData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DocumentUploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Upload front image
        storageReference = FirebaseStorage.getInstance().getReference()
                .child("document_photo")
                .child(frontImageUri.getLastPathSegment());

        storageReference.putFile(frontImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                frontImageURL = uri.toString();
                                // After uploading the front image, upload the back image
                                uploadBackImage(dialog);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DocumentUploadActivity.this, "Front Image Upload Failed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void uploadBackImage(AlertDialog dialog) {
        storageReference = FirebaseStorage.getInstance().getReference()
                .child("document_photo")
                .child(backImageUri.getLastPathSegment());

        storageReference.putFile(backImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                backImageURL = uri.toString();
                                uploadData(dialog);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DocumentUploadActivity.this, "Back Image Upload Failed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void uploadData(AlertDialog dialog) {
        // Create a Document object with status set to Pending
        Document document = new Document(frontImageURL, backImageURL);
        document.setStatus("Pending");

        // Generate a unique document ID
        String documentId = FirebaseDatabase.getInstance().getReference("documents").push().getKey();

        // Store the document in the database
        FirebaseDatabase.getInstance().getReference("documents")
                .child(documentId)
                .setValue(document)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Store the provider ID in the document for reference
                        FirebaseDatabase.getInstance().getReference("documents")
                                .child(documentId)
                                .child("providerId")
                                .setValue(providerId)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(DocumentUploadActivity.this, "Document Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        clearFields();

                                        Intent intent = new Intent(DocumentUploadActivity.this, ViewDocumentActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(DocumentUploadActivity.this, "Failed to link provider ID", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                });
                    } else {
                        Toast.makeText(DocumentUploadActivity.this, "Failed to upload document", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void clearFields() {
        frontDocumentImageView.setImageURI(null);
        backDocumentImageView.setImageURI(null);
        frontImageUri = null;
        backImageUri = null;
    }
}
