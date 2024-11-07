package com.sushmitamalakar.homeserviceadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifyPendingDocumentActivity extends AppCompatActivity {
    private ImageView frontDocumentImageView, backDocumentImageView;
    private TextView providerIdTextView, providerNameTextView, documentStatusTextView, documentIdTextView;
    private Button verifyDocumentButton, rejectDocumentButton;

    // Firebase reference for documents
    private DatabaseReference documentRef;

    private String frontImageUrl, backImageUrl, providerId, providerName, documentId, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_pending_documents);

        // Initialize views
        frontDocumentImageView = findViewById(R.id.frontDocumentImageView);
        backDocumentImageView = findViewById(R.id.backDocumentImageView);
        providerIdTextView = findViewById(R.id.providerIdTextView);
        providerNameTextView = findViewById(R.id.providerNameTextView); // New TextView for Provider Name
        documentStatusTextView = findViewById(R.id.documentStatusTextView);
        documentIdTextView = findViewById(R.id.documentIdTextView);
        verifyDocumentButton = findViewById(R.id.verifyDocumentButton);
        rejectDocumentButton = findViewById(R.id.rejectDocumentButton);

        // Get the intent data passed from the adapter
        Intent intent = getIntent();
        providerId = intent.getStringExtra("providerId");
        providerName = intent.getStringExtra("providerName");
        documentId = intent.getStringExtra("documentId");
        status = intent.getStringExtra("status");
        frontImageUrl = intent.getStringExtra("frontImageUrl");
        backImageUrl = intent.getStringExtra("backImageUrl");


        // Check for null values to avoid null pointer exceptions
        if (documentId == null) {
            Toast.makeText(this, "Error: Missing document ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity as we cannot proceed without documentId
            return;
        }

        // Display provider ID, provider name, document ID, and status
        providerIdTextView.setText("Provider ID: " + providerId);
        providerNameTextView.setText("Provider Name: " + providerName); // Set provider name
        documentIdTextView.setText("Document ID: " + documentId);
        documentStatusTextView.setText("Status: " + status);

        // Load the document images using Glide
        loadImage(frontImageUrl, frontDocumentImageView);
        loadImage(backImageUrl, backDocumentImageView);

        // Initialize Firebase reference for the document to be verified
        documentRef = FirebaseDatabase.getInstance().getReference("documents").child(documentId);

        // Set click listeners for viewing full-size images
        frontDocumentImageView.setOnClickListener(v -> viewFullImage(frontImageUrl));
        backDocumentImageView.setOnClickListener(v -> viewFullImage(backImageUrl));

        // Handle verification button click with confirmation dialog
        verifyDocumentButton.setOnClickListener(v -> showConfirmationDialog(true));

        // Handle rejection button click with confirmation dialog
        rejectDocumentButton.setOnClickListener(v -> showConfirmationDialog(false));
    }

    // Method to load image using Glide
    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imageView);
        } else {
            Toast.makeText(this, "Image URL is invalid", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to view full-size image
    private void viewFullImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Image URL is not valid", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to show a confirmation dialog before verifying or rejecting
    private void showConfirmationDialog(boolean isVerify) {
        String action = isVerify ? "verify" : "reject";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to " + action + " this document?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (isVerify) {
                        updateDocumentStatus("Verified");
                    } else {
                        updateDocumentStatus("Rejected");
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to update the document status in Firebase
    private void updateDocumentStatus(String newStatus) {
        documentRef.child("status").setValue(newStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = newStatus.equals("Verified") ? "Document verified successfully" : "Document rejected";
                        Toast.makeText(VerifyPendingDocumentActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after status update
                    } else {
                        Toast.makeText(VerifyPendingDocumentActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
