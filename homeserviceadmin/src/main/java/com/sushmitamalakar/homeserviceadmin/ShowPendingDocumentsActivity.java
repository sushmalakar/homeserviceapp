package com.sushmitamalakar.homeserviceadmin;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceadmin.adapter.PendingDocumentsAdapter;
import com.sushmitamalakar.homeserviceadmin.model.Document;

import java.util.ArrayList;
import java.util.List;

public class ShowPendingDocumentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Document> documentList;
    private DatabaseReference databaseReference;
    private PendingDocumentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pending_documents);

        recyclerView = findViewById(R.id.pendingDocumentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show a loading dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        documentList = new ArrayList<>();
        adapter = new PendingDocumentsAdapter(this, documentList);
        recyclerView.setAdapter(adapter);

        // Firebase reference to the "documents" node
        databaseReference = FirebaseDatabase.getInstance().getReference("documents");

        // Event listener to fetch pending documents
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                documentList.clear();
//                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
//                    Document document = itemSnapshot.getValue(Document.class);
//
//                    if (document != null && "Pending".equalsIgnoreCase(document.getStatus())) {
//                        String providerId = document.getProviderId();
//
//                        // Fetch provider fullName using providerId
//                        DatabaseReference providerRef = FirebaseDatabase.getInstance().getReference("providers").child(providerId);
//                        providerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot providerSnapshot) {
//                                if (providerSnapshot.exists()) {
//                                    String providerName = providerSnapshot.child("fullName").getValue(String.class); // Fetch fullName
//                                    document.setProviderName(providerName); // Set the providerName in the document
//                                } else {
//                                    document.setProviderName("Unknown Provider"); // Fallback if provider is not found
//                                }
//                                documentList.add(document);  // Add document with providerName to the list
//                                adapter.notifyDataSetChanged();  // Notify adapter to update the list
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.e("ProviderFetchError", "Error fetching provider name: " + error.getMessage());
//                            }
//                        });
//                    }
//                }
//                dialog.dismiss(); // Dismiss the loading dialog once all documents are processed
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                dialog.dismiss();
//                Log.e("ShowPendingDocuments", "Database error: " + error.getMessage());
//            }
//        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                documentList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Document document = itemSnapshot.getValue(Document.class);

                    if (document != null && "Pending".equalsIgnoreCase(document.getStatus())) {
                        document.setDocumentId(itemSnapshot.getKey());  // Set documentId from the Firebase key

                        String providerId = document.getProviderId();
                        DatabaseReference providerRef = FirebaseDatabase.getInstance().getReference("providers").child(providerId);
                        providerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot providerSnapshot) {
                                if (providerSnapshot.exists()) {
                                    String providerName = providerSnapshot.child("fullName").getValue(String.class);
                                    document.setProviderName(providerName); // Set providerName in document
                                } else {
                                    document.setProviderName("Unknown Provider");
                                }
                                documentList.add(document);  // Add document with providerName to the list
                                adapter.notifyDataSetChanged();  // Notify adapter to update the list
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("ProviderFetchError", "Error fetching provider name: " + error.getMessage());
                            }
                        });
                    }
                }
                dialog.dismiss(); // Dismiss the loading dialog once all documents are processed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e("ShowPendingDocuments", "Database error: " + error.getMessage());
            }
        });


    }
}
