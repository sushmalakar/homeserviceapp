package com.sushmitamalakar.providerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sushmitamalakar.providerapp.model.Document;

public class DocumentUploadActivity extends AppCompatActivity {
    private Button addDocButton;
    private ImageView frontDocumentImageView, backDocumentImageView;
    private Uri frontImageUri, backImageUri;
    private String frontImageURL, backImageURL;
    private StorageReference storageReference;
    private String providerId;
    private boolean isFrontImageSelected = false; // New flag to track which image view was clicked

    private static final String PREFS_NAME = "ProviderAppPrefs";
    private static final String KEY_PROVIDER_ID = "providerId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        // Retrieve the provider ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        providerId = sharedPreferences.getString(KEY_PROVIDER_ID, null); // Set to null if not found

        // Initialize views
        frontDocumentImageView = findViewById(R.id.frontDocumentImageView);
        backDocumentImageView = findViewById(R.id.backDocumentImageView);
        addDocButton = findViewById(R.id.addDocButton);

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
                                // Use the flag to determine which image view was clicked and set the corresponding URI
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

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Front Image URI
        if (frontImageUri == null) {
            Toast.makeText(DocumentUploadActivity.this, "Front Image Not Uploaded", Toast.LENGTH_SHORT).show();
            frontDocumentImageView.setBackgroundResource(R.drawable.error_background); // Optionally highlight the image view
            isValid = false;
        } else {
            frontDocumentImageView.setBackgroundResource(0); // Reset if valid
        }

        // Validate Back Image URI
        if (backImageUri == null) {
            Toast.makeText(DocumentUploadActivity.this, "Back Image Not Uploaded", Toast.LENGTH_SHORT).show();

            backDocumentImageView.setBackgroundResource(R.drawable.error_background); // Optionally highlight the image view
            isValid = false;
        } else {
            backDocumentImageView.setBackgroundResource(0);
        }
        if (frontImageUri == null && backImageUri == null){
            Toast.makeText(DocumentUploadActivity.this, "Image Not Uploaded", Toast.LENGTH_SHORT).show();
            isValid = false;
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
        document.setStatus("Pending"); // Ensure status is set

        // Generate a unique document ID
        String documentId = FirebaseDatabase.getInstance().getReference("documents").push().getKey();

        // Store the document in the database
        FirebaseDatabase.getInstance().getReference("documents")
                .child(documentId)
                .setValue(document)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Store the provider ID in the document for reference
                            FirebaseDatabase.getInstance().getReference("documents")
                                    .child(documentId)
                                    .child("providerId")
                                    .setValue(providerId)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(DocumentUploadActivity.this, "Document Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                clearFields();

                                                Intent intent = new Intent(DocumentUploadActivity.this, ViewDocumentActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DocumentUploadActivity.this, "Failed to link provider ID", Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                        } else {
                            Toast.makeText(DocumentUploadActivity.this, "Failed to upload document", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
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



//public class DocumentUploadActivity extends AppCompatActivity {
//    private Button addDocButton;
//    private ImageView frontDocumentImageView, backDocumentImageView;
////    private TextInputEditText citizenshipNumberEditText, issueDateEditText, issueDistrictEditText;
//    private Uri frontImageUri, backImageUri;
//    private String frontImageURL, backImageURL;
//    private StorageReference storageReference;
//    private String providerId; // Provider ID to be retrieved from SharedPreferences
//
//    private static final String PREFS_NAME = "ProviderAppPrefs";
//    private static final String KEY_PROVIDER_ID = "providerId";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_document_upload);
//
//        // Retrieve the provider ID from SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        providerId = sharedPreferences.getString(KEY_PROVIDER_ID, null); // Set to null if not found
//
//        // Initialize views
//        frontDocumentImageView = findViewById(R.id.frontDocumentImageView);
//        backDocumentImageView = findViewById(R.id.backDocumentImageView);
//        addDocButton = findViewById(R.id.addDocButton);
////        citizenshipNumberEditText = findViewById(R.id.citizenshipNumberEditText);
////        issueDateEditText = findViewById(R.id.issueDateEditText);
////        issueDistrictEditText = findViewById(R.id.issueDistrictEditText);
//
//        // Register activity result launcher for image selection
//        final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == RESULT_OK) {
//                            Intent data = result.getData();
//                            if (data != null) {
//                                if (frontImageUri == null) {
//                                    frontImageUri = data.getData();
//                                    frontDocumentImageView.setImageURI(frontImageUri);
//                                } else {
//                                    backImageUri = data.getData();
//                                    backDocumentImageView.setImageURI(backImageUri);
//                                }
//                            }
//                        } else {
//                            Toast.makeText(DocumentUploadActivity.this, "No Image selected", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );
//
//        // Set click listeners for image views to select images
//        frontDocumentImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
//            }
//        });
//
//        backDocumentImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
//            }
//        });
//
//        // Upload document button click listener
//        addDocButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (validateInputs()) {
//                    uploadImagesAndData();
//                }
//            }
//        });
//    }
//
//    private boolean validateInputs() {
//        boolean isValid = true;
//
////        String citizenshipNumber = citizenshipNumberEditText.getText().toString().trim();
////        String issueDate = issueDateEditText.getText().toString().trim();
////        String issueDistrict = issueDistrictEditText.getText().toString().trim();
//
//        // Clear previous errors
////        citizenshipNumberEditText.setError(null);
////        issueDateEditText.setError(null);
////        issueDistrictEditText.setError(null);
//
//        // Validate Citizenship Number
////        if (citizenshipNumber.isEmpty()) {
////            citizenshipNumberEditText.setError("Please enter your citizenship number");
////            isValid = false;
////        }
//
//        // Validate Issue Date
////        if (issueDate.isEmpty()) {
////            issueDateEditText.setError("Please enter the issue date");
////            isValid = false;
////        }
//
//        // Validate Issue District
////        if (issueDistrict.isEmpty()) {
////            issueDistrictEditText.setError("Please enter the issue district");
////            isValid = false;
////        }
//
//        // Validate Front Image URI
//        if (frontImageUri == null) {
//            frontDocumentImageView.setBackgroundResource(R.drawable.error_background); // Optionally highlight the image view
//            isValid = false;
//        } else {
//            frontDocumentImageView.setBackgroundResource(0); // Reset if valid
//        }
//
//        // Validate Back Image URI
//        if (backImageUri == null) {
//            backDocumentImageView.setBackgroundResource(R.drawable.error_background); // Optionally highlight the image view
//            isValid = false;
//        } else {
//            backDocumentImageView.setBackgroundResource(0); // Reset if valid
//        }
//
//        return isValid;
//    }
//
//
//    private void uploadImagesAndData() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(DocumentUploadActivity.this);
//        builder.setCancelable(false);
//        builder.setView(R.layout.progress_layout);
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//
//        // Upload front image
//        storageReference = FirebaseStorage.getInstance().getReference()
//                .child("document_photo")
//                .child(frontImageUri.getLastPathSegment());
//
//        storageReference.putFile(frontImageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                frontImageURL = uri.toString();
//                                // After uploading the front image, upload the back image
//                                uploadBackImage(dialog);
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(DocumentUploadActivity.this, "Front Image Upload Failed", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                });
//    }
//
//    private void uploadBackImage(AlertDialog dialog) {
//        storageReference = FirebaseStorage.getInstance().getReference()
//                .child("document_photo")
//                .child(backImageUri.getLastPathSegment());
//
//        storageReference.putFile(backImageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                backImageURL = uri.toString();
//                                uploadData(dialog);
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(DocumentUploadActivity.this, "Back Image Upload Failed", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                });
//    }
//
//    private void uploadData(AlertDialog dialog) {
//        // Collect data from input fields
////        String citizenshipNumber = citizenshipNumberEditText.getText().toString();
////        String issueDate = issueDateEditText.getText().toString();
////        String issueDistrict = issueDistrictEditText.getText().toString();
//
//        // Create a Document object
//        Document document = new Document(frontImageURL, backImageURL);
//
//        // Store the data in the 'documents' node with a unique document ID
//        String documentId = FirebaseDatabase.getInstance().getReference("documents").push().getKey(); // Generate a new document ID
//
//        FirebaseDatabase.getInstance().getReference("documents")
//                .child(documentId)  // Use the generated document ID
//                .setValue(document)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            // Also store the provider ID in the document for reference
//                            FirebaseDatabase.getInstance().getReference("documents")
//                                    .child(documentId)
//                                    .child("providerId")
//                                    .setValue(providerId)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(DocumentUploadActivity.this, "Document Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                                                clearFields();  // Clear the input fields after success
//                                                Log.d("DocumentUploadActivity", "Success");
//                                            } else {
//                                                Toast.makeText(DocumentUploadActivity.this, "Failed to link provider ID", Toast.LENGTH_SHORT).show();
//                                            }
//                                            dialog.dismiss();
//                                        }
//                                    });
//                        } else {
//                            Toast.makeText(DocumentUploadActivity.this, "Failed to upload document", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        }
//                    }
//                });
//    }
//
//    private void clearFields() {
////        citizenshipNumberEditText.setText("");
////        issueDateEditText.setText("");
////        issueDistrictEditText.setText("");
//        frontDocumentImageView.setImageURI(null);
//        backDocumentImageView.setImageURI(null);
//        frontImageUri = null;
//        backImageUri = null;
//    }
//}