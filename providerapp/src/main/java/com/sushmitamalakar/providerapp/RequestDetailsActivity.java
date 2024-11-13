//package com.sushmitamalakar.providerapp;
//
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.graphics.Color;
//import android.location.Address;
//import android.location.Geocoder;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import com.bumptech.glide.Glide;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.sushmitamalakar.providerapp.databinding.ActivityRequestDetailsBinding;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//public class RequestDetailsActivity extends AppCompatActivity {
//
//    private ActivityRequestDetailsBinding binding;
//    private DatabaseReference bookingsRef;
//    private String bookingId;
//    private String serviceId;
//    private String status;
//    private Double latitude;
//    private Double longitude;
//
//    private static final String TAG = "RequestDetailsActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityRequestDetailsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Initialize Firebase reference
//        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
//
//        // Retrieve data from intent
//        bookingId = getIntent().getStringExtra("bookingId");
//        serviceId = getIntent().getStringExtra("serviceId");
//        String serviceTitle = getIntent().getStringExtra("serviceTitle");
//        String serviceImage = getIntent().getStringExtra("serviceImage");
//        String userName = getIntent().getStringExtra("userName");
//        String date = getIntent().getStringExtra("date");
//        String time = getIntent().getStringExtra("time");
//        String charge = getIntent().getStringExtra("charge");
//        status = getIntent().getStringExtra("status");
//        String message = getIntent().getStringExtra("message");
//
//        // Log the received bookingId for debugging
//        Log.d(TAG, "Received bookingId: " + bookingId);
//
//        if (bookingId == null || bookingId.isEmpty()) {
//            Toast.makeText(this, "Booking ID is missing", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Set data to UI elements
//        binding.serviceNameTextView.setText(serviceTitle);
//        binding.userNameTextView.setText("User: " + userName);
//        binding.bookingDateTextView.setText("Date: " + date);
//        binding.bookingTimeTextView.setText("Time: " + time);
//        binding.bookingChargeTextView.setText("Charge: Rs " + charge);
//        binding.bookingStatusTextView.setText("Status: " + status);
//        binding.userMessageTextView.setText("Message: " + message);
//
//        // Get the address from coordinates and display it
//        String address = getAddressFromCoordinates(latitude, longitude);
//        binding.userAddressTextView.setText("Address: " + address);
//
//        // Set the status text color based on the current status
//        updateStatusColor(status);
//
//        // Load the service image
//        Glide.with(this).load(serviceImage).into(binding.serviceImageView);
//
//        // Update button visibility based on current status
//        updateButtonVisibility(status);
//
//        // Accept button click listener
//        binding.acceptButton.setOnClickListener(v -> showConfirmationDialog("Accept"));
//
//        // Reject button click listener
//        binding.rejectButton.setOnClickListener(v -> showConfirmationDialog("Reject"));
//    }
//
//    // Method to get human-readable address from latitude and longitude
//    private String getAddressFromCoordinates(double latitude, double longitude) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address address = addresses.get(0);
//                return address.getAddressLine(0);
//            } else {
//                Log.e(TAG, "No address found for the given coordinates.");
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Geocoding failed: " + e.getMessage());
//        }
//        return "Unable to determine address. Please check your network connection.";
//    }
//
//
//    // Method to show confirmation dialog
//    private void showConfirmationDialog(String action) {
//        String message = action.equals("Accept") ?
//                "Are you sure you want to accept this booking?" :
//                "Are you sure you want to reject this booking?";
//
//        new AlertDialog.Builder(this)
//                .setTitle(action + " Booking")
//                .setMessage(message)
//                .setPositiveButton("Yes", (dialog, which) -> updateBookingStatus(action))
//                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
//
//    // Method to update booking status in Firebase
//    private void updateBookingStatus(String action) {
//        if (bookingId == null || bookingId.isEmpty()) {
//            Toast.makeText(this, "Booking ID is missing", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String newStatus = action.equals("Accept") ? "Accepted" : "Rejected";
//
//        // Directly access the booking entry using the booking ID
//        DatabaseReference bookingRef = bookingsRef.child(bookingId);
//
//        // Update the status of the booking entry
//        bookingRef.child("status").setValue(newStatus)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Booking status updated to " + newStatus, Toast.LENGTH_SHORT).show();
//                    binding.bookingStatusTextView.setText("Status: " + newStatus);
//                    updateStatusColor(newStatus);
//                    updateButtonVisibility(newStatus);
//
//                    // Redirect to ProviderDashboard
//                    navigateToProviderDashboard();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to update booking status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Error updating status: " + e.getMessage());
//                });
//    }
//
//    // Method to update the status text color
//    private void updateStatusColor(String status) {
//        if ("Accepted".equalsIgnoreCase(status)) {
//            binding.bookingStatusTextView.setTextColor(Color.parseColor("#4CAF50")); // Green color
//        } else if ("Rejected".equalsIgnoreCase(status)) {
//            binding.bookingStatusTextView.setTextColor(Color.parseColor("#F44336")); // Red color
//        } else {
//            binding.bookingStatusTextView.setTextColor(Color.parseColor("#000000")); // Black color for other statuses
//        }
//    }
//
//    // Method to navigate to ProviderDashboard
//    private void navigateToProviderDashboard() {
//        Intent intent = new Intent(RequestDetailsActivity.this, ProviderDashboardActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish(); // Close the current activity
//    }
//
//    // Method to update button visibility based on status
//    private void updateButtonVisibility(String status) {
//        if ("Accepted".equals(status) || "Rejected".equals(status)) {
//            binding.acceptButton.setVisibility(android.view.View.GONE);
//            binding.rejectButton.setVisibility(android.view.View.GONE);
//        } else {
//            binding.acceptButton.setVisibility(android.view.View.VISIBLE);
//            binding.rejectButton.setVisibility(android.view.View.VISIBLE);
//        }
//    }
//}
package com.sushmitamalakar.providerapp;

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
import com.sushmitamalakar.providerapp.databinding.ActivityRequestDetailsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RequestDetailsActivity extends AppCompatActivity {

    private ActivityRequestDetailsBinding binding;
    private DatabaseReference bookingsRef;
    private DatabaseReference usersRef;
    private String bookingId;
    private String userId;
    private String status;
    private Double latitude;
    private Double longitude;

    private static final String TAG = "RequestDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase references
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve data from intent
        bookingId = getIntent().getStringExtra("bookingId");
        userId = getIntent().getStringExtra("userId");
        String serviceTitle = getIntent().getStringExtra("serviceTitle");
        String serviceImage = getIntent().getStringExtra("serviceImage");
        String userName = getIntent().getStringExtra("userName");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String charge = getIntent().getStringExtra("charge");
        status = getIntent().getStringExtra("status");
        String message = getIntent().getStringExtra("message");

        // Log the received bookingId for debugging
        Log.d(TAG, "Received bookingId: " + bookingId);

        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Booking ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set data to UI elements
        binding.serviceNameTextView.setText(serviceTitle);
        binding.userNameTextView.setText("User: " + userName);
        binding.bookingDateTextView.setText("Date: " + date);
        binding.bookingTimeTextView.setText("Time: " + time);
        binding.bookingChargeTextView.setText("Charge: Rs " + charge);
        binding.bookingStatusTextView.setText("Status: " + status);
        binding.userMessageTextView.setText("Message: " + message);

        // Load the service image
        Glide.with(this).load(serviceImage).into(binding.serviceImageView);

        // Update status color and button visibility
        updateStatusColor(status);
        updateButtonVisibility(status);

        // Fetch and display the user's location
        fetchUserLocation();

        // Accept button click listener
        binding.acceptButton.setOnClickListener(v -> showConfirmationDialog("Accept"));

        // Reject button click listener
        binding.rejectButton.setOnClickListener(v -> showConfirmationDialog("Reject"));

        // Completed button click listener
        binding.completedButton.setOnClickListener(v -> showConfirmationDialog("Completed"));

    }

    // Method to fetch user’s location from Firebase
    private void fetchUserLocation() {
        usersRef.child(userId).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    longitude = dataSnapshot.child("longitude").getValue(Double.class);

                    Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);

                    // Get the address from coordinates and display it
                    String address = getAddressFromCoordinates(latitude, longitude);
                    binding.userAddressTextView.setText("Address: " + address);
                } else {
                    binding.userAddressTextView.setText("Address: Location not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestDetailsActivity.this, "Failed to load location: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to get human-readable address from latitude and longitude
    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            } else {
                Log.e(TAG, "No address found for the given coordinates.");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoding failed: " + e.getMessage());
        }
        return "Unable to determine address. Please check your network connection.";
    }

    // Method to show confirmation dialog
    private void showConfirmationDialog(String action) {
        String message;
        switch (action) {
            case "Accept":
                message = "Are you sure you want to accept this booking?";
                break;
            case "Reject":
                message = "Are you sure you want to reject this booking?";
                break;
            case "Completed":
                message = "Are you sure you want to mark this booking as completed?";
                break;
            default:
                message = "Are you sure?";
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle(action + " Booking")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> updateBookingStatus(action))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to update booking status in Firebase
// Method to update booking status in Firebase
    private void updateBookingStatus(String action) {
        String newStatus;
        switch (action) {
            case "Accept":
                newStatus = "Accepted";
                break;
            case "Reject":
                newStatus = "Rejected";
                break;
            case "Completed":
                newStatus = "Completed";
                break;
            default:
                newStatus = status;
                break;
        }

        DatabaseReference bookingRef = bookingsRef.child(bookingId);

        bookingRef.child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    binding.bookingStatusTextView.setText("Status: " + newStatus);
                    updateStatusColor(newStatus);
                    updateButtonVisibility(newStatus);
                    if ("Completed".equals(newStatus)) {
                        navigateToProviderDashboard();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }

    // Method to update the status text color
    private void updateStatusColor(String status) {
        if ("Accepted".equalsIgnoreCase(status)) {
            binding.bookingStatusTextView.setTextColor(Color.parseColor("#4CAF50")); // Green color
        } else if ("Rejected".equalsIgnoreCase(status)) {
            binding.bookingStatusTextView.setTextColor(Color.parseColor("#F44336")); // Red color
        } else if ("Completed".equalsIgnoreCase(status)) {
            binding.bookingStatusTextView.setTextColor(Color.parseColor("#2196F3")); // Blue color
        } else {
            binding.bookingStatusTextView.setTextColor(Color.parseColor("#000000")); // Black color for other statuses
        }
    }


    // Method to navigate to ProviderDashboard
    private void navigateToProviderDashboard() {
        Intent intent = new Intent(RequestDetailsActivity.this, ProviderDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }

    // Method to update button visibility based on status
    private void updateButtonVisibility(String status) {
        if ("Accepted".equals(status) || "Rejected".equals(status) || "Completed".equals(status)) {
            binding.acceptButton.setVisibility(android.view.View.GONE);
            binding.rejectButton.setVisibility(android.view.View.GONE);
            binding.completedButton.setVisibility(android.view.View.GONE);
        } else {
            binding.acceptButton.setVisibility(android.view.View.VISIBLE);
            binding.rejectButton.setVisibility(android.view.View.VISIBLE);
            binding.completedButton.setVisibility(android.view.View.VISIBLE);
        }
    }

}

