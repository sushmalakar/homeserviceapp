package com.sushmitamalakar.providerapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.providerapp.adapter.ServiceAdapter;
import com.sushmitamalakar.providerapp.model.Provider;
import com.sushmitamalakar.providerapp.model.Service;

import java.util.ArrayList;
import java.util.HashMap;

public class ProviderDashboardActivity extends AppCompatActivity {
    private DrawerLayout providerDrawerLayout;
    private ImageButton toggleImageButton;
    private NavigationView navigationView;
    private TextView userNameTextView, userEmailTextView;
    private ImageView profileImageView;
    private GridView servicesGridView;
    private ServiceAdapter serviceAdapter;
    private ArrayList<Service> serviceList;
    private ArrayList<Service> originalServiceList;
    private ArrayList<DataSnapshot> serviceSnapshots = new ArrayList<>();
    private SearchView searchView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        // Initialize Firebase authentication and database reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("providers");

        providerDrawerLayout = findViewById(R.id.providerDrawerLayout);
        toggleImageButton = findViewById(R.id.toggleImageButton);
        navigationView = findViewById(R.id.navigationView);

        // Load header views from NavigationView's header layout
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.userNameTextView);
        userEmailTextView = headerView.findViewById(R.id.userEmailTextView);
        profileImageView = headerView.findViewById(R.id.profileImageView);

        // Initialize GridView and adapter
        servicesGridView = findViewById(R.id.servicesGridView);
        serviceList = new ArrayList<>();
        originalServiceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(ProviderDashboardActivity.this, serviceList);
        servicesGridView.setAdapter(serviceAdapter);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        loadUserData();
        fetchServices();

        servicesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the Firebase-generated serviceId
                String serviceId = getFirebaseKeyFromSnapshot(position);

                // Check document verification status before showing charge dialog
                checkDocumentVerificationStatus(serviceId);
            }
        });

        toggleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                providerDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.myProfileItem) {
                    openProfileActivity();
                    return true;
                } else if (id == R.id.logoutItem) {
                    handleLogout();
                    return true;
                } else if (id == R.id.myDocumentsItem) {
                    openDocument();
                    return true;
                }else if (id == R.id.locationItem) {
                    openLocation();
                    return true;
                }
                providerDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    // Method to check document verification status
    private void checkDocumentVerificationStatus(String serviceId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String providerId = currentUser.getUid();
            DatabaseReference documentRef = FirebaseDatabase.getInstance().getReference("documents");

            documentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean documentFound = false;

                    for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
                        String docProviderId = documentSnapshot.child("providerId").getValue(String.class);

                        // Check if this document belongs to the current provider
                        if (providerId.equals(docProviderId)) {
                            documentFound = true;
                            String status = documentSnapshot.child("status").getValue(String.class);

                            if ("Verified".equalsIgnoreCase(status)) {
                                showChargeInputDialog(serviceId);
                            } else {
                                showVerificationRequiredDialog();
                            }
                            break;
                        }
                    }

                    if (!documentFound) {
                        Log.d("ProviderDashboard", "No document found for providerId: " + providerId);
                        showVerificationRequiredDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProviderDashboardActivity.this, "Error checking document verification status", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    // Helper method to show verification required dialog
    private void showVerificationRequiredDialog() {
        new AlertDialog.Builder(ProviderDashboardActivity.this)
                .setTitle("Verification Required")
                .setMessage("Your document is not verified. You cannot add a service charge.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to fetch serviceId from position
    private String getFirebaseKeyFromSnapshot(int position) {
        DataSnapshot snapshot = serviceSnapshots.get(position);
        return snapshot.getKey();
    }

    private void showChargeInputDialog(String serviceId) {
        new AlertDialog.Builder(ProviderDashboardActivity.this)
                .setTitle("Confirm Service")
                .setMessage("Do you want to add this service?")
                .setPositiveButton("Yes", (dialogInterface, i) -> showServiceChargeDialog(serviceId))
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void showServiceChargeDialog(String serviceId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_service_charge, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProviderDashboardActivity.this);
        builder.setView(dialogView);

        EditText chargeEditText = dialogView.findViewById(R.id.chargeEditText);
        TextView chargeNoteTextView = dialogView.findViewById(R.id.chargeNoteTextView);
        Button saveChargeButton = dialogView.findViewById(R.id.saveChargeButton);

        // Set the note below Hourly Charge
        chargeNoteTextView.setText("Note: This charge is calculated on an hourly basis.");

        AlertDialog dialog = builder.create();
        dialog.show();

        saveChargeButton.setOnClickListener(v -> {
            String enteredCharge = chargeEditText.getText().toString().trim();
            if (!enteredCharge.isEmpty()) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    String providerId = currentUser.getUid();
                    saveServiceChargeToFirebase(serviceId, providerId, enteredCharge);
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(ProviderDashboardActivity.this, "Please enter a valid charge", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveServiceChargeToFirebase(String serviceId, String providerId, String charge) {
        DatabaseReference serviceChargeRef = FirebaseDatabase.getInstance().getReference("service_charge");
        String chargeId = serviceChargeRef.push().getKey();

        if (chargeId != null) {
            HashMap<String, Object> chargeMap = new HashMap<>();
            chargeMap.put("serviceId", serviceId);
            chargeMap.put("providerId", providerId);
            chargeMap.put("charge", charge);

            serviceChargeRef.child(chargeId).setValue(chargeMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProviderDashboardActivity.this, "Charge saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProviderDashboardActivity.this, "Failed to save charge", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void searchList(String text) {
        ArrayList<Service> filteredList = new ArrayList<>();

        for (Service service : originalServiceList) {
            if (service.getServiceTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(service);
            }
        }

        serviceAdapter.searchServiceList(filteredList);
        serviceAdapter.notifyDataSetChanged();
    }

    private void fetchServices() {
        DatabaseReference servicesReference = FirebaseDatabase.getInstance().getReference("services");
        servicesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceList.clear();
                originalServiceList.clear();
                serviceSnapshots.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String serviceId = snapshot.getKey();
                    Service service = snapshot.getValue(Service.class);

                    if (service != null) {
                        serviceList.add(service);
                        originalServiceList.add(service);
                        serviceSnapshots.add(snapshot);
                    }
                }

                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProviderDashboardActivity", "Failed to load services: " + databaseError.getMessage());
                Toast.makeText(ProviderDashboardActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Provider provider = dataSnapshot.getValue(Provider.class);
                        if (provider != null) {
                            userNameTextView.setText(provider.getFullName());
                            userEmailTextView.setText(provider.getEmail());
                            if (provider.getImageUrl() != null && !provider.getImageUrl().isEmpty()) {
                                Glide.with(ProviderDashboardActivity.this).load(provider.getImageUrl()).into(profileImageView);
                            } else {
                                profileImageView.setImageResource(R.drawable.user_icon);
                            }
                        }
                    } else {
                        Toast.makeText(ProviderDashboardActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("UserDashboardActivity", "Failed to load user data: " + databaseError.getMessage());
                    Toast.makeText(ProviderDashboardActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void openProfileActivity() {
        Log.d("UserDashboardActivity", "Opening ProfileActivity");
        startActivity(new Intent(ProviderDashboardActivity.this, ProfileActivity.class));
        providerDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void handleLogout() {
        auth.signOut();
        startActivity(new Intent(ProviderDashboardActivity.this, LoginActivity.class));
        finish();
    }

    private void openDocument() {
        Log.d("ProviderDashboardActivity", "Opening DocumentUploadActivity");
        startActivity(new Intent(ProviderDashboardActivity.this, DocumentUploadActivity.class));
        providerDrawerLayout.closeDrawer(GravityCompat.START);
    }
    private void openLocation() {
        Log.d("ProviderDashboardActivity", "Opening MapActivity");
        startActivity(new Intent(ProviderDashboardActivity.this, MapActivity.class));
        providerDrawerLayout.closeDrawer(GravityCompat.START);
    }

}
