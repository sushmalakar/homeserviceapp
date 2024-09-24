package com.sushmitamalakar.providerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
                }
                providerDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    // Method to search services based on query
    public void searchList(String text) {
        ArrayList<Service> filteredList = new ArrayList<>();

        // Filter from the original service list to ensure it's not modified
        for (Service service : originalServiceList) {
            if (service.getServiceTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(service);
            }
        }

        // Update the service list in the adapter
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

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        serviceList.add(service);  // Add service to the current list
                        originalServiceList.add(service);  // Add to original list
                    }
                }

                serviceAdapter.notifyDataSetChanged();  // Notify adapter of data changes
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
}

