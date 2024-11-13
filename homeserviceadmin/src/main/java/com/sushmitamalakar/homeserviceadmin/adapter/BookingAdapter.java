package com.sushmitamalakar.homeserviceadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.homeserviceadmin.R;
import com.sushmitamalakar.homeserviceadmin.UpdateBookingStatusActivity;
import com.sushmitamalakar.homeserviceadmin.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final Context context;
    private final List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Set the status text
        holder.statusRecyclerTextView.setText(booking.getStatus());

        // Fetch user full name from the "users" node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(booking.getUserId());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.child("fullName").getValue(String.class);
                holder.userNameTextView.setText(fullName != null ? fullName : "Unknown User");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BookingAdapter", "Failed to fetch user full name: " + error.getMessage());
                holder.userNameTextView.setText("Unknown User");
            }
        });

        // Fetch service name from the "services" node
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services").child(booking.getServiceId());
        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String serviceName = snapshot.child("serviceTitle").getValue(String.class);
                holder.serviceNameTextView.setText(serviceName != null ? serviceName : "Unknown Service");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BookingAdapter", "Error fetching service: " + error.getMessage());
                holder.serviceNameTextView.setText("Unknown Service");
            }
        });

        // Click listener to open UpdateBookingStatusActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateBookingStatusActivity.class);
            intent.putExtra("bookingId", booking.getBookingId());
            intent.putExtra("serviceId", booking.getServiceId());
            intent.putExtra("userId", booking.getUserId());
            intent.putExtra("providerId", booking.getProviderId());
            intent.putExtra("serviceTitle", booking.getServiceTitle());
            intent.putExtra("status", booking.getStatus());
            intent.putExtra("date", booking.getDate());
            intent.putExtra("time", booking.getTime());
            intent.putExtra("charge", booking.getCharge());
            intent.putExtra("message", booking.getMessage());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView, serviceNameTextView, statusRecyclerTextView;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            statusRecyclerTextView = itemView.findViewById(R.id.statusRecyclerTextView);
        }
    }
}
