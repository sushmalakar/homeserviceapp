package com.sushmitamalakar.homeserviceapp;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceapp.adapter.ProviderAdapter;
import com.sushmitamalakar.homeserviceapp.databinding.ActivityProfileBinding;
import com.sushmitamalakar.homeserviceapp.databinding.ActivityProviderListBinding;
import com.sushmitamalakar.homeserviceapp.model.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProviderListActivity extends DrawerBaseActivity {

    ActivityProviderListBinding activityProviderListBinding;

    private RecyclerView providerRecyclerView;
    private LinearLayout filterButton;
    private ProviderAdapter providerAdapter;
    private ArrayList<Provider> providerList;
    private DatabaseReference serviceChargeReference;
    private String serviceId, userId;
    private Location userLocation;
    private static final String TAG = "ProviderListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProviderListBinding = ActivityProviderListBinding.inflate(getLayoutInflater());
        setContentView(activityProviderListBinding.getRoot());
        serviceId = getIntent().getStringExtra("serviceId");
        userId = getIntent().getStringExtra("userId");

        providerRecyclerView = findViewById(R.id.providerRecyclerView);
        providerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filterButton = findViewById(R.id.filterButton);


        providerList = new ArrayList<>();
        providerAdapter = new ProviderAdapter(this, providerList, serviceId, userId);
        providerRecyclerView.setAdapter(providerAdapter);

        // Bring the filter button to the front
        filterButton.bringToFront();

        // Set click listener for the filter button
        filterButton.setOnClickListener(this::openFilterDialog);


        // Fetch user location
        fetchUserLocation();

        if (serviceId != null) {
            fetchProviders(serviceId);
        } else {
            Toast.makeText(this, "No service selected", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchUserLocation() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("location");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitude = snapshot.child("latitude").getValue(Double.class);
                Double longitude = snapshot.child("longitude").getValue(Double.class);
                if (latitude != null && longitude != null) {
                    userLocation = new Location("User");
                    userLocation.setLatitude(latitude);
                    userLocation.setLongitude(longitude);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user location: " + error.getMessage());
            }
        });
    }

    private void fetchProviders(String serviceId) {
        serviceChargeReference = FirebaseDatabase.getInstance().getReference("service_charge");

        serviceChargeReference.orderByChild("serviceId").equalTo(serviceId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        providerList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String providerId = snapshot.child("providerId").getValue(String.class);
                                String charge = String.valueOf(snapshot.child("charge").getValue());

                                if (providerId != null && charge != null) {
                                    fetchProviderDetails(providerId, charge);
                                }
                            }
                        } else {
                            Toast.makeText(ProviderListActivity.this, "No providers found for this service", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProviderListActivity.this, "Failed to load providers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchProviderDetails(String providerId, String charge) {
        DatabaseReference providerReference = FirebaseDatabase.getInstance().getReference("providers").child(providerId);

        providerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot providerSnapshot) {
                String fullName = providerSnapshot.child("fullName").getValue(String.class);
                String imageUrl = providerSnapshot.child("imageUrl").getValue(String.class);

                Double latitude = providerSnapshot.child("location").child("latitude").getValue(Double.class);
                Double longitude = providerSnapshot.child("location").child("longitude").getValue(Double.class);

                if (latitude != null && longitude != null) {
                    getShortAddress(latitude, longitude, fullName, charge, imageUrl, providerId);
                } else {
                    Provider.Location location = new Provider.Location(0.0, 0.0);
                    Provider provider = new Provider(providerId, fullName, charge, location, imageUrl);
                    providerList.add(provider);
                    providerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProviderListActivity.this, "Failed to load provider details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getShortAddress(double latitude, double longitude, String fullName, String charge, String imageUrl, String providerId) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String shortAddress;
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();

                shortAddress = (city != null ? city : "") +
                        (state != null ? ", " + state : "") +
                        (country != null ? ", " + country : "");
            } else {
                shortAddress = "Location not found";
            }

            Provider.Location location = new Provider.Location(latitude, longitude);
            Provider provider = new Provider(providerId, fullName, charge, location, imageUrl);
            providerList.add(provider);
            providerAdapter.updateProviderLocation(providerId, shortAddress);
        } catch (IOException e) {
            Log.e(TAG, "Geocoding failed: " + e.getMessage());
        }
    }

    public void openFilterDialog(View view) {
        String[] filterOptions = {"Sort by Price", "Sort by Near Me"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Options");
        builder.setItems(filterOptions, (dialog, which) -> {
            if (which == 0) {
                sortByPrice();
            } else if (which == 1) {
                sortByNearMe();
            }
        });
        builder.show();
    }

    private void sortByPrice() {
        Collections.sort(providerList, Comparator.comparing(Provider::getCharge));
        providerAdapter.notifyDataSetChanged();
    }

    private void sortByNearMe() {
        if (userLocation != null) {
            Collections.sort(providerList, (p1, p2) -> {
                double distance1 = haversine(userLocation.getLatitude(), userLocation.getLongitude(),
                        p1.getLocation().getLatitude(), p1.getLocation().getLongitude());
                double distance2 = haversine(userLocation.getLatitude(), userLocation.getLongitude(),
                        p2.getLocation().getLatitude(), p2.getLocation().getLongitude());
                return Double.compare(distance1, distance2);
            });
            providerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "User location not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
