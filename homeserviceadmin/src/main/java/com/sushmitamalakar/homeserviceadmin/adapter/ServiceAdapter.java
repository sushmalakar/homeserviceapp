package com.sushmitamalakar.homeserviceadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sushmitamalakar.homeserviceadmin.EditServiceActivity;
import com.sushmitamalakar.homeserviceadmin.R;
import com.sushmitamalakar.homeserviceadmin.ShowProviderActivity;
import com.sushmitamalakar.homeserviceadmin.holder.ServiceViewHolder;
import com.sushmitamalakar.homeserviceadmin.model.Service;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceViewHolder> {
    private Context context;
    private List<Service> serviceList;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_items_services, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        String imageUrl = serviceList.get(position).getServiceImage();
        String serviceId = serviceList.get(position).getServiceId();


        // Load image with placeholders and error images
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.icon_error) // Add a placeholder drawable
                .error(R.drawable.icon_error) // Add an error drawable
                .into(holder.serviceImage);

        holder.serviceName.setText(serviceList.get(position).getServiceTitle());

        // Handle the service card click to show providers
        holder.serviceRecyclerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProviderActivity.class);
                intent.putExtra("Image", imageUrl);
                intent.putExtra("ServiceName", serviceList.get(holder.getAdapterPosition()).getServiceTitle());
                context.startActivity(intent);
            }
        });

        // Handle the edit icon click
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an edit service activity or show a dialog
                Intent editIntent = new Intent(context, EditServiceActivity.class);
                editIntent.putExtra("ServiceId", serviceList.get(holder.getAdapterPosition()).getServiceId());
                editIntent.putExtra("ServiceName", serviceList.get(holder.getAdapterPosition()).getServiceTitle());
                editIntent.putExtra("ServiceImage", serviceList.get(holder.getAdapterPosition()).getServiceImage());

                context.startActivity(editIntent);
            }
        });

        // Handle the delete icon click
        holder.deleteIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete this service?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteService(serviceId, holder.getAdapterPosition()))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    // Method to delete a service from Firebase
    private void deleteService(String serviceId, int position) {
        if (serviceId == null || serviceId.isEmpty()) {
            Toast.makeText(context, "Error: Service ID is missing or null", Toast.LENGTH_SHORT).show();
            return;
        }
        // Log the serviceId for debugging
        Log.d("ServiceAdapter", "Deleting service with ID: " + serviceId);

        // Reference to the Firebase database (update the path as per your Firebase structure)
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services").child(serviceId);

        // Delete the service
        servicesRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remove the item from the list and notify the adapter
                    serviceList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, serviceList.size());
                    Toast.makeText(context, "Service deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ServiceAdapter", "Error deleting service: " + e.getMessage());
                });
    }


    @Override
    public int getItemCount() {
        return serviceList.size();
    }
}

