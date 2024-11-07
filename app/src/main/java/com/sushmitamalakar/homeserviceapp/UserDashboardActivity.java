package com.sushmitamalakar.homeserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.sushmitamalakar.homeserviceapp.adapter.ServiceAdapter;
import com.sushmitamalakar.homeserviceapp.databinding.ActivityUserDashboardBinding;
import com.sushmitamalakar.homeserviceapp.model.Service;
import com.sushmitamalakar.homeserviceapp.model.User;

import java.util.ArrayList;

public class UserDashboardActivity extends DrawerBaseActivity {

    ActivityUserDashboardBinding activityUserDashboardBinding;
    private DrawerLayout userDrawerLayout;
    private ImageButton toggleImageButton;
    private NavigationView navigationView;
    private TextView userNameTextView, userEmailTextView;
    private ImageView profileImageView;
    private GridView servicesGridView;
    private ServiceAdapter serviceAdapter;
    private ArrayList<Service> serviceList;
    private ArrayList<Service> originalServiceList;
    private SearchView searchView;
    private FirebaseAuth auth;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference servicesDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserDashboardBinding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        allocateActivityTitle("User Dashboard");
        setContentView(activityUserDashboardBinding.getRoot());

        auth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        servicesDatabaseReference = FirebaseDatabase.getInstance().getReference("services");

        userDrawerLayout = findViewById(R.id.userDrawerLayout);
        toggleImageButton = findViewById(R.id.toggleImageButton);
        navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.userNameTextView);
        userEmailTextView = headerView.findViewById(R.id.userEmailTextView);
        profileImageView = headerView.findViewById(R.id.profileImageView);

        servicesGridView = findViewById(R.id.servicesGridView);
        serviceList = new ArrayList<>();
        originalServiceList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(UserDashboardActivity.this, serviceList);
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

        servicesGridView.setOnItemClickListener((parent, view, position, id) -> {
            Service selectedService = serviceList.get(position);
            if (selectedService.getServiceId() != null) {
                checkUserLocationBeforeProceeding(selectedService.getServiceId());
            } else {
                Toast.makeText(UserDashboardActivity.this, "Service ID is missing.", Toast.LENGTH_SHORT).show();
            }
        });

        toggleImageButton.setOnClickListener(v -> userDrawerLayout.openDrawer(GravityCompat.START));
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
        servicesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceList.clear();
                originalServiceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        service.setServiceId(snapshot.getKey()); // Set serviceId using Firebase key
                        serviceList.add(service);
                        originalServiceList.add(service);
                    }
                }
                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserDashboardActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            userNameTextView.setText(user.getFullName());
                            userEmailTextView.setText(user.getEmail());
                            Glide.with(UserDashboardActivity.this).load(user.getImageUrl()).into(profileImageView);
                        }
                    } else {
                        Toast.makeText(UserDashboardActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserDashboardActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkUserLocationBeforeProceeding(String serviceId) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference locationRef = userDatabaseReference.child(userId).child("location");

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent intent = new Intent(UserDashboardActivity.this, ProviderListActivity.class);
                    intent.putExtra("serviceId", serviceId);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserDashboardActivity.this, "Please set your location first.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserDashboardActivity.this, MapActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDashboardActivity.this, "Error checking location", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
