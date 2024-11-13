package com.sushmitamalakar.homeserviceadmin;

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
import com.sushmitamalakar.homeserviceadmin.adapter.BookingAdapter;
import com.sushmitamalakar.homeserviceadmin.databinding.ActivityShowRejectedBookingsBinding;
import com.sushmitamalakar.homeserviceadmin.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class ShowRejectedBookingsActivity extends DrawerBaseActivity {

    private ActivityShowRejectedBookingsBinding binding;
    private RecyclerView rejectedBookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowRejectedBookingsBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Rejected Bookings");
        setContentView(binding.getRoot());

        // Initialize RecyclerView and Adapter
        rejectedBookingsRecyclerView = findViewById(R.id.rejectedBookingsRecyclerView);
        rejectedBookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        rejectedBookingsRecyclerView.setAdapter(bookingAdapter);

        // Firebase reference for bookings
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        // Fetch only rejected bookings
        fetchRejectedBookings();
    }

    private void fetchRejectedBookings() {
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    String status = bookingSnapshot.child("status").getValue(String.class);
                    if ("Rejected".equalsIgnoreCase(status)) {
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
                    Toast.makeText(ShowRejectedBookingsActivity.this, "No rejected bookings found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShowRejectedBookings", "Database error: " + error.getMessage());
                Toast.makeText(ShowRejectedBookingsActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
