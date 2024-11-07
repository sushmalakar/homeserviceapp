package com.sushmitamalakar.homeserviceapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceapp.adapter.ProviderAdapter;
import com.sushmitamalakar.homeserviceapp.model.Provider;

import java.util.ArrayList;

//public class ProviderListActivity extends AppCompatActivity {
//
//    private RecyclerView providerRecyclerView;
//    private ProviderAdapter providerAdapter;
//    private ArrayList<Provider> providerList;
//    private DatabaseReference serviceChargeReference;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_provider_list);
//
//        providerRecyclerView = findViewById(R.id.providerRecyclerView);
//        providerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        providerList = new ArrayList<>();
//        providerAdapter = new ProviderAdapter(this, providerList);
//        providerRecyclerView.setAdapter(providerAdapter);
//
//        String serviceId = getIntent().getStringExtra("serviceId");
//        if (serviceId != null) {
//            fetchProviders(serviceId);
//        } else {
//            Toast.makeText(this, "No service selected", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    private void fetchProviders(String serviceId) {
//        serviceChargeReference = FirebaseDatabase.getInstance().getReference("service_charge");
//
//        serviceChargeReference.orderByChild("serviceId").equalTo(serviceId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        providerList.clear();
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                String providerId = snapshot.child("providerId").getValue(String.class);
//                                String charge = snapshot.child("charge").getValue(String.class);
//
//                                if (providerId != null && charge != null) {
//                                    Provider provider = new Provider(providerId, charge);
//                                    providerList.add(provider);
//                                }
//                            }
//                            providerAdapter.notifyDataSetChanged();
//                        } else {
//                            Toast.makeText(ProviderListActivity.this, "No providers found for this service", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(ProviderListActivity.this, "Failed to load providers", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceapp.adapter.ProviderAdapter;
import com.sushmitamalakar.homeserviceapp.model.Provider;

import java.util.ArrayList;

public class ProviderListActivity extends AppCompatActivity {

    private RecyclerView providerRecyclerView;
    private ProviderAdapter providerAdapter;
    private ArrayList<Provider> providerList;
    private DatabaseReference serviceChargeReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);

        providerRecyclerView = findViewById(R.id.providerRecyclerView);
        providerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        providerList = new ArrayList<>();
        providerAdapter = new ProviderAdapter(this, providerList);
        providerRecyclerView.setAdapter(providerAdapter);

        String serviceId = getIntent().getStringExtra("serviceId");
        if (serviceId != null) {
            fetchProviders(serviceId);
        } else {
            Toast.makeText(this, "No service selected", Toast.LENGTH_SHORT).show();
            finish();
        }
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
                                String charge = String.valueOf(snapshot.child("charge").getValue()); // Convert charge to String

                                if (providerId != null && charge != null) {
                                    // Fetch additional provider details from providers node
                                    DatabaseReference providerReference = FirebaseDatabase.getInstance().getReference("providers").child(providerId);
                                    providerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot providerSnapshot) {
                                            String fullName = providerSnapshot.child("fullName").getValue(String.class);
                                            String imageUrl = providerSnapshot.child("imageUrl").getValue(String.class);

                                            // Retrieve nested location with latitude and longitude
                                            Double latitude = providerSnapshot.child("location").child("latitude").getValue(Double.class);
                                            Double longitude = providerSnapshot.child("location").child("longitude").getValue(Double.class);

                                            Provider.Location location = null;
                                            if (latitude != null && longitude != null) {
                                                location = new Provider.Location(latitude, longitude);
                                            }

                                            if (fullName != null && imageUrl != null && location != null) {
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
}
