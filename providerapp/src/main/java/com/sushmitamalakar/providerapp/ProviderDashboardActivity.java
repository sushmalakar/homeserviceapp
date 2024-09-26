package com.sushmitamalakar.providerapp;

import android.app.Dialog;
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
    private ArrayList<Service> originalServiceList;  // Store the original list
    private ArrayList<DataSnapshot> serviceSnapshots = new ArrayList<>();  // Store Firebase snapshots
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
        originalServiceList = new ArrayList<>();  // Initialize original service list
        serviceAdapter = new ServiceAdapter(ProviderDashboardActivity.this, serviceList);
        servicesGridView.setAdapter(serviceAdapter);

        // Initialize SearchView and configure search behavior
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

        // Handle the click on Service icons
        servicesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the Firebase-generated serviceId
                String serviceId = getFirebaseKeyFromSnapshot(position);

                // Show the dialog and pass the serviceId to save charge
                showChargeInputDialog(serviceId);
            }
        });

        // Drawer toggle button listener
        toggleImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                providerDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Navigation drawer item click listener
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
                }
                providerDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    // Method to fetch serviceId from position
    private String getFirebaseKeyFromSnapshot(int position) {
        DataSnapshot snapshot = serviceSnapshots.get(position);
        return snapshot.getKey();  // This gets the unique Firebase key
    }

    // Method to show dialog to enter service charge
    private void showChargeInputDialog(String serviceId) {
        // Create and configure the dialog
        Dialog dialog = new Dialog(ProviderDashboardActivity.this);
        dialog.setContentView(R.layout.dialog_service_charge);

        // Find views from the dialog layout
        EditText chargeEditText = dialog.findViewById(R.id.chargeEditText);
        Button saveChargeButton = dialog.findViewById(R.id.saveChargeButton);

        // Set the click listener for the save button
        saveChargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered charge
                String enteredCharge = chargeEditText.getText().toString().trim();

                if (!enteredCharge.isEmpty()) {
                    // Get the logged-in provider's ID
                    FirebaseUser currentUser = auth.getCurrentUser();
                    if (currentUser != null) {
                        String providerId = currentUser.getUid();

                        // Save the service charge to Firebase using serviceId and providerId
                        saveServiceChargeToFirebase(serviceId, providerId, enteredCharge);

                        // Close the dialog
                        dialog.dismiss();
                    } else {
                        Toast.makeText(ProviderDashboardActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProviderDashboardActivity.this, "Please enter a valid charge", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Show the dialog
        dialog.show();
    }

    // Method to save service charge to Firebase
    private void saveServiceChargeToFirebase(String serviceId, String providerId, String charge) {
        // Create a reference to the "service_charge" node in Firebase
        DatabaseReference serviceChargeRef = FirebaseDatabase.getInstance().getReference("service_charge");

        // Create a unique key for this entry
        String chargeId = serviceChargeRef.push().getKey();

        if (chargeId != null) {
            // Create a map to hold the charge details
            HashMap<String, Object> chargeMap = new HashMap<>();
            chargeMap.put("serviceId", serviceId);
            chargeMap.put("providerId", providerId);
            chargeMap.put("charge", charge);

            // Save the charge details in the "service_charge" node
            serviceChargeRef.child(chargeId).setValue(chargeMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProviderDashboardActivity.this, "Charge saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProviderDashboardActivity.this, "Failed to save charge", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ProviderDashboardActivity.this, "Failed to create charge ID", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to search services based on query
    public void searchList(String text) {
        ArrayList<Service> filteredList = new ArrayList<>();

        for (Service service : originalServiceList) {
            if (service.getServiceTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(service);
            }
        }

        serviceAdapter.searchServiceList(filteredList);
        serviceAdapter.notifyDataSetChanged();  // Notify the adapter of the changes
    }

    // Fetch all services from Firebase
    private void fetchServices() {
        DatabaseReference servicesReference = FirebaseDatabase.getInstance().getReference("services");
        servicesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceList.clear();  // Clear current list
                originalServiceList.clear();  // Clear original list
                serviceSnapshots.clear(); // Clear snapshots list

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the serviceId (Firebase key)
                    String serviceId = snapshot.getKey();  // This gets the unique Firebase key

                    // Fetch the service object from the snapshot
                    Service service = snapshot.getValue(Service.class);

                    // Add the service to the list
                    if (service != null) {
                        serviceList.add(service);
                        originalServiceList.add(service);  // Add serviceId to track the service
                        serviceSnapshots.add(snapshot);  // Store the snapshot for later retrieval
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

    // Method to load user data from Firebase and update the UI
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
                                profileImageView.setImageResource(R.drawable.user_icon); // default image
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

    // Method to open the Profile Activity
    private void openProfileActivity() {
        Log.d("UserDashboardActivity", "Opening ProfileActivity");
        startActivity(new Intent(ProviderDashboardActivity.this, ProfileActivity.class));
        providerDrawerLayout.closeDrawer(GravityCompat.START);
    }

    // Method to handle user logout
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
}
