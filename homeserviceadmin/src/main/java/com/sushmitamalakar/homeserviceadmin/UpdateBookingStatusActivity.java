package com.sushmitamalakar.homeserviceadmin;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceadmin.databinding.ActivityUpdateBookingStatusBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UpdateBookingStatusActivity extends AppCompatActivity {

    private ActivityUpdateBookingStatusBinding binding;
    private DatabaseReference bookingsRef, servicesRef, usersRef;
    private String bookingId, serviceId, userId, currentStatus;
    private Double latitude, longitude;
    private static final String TAG = "UpdateBookingStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBookingStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase references
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        servicesRef = FirebaseDatabase.getInstance().getReference("services");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve data from intent
        Intent intent = getIntent();
        bookingId = intent.getStringExtra("bookingId");
        serviceId = intent.getStringExtra("serviceId");
        userId = intent.getStringExtra("userId");
        currentStatus = intent.getStringExtra("status");

        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Booking ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display current status
        binding.bookingStatusTextView.setText("Status: " + currentStatus);
        updateStatusColor(currentStatus);

        // Fetch details
        fetchBookingDetails();
        fetchServiceDetails();
        fetchUserDetails();
    }

    // Method to fetch booking details
    private void fetchBookingDetails() {
        bookingsRef.child(bookingId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date = snapshot.child("date").getValue(String.class);
                String time = snapshot.child("time").getValue(String.class);
                String charge = snapshot.child("charge").getValue(String.class);
                String message = snapshot.child("message").getValue(String.class);

                binding.bookingDateTextView.setText("Date: " + (date != null ? date : "N/A"));
                binding.bookingTimeTextView.setText("Time: " + (time != null ? time : "N/A"));
                binding.bookingChargeTextView.setText("Charge: Rs " + (charge != null ? charge : "N/A"));
                binding.userMessageTextView.setText("Message: " + (message != null ? message : "No message"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch booking details: " + error.getMessage());
                Toast.makeText(UpdateBookingStatusActivity.this, "Error fetching booking details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch service details
    private void fetchServiceDetails() {
        servicesRef.child(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String serviceTitle = snapshot.child("serviceTitle").getValue(String.class);
                String serviceImage = snapshot.child("serviceImage").getValue(String.class);

                binding.serviceNameTextView.setText(serviceTitle != null ? serviceTitle : "Unknown Service");
                Glide.with(UpdateBookingStatusActivity.this)
                        .load(serviceImage != null ? serviceImage : R.drawable.icon_error)
                        .into(binding.serviceImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch service details: " + error.getMessage());
            }
        });
    }

    // Method to fetch user details including location
    private void fetchUserDetails() {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.child("fullName").getValue(String.class);
                String contact = snapshot.child("mobileNo").getValue(String.class);
                latitude = snapshot.child("location").child("latitude").getValue(Double.class);
                longitude = snapshot.child("location").child("longitude").getValue(Double.class);

                binding.userNameTextView.setText("User: " + (fullName != null ? fullName : "Unknown User"));
                binding.userContactTextView.setText("Contact: " + (contact != null ? contact : "No contact available"));

                String address = getAddressFromCoordinates(latitude, longitude);
                binding.userAddressTextView.setText("Address: " + address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user details: " + error.getMessage());
            }
        });
    }

    // Method to get human-readable address from latitude and longitude
    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return (addresses != null && !addresses.isEmpty()) ? addresses.get(0).getAddressLine(0) : "Location not available";
        } catch (IOException e) {
            Log.e(TAG, "Geocoding failed: " + e.getMessage());
            return "Unable to determine address";
        }
    }

    // Method to update status color
    private void updateStatusColor(String status) {
        int color = status.equals("Accepted") ? Color.GREEN : status.equals("Rejected") ? Color.RED : Color.BLUE;
        binding.bookingStatusTextView.setTextColor(color);
    }

    // Method to navigate back
    private void navigateToAdminDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
