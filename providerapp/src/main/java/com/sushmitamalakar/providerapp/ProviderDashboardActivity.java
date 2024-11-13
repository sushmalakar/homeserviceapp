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

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("providers");

        providerDrawerLayout = findViewById(R.id.providerDrawerLayout);
        toggleImageButton = findViewById(R.id.toggleImageButton);
        navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.userNameTextView);
        userEmailTextView = headerView.findViewById(R.id.userEmailTextView);
        profileImageView = headerView.findViewById(R.id.profileImageView);

        servicesGridView = findViewById(R.id.servicesGridView);
        serviceList = new ArrayList<>();
        originalServiceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(this, serviceList);
        servicesGridView.setAdapter(serviceAdapter);

        searchView = findViewById(R.id.searchView);
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

        servicesGridView.setOnItemClickListener((parent, view, position, id) -> {
            String serviceId = getFirebaseKeyFromSnapshot(position);
            checkDocumentVerificationStatus(serviceId);
        });

        toggleImageButton.setOnClickListener(v -> providerDrawerLayout.openDrawer(GravityCompat.START));

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
                }else if(id == R.id.requestsItem){
                    openRequests();
                    return true;
                }else if(id == R.id.myservicesItem){
                    openServices();
                    return true;
                }else if(id == R.id.dashboardItem){
                    openDashboard();
                    return true;
                }
                providerDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
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
                            Glide.with(ProviderDashboardActivity.this).load(provider.getImageUrl()).into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProviderDashboardActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void searchList(String text) {
        ArrayList<Service> filteredList = new ArrayList<>();
        for (Service service : originalServiceList) {
            if (service.getServiceTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(service);
            }
        }
        serviceAdapter.searchServiceList(filteredList);
        serviceAdapter.notifyDataSetChanged();
    }

    private String getFirebaseKeyFromSnapshot(int position) {
        return serviceSnapshots.get(position).getKey();
    }

    private void checkDocumentVerificationStatus(String serviceId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String providerId = currentUser.getUid();
            DatabaseReference documentRef = FirebaseDatabase.getInstance().getReference("documents");

            documentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot documentSnapshot : snapshot.getChildren()) {
                        String docProviderId = documentSnapshot.child("providerId").getValue(String.class);
                        if (providerId.equals(docProviderId)) {
                            String status = documentSnapshot.child("status").getValue(String.class);
                            if ("Verified".equalsIgnoreCase(status)) {
                                checkExistingServiceCharge(serviceId);
                                return;
                            }
                        }
                    }
                    showVerificationRequiredDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProviderDashboardActivity.this, "Error checking document status", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String existingChargeId = null;

    private void checkExistingServiceCharge(String serviceId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String providerId = currentUser.getUid();
            DatabaseReference serviceChargeRef = FirebaseDatabase.getInstance().getReference("service_charge");

            serviceChargeRef.orderByChild("serviceId").equalTo(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot chargeSnapshot : snapshot.getChildren()) {
                        if (providerId.equals(chargeSnapshot.child("providerId").getValue(String.class))) {
                            existingChargeId = chargeSnapshot.getKey(); // Store the existing chargeId
                            String existingCharge = chargeSnapshot.child("charge").getValue(String.class);
                            showUpdateChargeDialog(serviceId, existingCharge);
                            return;
                        }
                    }
                    existingChargeId = null; // No existing charge found
                    showServiceChargeDialog(serviceId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProviderDashboardActivity.this, "Error checking charge", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void showVerificationRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Verification Required")
                .setMessage("Your document is not verified. You cannot add a service charge.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showUpdateChargeDialog(String serviceId, String existingCharge) {
        new AlertDialog.Builder(this)
                .setTitle("Charge Exists")
                .setMessage("Current charge: " + existingCharge + "\nDo you want to update it?")
                .setPositiveButton("Yes", (dialog, which) -> showServiceChargeDialog(serviceId))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showServiceChargeDialog(String serviceId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_service_charge, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        EditText chargeEditText = dialogView.findViewById(R.id.chargeEditText);
        Button saveChargeButton = dialogView.findViewById(R.id.saveChargeButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        saveChargeButton.setOnClickListener(v -> {
            String charge = chargeEditText.getText().toString().trim();
            if (!charge.isEmpty()) {
                saveServiceChargeToFirebase(serviceId, auth.getCurrentUser().getUid(), charge);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a valid charge", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveServiceChargeToFirebase(String serviceId, String providerId, String charge) {
        DatabaseReference serviceChargeRef = FirebaseDatabase.getInstance().getReference("service_charge");
        String chargeId = (existingChargeId != null) ? existingChargeId : serviceChargeRef.push().getKey(); // Use existingChargeId if available

        if (chargeId != null) {
            HashMap<String, Object> chargeMap = new HashMap<>();
            chargeMap.put("serviceId", serviceId);
            chargeMap.put("providerId", providerId);
            chargeMap.put("charge", charge);

            serviceChargeRef.child(chargeId).setValue(chargeMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Charge updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update charge", Toast.LENGTH_SHORT).show();
                        }
                        existingChargeId = null; // Reset after updating
                    });
        }
    }


    private void openProfileActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void handleLogout() {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void openDocument() {
        startActivity(new Intent(this, DocumentUploadActivity.class));
    }

    private void openLocation() {
        startActivity(new Intent(this, MapActivity.class));
    }

    private void openRequests() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String providerId = currentUser.getUid();
            Log.d("ProviderDashboardActivity", "Opening RequestsActivity with providerId: " + providerId);

            Intent intent = new Intent(ProviderDashboardActivity.this, RequestsActivity.class);
            intent.putExtra("providerId", providerId);
            startActivity(intent);
            providerDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
    private void openServices() {
        startActivity(new Intent(this, ServicesActivity.class));
    }

    private void openDashboard() {
        startActivity(new Intent(this, ProviderDashboardActivity.class));
    }
}
