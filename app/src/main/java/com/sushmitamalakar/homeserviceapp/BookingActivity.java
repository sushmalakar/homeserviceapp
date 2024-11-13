package com.sushmitamalakar.homeserviceapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private EditText dateEditText, timeEditText, messageEditText;
    private Button submitButton;
    private CircleImageView providerImageView;
    private TextView titleNameTextView, emailTextView, mobileTextView, locationTextView;
    private DatabaseReference bookingsReference;
    private Calendar selectedDateTime;

    private String serviceId;
    private String userId;
    private String providerId;
    private String charge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize views
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        messageEditText = findViewById(R.id.messageEditText);
        submitButton = findViewById(R.id.submitButton);
        providerImageView = findViewById(R.id.userIconImageView);
        titleNameTextView = findViewById(R.id.titleNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        mobileTextView = findViewById(R.id.mobileTextView);
        locationTextView = findViewById(R.id.locationTextView);

        selectedDateTime = Calendar.getInstance();
        bookingsReference = FirebaseDatabase.getInstance().getReference("bookings");

        // Get data from Intent
        serviceId = getIntent().getStringExtra("serviceId");
        userId = getIntent().getStringExtra("userId");
        providerId = getIntent().getStringExtra("providerId");
        charge = getIntent().getStringExtra("charge");

        // Set up Date and Time Pickers
        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        submitButton.setOnClickListener(v -> submitBooking());

        // Fetch provider details to display
        if (providerId != null) {
            fetchProviderDetails(providerId);
        }
    }

    private void fetchProviderDetails(String providerId) {
        DatabaseReference providersReference = FirebaseDatabase.getInstance().getReference("providers").child(providerId);

        providersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve provider details
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String mobileNo = dataSnapshot.child("mobileNo").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    // Retrieve nested location fields
                    Double latitude = dataSnapshot.child("location").child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("location").child("longitude").getValue(Double.class);

                    // Set values in the UI
                    titleNameTextView.setText(fullName != null ? fullName : "N/A");
                    emailTextView.setText(email != null ? email : "N/A");
                    mobileTextView.setText(mobileNo != null ? mobileNo : "N/A");

                    // Convert latitude and longitude to a short address
                    if (latitude != null && longitude != null) {
                        convertLatLngToShortAddress(latitude, longitude);
                    } else {
                        locationTextView.setText("Location: Not available");
                    }

                    // Load image using Glide
                    Glide.with(BookingActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.user_icon) // Use a placeholder if imageUrl is empty
                            .into(providerImageView);
                } else {
                    Toast.makeText(BookingActivity.this, "Provider details not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookingActivity.this, "Failed to load provider details.", Toast.LENGTH_SHORT).show();
                Log.e("BookingActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void convertLatLngToShortAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Extract a short address: city, state, country
                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();

                // Combine them to form a short address format
                String shortAddress = (city != null ? city : "") +
                        (state != null ? ", " + state : "") +
                        (country != null ? ", " + country : "");

                locationTextView.setText("Location: " + shortAddress);
            } else {
                locationTextView.setText("Location: Address not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            locationTextView.setText("Location: Unable to fetch address");
        }
    }

    private void showDatePickerDialog() {
        Calendar today = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(year, month, dayOfMonth);
                    dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void submitBooking() {
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare booking data
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("userId", userId);
        bookingData.put("serviceId", serviceId);
        bookingData.put("providerId", providerId);
        bookingData.put("charge", charge);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("message", message);
        bookingData.put("status","Pending");

        // Add serviceId and userId to booking data


        // Save booking to Firebase
        bookingsReference.push().setValue(bookingData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BookingActivity.this, "Booking Successful", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(BookingActivity.this, "Booking Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
