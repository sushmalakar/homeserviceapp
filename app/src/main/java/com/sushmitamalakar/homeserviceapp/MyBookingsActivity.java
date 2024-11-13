package com.sushmitamalakar.homeserviceapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceapp.adapter.BookingAdapter;
import com.sushmitamalakar.homeserviceapp.databinding.ActivityMapBinding;
import com.sushmitamalakar.homeserviceapp.databinding.ActivityMyBookingsBinding;
import com.sushmitamalakar.homeserviceapp.model.Booking;
import java.util.ArrayList;

public class MyBookingsActivity extends DrawerBaseActivity {
    ActivityMyBookingsBinding activityMyBookingsBinding;

    private RecyclerView bookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private ArrayList<Booking> bookingList;
    private DatabaseReference bookingsReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMyBookingsBinding = ActivityMyBookingsBinding.inflate(getLayoutInflater());
        setContentView(activityMyBookingsBinding.getRoot());
        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        bookingsRecyclerView.setAdapter(bookingAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            fetchBookings();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchBookings() {
        bookingsReference = FirebaseDatabase.getInstance().getReference("bookings");

        bookingsReference.orderByChild("userId").equalTo(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookingList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Booking booking = snapshot.getValue(Booking.class);
                                if (booking != null) {
                                    fetchServiceAndProviderNames(booking);
                                }
                            }
                        } else {
                            Toast.makeText(MyBookingsActivity.this, "No bookings found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MyBookingsActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchServiceAndProviderNames(Booking booking) {
        // Fetch the service name
        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("services").child(booking.getServiceId());
        serviceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String serviceName = dataSnapshot.child("serviceTitle").getValue(String.class);
                booking.setServiceName(serviceName != null ? serviceName : "Unknown Service");

                // Fetch the provider name after service name
                DatabaseReference providerReference = FirebaseDatabase.getInstance().getReference("providers").child(booking.getProviderId());
                providerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot providerSnapshot) {
                        String providerName = providerSnapshot.child("fullName").getValue(String.class);
                        booking.setProviderName(providerName != null ? providerName : "Unknown Provider");

                        // Add the booking to the list and notify the adapter
                        bookingList.add(booking);
                        bookingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyBookingsActivity.this, "Failed to load provider name", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyBookingsActivity.this, "Failed to load service name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
