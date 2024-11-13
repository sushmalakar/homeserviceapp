
package com.sushmitamalakar.providerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.sushmitamalakar.providerapp.R;
import com.sushmitamalakar.providerapp.RequestDetailsActivity;
import com.sushmitamalakar.providerapp.model.ServiceRequest;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private final Context context;
    private final List<ServiceRequest> requestList;

    public RequestsAdapter(Context context, List<ServiceRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_requests, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceRequest request = requestList.get(position);

        holder.serviceNameTextView.setText(request.getServiceTitle());
        holder.userNameTextView.setText("User: " + request.getUserName());
        holder.bookingDateTextView.setText("Date: " + request.getDate());
        holder.bookingTimeTextView.setText("Time: " + request.getTime());
        holder.bookingChargeTextView.setText("Charge: Rs " + request.getCharge());
        holder.bookingStatusTextView.setText(request.getStatus());

        Glide.with(context).load(request.getServiceImage()).into(holder.serviceImageView);


        //changing the status color
        String status = request.getStatus();
        if ("Accepted".equalsIgnoreCase(status)) {
            holder.bookingStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if ("Rejected".equalsIgnoreCase(status)) {
            holder.bookingStatusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.bookingStatusTextView.setTextColor(context.getResources().getColor(android.R.color.black));
        }
        // Click listener to open RequestDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RequestDetailsActivity.class);
            intent.putExtra("bookingId", request.getBookingId()); // Pass the booking ID correctly
            intent.putExtra("serviceId", request.getServiceId());
            intent.putExtra("providerId", request.getProviderId());
            intent.putExtra("userId", request.getUserId());
            intent.putExtra("serviceTitle", request.getServiceTitle());
            intent.putExtra("serviceImage", request.getServiceImage());
            intent.putExtra("userName", request.getUserName());
            intent.putExtra("date", request.getDate());
            intent.putExtra("time", request.getTime());
            intent.putExtra("charge", request.getCharge());
            intent.putExtra("status", request.getStatus());
            intent.putExtra("message", request.getMessage());

            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImageView;
        TextView serviceNameTextView, userNameTextView, bookingDateTextView, bookingTimeTextView, bookingChargeTextView, bookingStatusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImageView = itemView.findViewById(R.id.serviceImageView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            bookingDateTextView = itemView.findViewById(R.id.bookingDateTextView);
            bookingTimeTextView = itemView.findViewById(R.id.bookingTimeTextView);
            bookingChargeTextView = itemView.findViewById(R.id.bookingChargeTextView);
            bookingStatusTextView = itemView.findViewById(R.id.bookingStatusTextView);


        }
    }
}



