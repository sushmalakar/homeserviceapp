package com.sushmitamalakar.homeserviceadmin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceadmin.adapter.BookingAdapter;
import com.sushmitamalakar.homeserviceadmin.databinding.ActivityShowPendingBookingsBinding;
import com.sushmitamalakar.homeserviceadmin.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class ShowPendingBookingsActivity extends DrawerBaseActivity {

    private ActivityShowPendingBookingsBinding binding;
    private RecyclerView pendingBookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowPendingBookingsBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Pending Bookings");
        setContentView(binding.getRoot());

        // Initialize RecyclerView and Adapter
        pendingBookingsRecyclerView = findViewById(R.id.pendingBookingsRecyclerView);
        pendingBookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        pendingBookingsRecyclerView.setAdapter(bookingAdapter);

        // Firebase reference for bookings
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        // Fetch only pending bookings
        fetchPendingBookings();
    }

    // Method to fetch pending bookings from Firebase
    private void fetchPendingBookings() {
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    String status = bookingSnapshot.child("status").getValue(String.class);
                    if ("Pending".equalsIgnoreCase(status)) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        if (booking != null) {
                            // Set the bookingId manually
                            booking.setBookingId(bookingSnapshot.getKey());
                            bookingList.add(booking);
                        }
                    }
                }
                bookingAdapter.notifyDataSetChanged();

                if (bookingList.isEmpty()) {
                    Toast.makeText(ShowPendingBookingsActivity.this, "No pending bookings found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShowPendingBookings", "Database error: " + error.getMessage());
                Toast.makeText(ShowPendingBookingsActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
