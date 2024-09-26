package com.sushmitamalakar.providerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.providerapp.model.Document;

public class ViewDocumentActivity extends AppCompatActivity {
    private ImageView frontDocumentImageView, backDocumentImageView;
    private TextView documentStatusTextView; // TextView for displaying the document status
    private String providerId;
    private static final String PREFS_NAME = "ProviderAppPrefs";
    private static final String KEY_PROVIDER_ID = "providerId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);

        // Initialize views
        frontDocumentImageView = findViewById(R.id.frontDocumentImageView);
        backDocumentImageView = findViewById(R.id.backDocumentImageView);
        documentStatusTextView = findViewById(R.id.documentStatusTextView); // Initialize the status TextView

        // Get provider ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        providerId = sharedPreferences.getString(KEY_PROVIDER_ID, null);

        // Load the document if provider ID is available
        if (providerId != null) {
            loadDocument();
        } else {
            Toast.makeText(this, "Provider Id not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDocument() {
        DatabaseReference documentRef = FirebaseDatabase.getInstance().getReference("documents");

        documentRef.orderByChild("providerId").equalTo(providerId).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
                                Document document = documentSnapshot.getValue(Document.class);
                                if (document != null) {
                                    displayDocument(document);
                                    // Set the status in the TextView
                                    documentStatusTextView.setText("Status: " + document.getStatus());
                                }
                            }
                        } else {
                            Toast.makeText(ViewDocumentActivity.this, "No documents found for this provider.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewDocumentActivity.this, "Failed to load document", Toast.LENGTH_SHORT).show();
                        Log.e("ViewDocumentActivity", "Database Error: " + databaseError.getMessage());
                    }
                });
    }

    private void displayDocument(Document document) {
        Glide.with(this).load(Uri.parse(document.getFrontImageUrl())).into(frontDocumentImageView);
        Glide.with(this).load(Uri.parse(document.getBackImageUrl())).into(backDocumentImageView);
    }
}
