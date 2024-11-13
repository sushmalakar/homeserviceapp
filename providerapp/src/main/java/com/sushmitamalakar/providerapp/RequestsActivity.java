package com.sushmitamalakar.providerapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.providerapp.adapter.RequestsAdapter;
import com.sushmitamalakar.providerapp.databinding.ActivityRequestsBinding;
import com.sushmitamalakar.providerapp.model.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends DrawerBaseActivity {
    ActivityRequestsBinding activityRequestsBinding;
    private RecyclerView requestsRecyclerView;
    private RequestsAdapter requestsAdapter;
    private List<ServiceRequest> requestList;
    private DatabaseReference databaseReference;
    private String providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRequestsBinding = ActivityRequestsBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Document Upload");
        setContentView(activityRequestsBinding.getRoot());

        providerId = getIntent().getStringExtra("providerId");

        if (providerId == null || providerId.isEmpty()) {
            Toast.makeText(this, "Provider ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("bookings");

        fetchRequests();
    }

    private void fetchRequests() {
        databaseReference.orderByChild("providerId").equalTo(providerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ServiceRequest request = snapshot.getValue(ServiceRequest.class);
                            if (request != null) {
                                // Set the booking ID (Firebase key) in the ServiceRequest object
                                request.setBookingId(snapshot.getKey());
                                Log.d("RequestsActivity", "Booking ID set: " + request.getBookingId());
                                fetchServiceDetails(request);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchServiceDetails(ServiceRequest request) {
        String serviceId = request.getServiceId();

        if (serviceId == null || serviceId.isEmpty()) {
            Log.e("RequestsActivity", "Service ID is null or empty for this request.");
            return;
        }

        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services").child(serviceId);
        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                if (serviceSnapshot.exists()) {
                    request.setServiceImage(serviceSnapshot.child("serviceImage").getValue(String.class));
                    request.setServiceTitle(serviceSnapshot.child("serviceTitle").getValue(String.class));
                }
                fetchUserDetails(request);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, "Failed to load service details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails(ServiceRequest request) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(request.getUserId());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    request.setUserName(userSnapshot.child("fullName").getValue(String.class));

                }
                requestList.add(request);
                requestsAdapter = new RequestsAdapter(RequestsActivity.this, requestList);
                requestsRecyclerView.setAdapter(requestsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
