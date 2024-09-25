package com.sushmitamalakar.providerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sushmitamalakar.providerapp.model.Provider;

public class DocumentUploadActivity extends AppCompatActivity {
    private Button addDocButton;
    private ImageView documentImage;
    private Uri imageUri;
    private String imageURL;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        documentImage = findViewById(R.id.documentImageView);
        addDocButton = findViewById(R.id.addDocButton);

        final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                imageUri = data.getData();
                                documentImage.setImageURI(imageUri);
                            }
                        } else {
                            Toast.makeText(DocumentUploadActivity.this, "No Image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        documentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        addDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImageAndData();
                } else {
                    Toast.makeText(DocumentUploadActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImageAndData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DocumentUploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        storageReference = FirebaseStorage.getInstance().getReference()
                .child("document_photo")
                .child(imageUri.getLastPathSegment());

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageURL = uri.toString();
                                uploadData();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DocumentUploadActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DocumentUploadActivity.this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void uploadData() {
        Provider provider = new Provider();
        provider.setDocImageURL(imageURL);

        FirebaseDatabase.getInstance().getReference("providers")
                .child(String.valueOf(System.currentTimeMillis())) // Using timestamp as a unique key
                .setValue(provider)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DocumentUploadActivity.this, "Document Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            documentImage.setImageURI(null); // Clear the image
                            Log.d("DocumentUploadActivity", "Successful");
                        } else {
                            Toast.makeText(DocumentUploadActivity.this, "Failed to Add Document: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DocumentUploadActivity.this, "Failed to Add Document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
