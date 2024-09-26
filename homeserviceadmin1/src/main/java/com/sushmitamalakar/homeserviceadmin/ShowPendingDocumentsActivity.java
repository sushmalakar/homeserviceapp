//package com.yourapp; // Adjust the package name
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.sushmitamalakar.providerapp.model.Document; // Adjust the import path
//
//import java.util.ArrayList;
//import java.util.List;
//
//import edu.divyagyan.homeserviceadmin.DocumentAdapter;
//
//public class ShowPendingDocumentsActivity extends AppCompatActivity {
//
//    private RecyclerView servicesRecyclerView;
//    private DocumentAdapter documentAdapter;
//    private List<Document> documentList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.show_pending_documents); // Your layout file containing RecyclerView
//
//        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
//        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        documentAdapter = new DocumentAdapter(documentList, new DocumentAdapter.OnDocumentClickListener() {
//            @Override
//            public void onDocumentClick(Document document) {
//                // Handle document click
//                Toast.makeText(ShowPendingDocumentsActivity.this, "Clicked: " + document.getName(), Toast.LENGTH_SHORT).show();
//                // Navigate to document detail page or approval/rejection
//                // Intent intent = new Intent(ShowPendingDocumentsActivity.this, DocumentDetailActivity.class);
//                // intent.putExtra("DOCUMENT_ID", document.getId()); // Pass document ID to detail page
//                // startActivity(intent);
//            }
//        });
//
//        servicesRecyclerView.setAdapter(documentAdapter);
//
//        loadPendingDocuments();
//    }
//
//    private void loadPendingDocuments() {
//        DatabaseReference documentRef = FirebaseDatabase.getInstance().getReference("documents");
//        documentRef.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                documentList.clear();
//                for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
//                    Document document = documentSnapshot.getValue(Document.class);
//                    if (document != null) {
//                        documentList.add(document);
//                    }
//                }
//                documentAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ShowPendingDocumentsActivity.this, "Failed to load documents", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
