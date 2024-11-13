package com.sushmitamalakar.providerapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushmitamalakar.providerapp.adapter.MyServicesAdapter;
import com.sushmitamalakar.providerapp.model.ServiceRequest;
import com.sushmitamalakar.providerapp.databinding.ActivityServicesBinding;

import java.util.ArrayList;

public class ServicesActivity extends DrawerBaseActivity {

    ActivityServicesBinding binding;
    private RecyclerView servicesRecyclerView;
    private MyServicesAdapter myServicesAdapter;
    private ArrayList<ServiceRequest> serviceRequestList;
    private DatabaseReference servicesDatabaseReference, serviceChargeDatabaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        servicesRecyclerView = binding.servicesRecyclerView;
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        servicesDatabaseReference = FirebaseDatabase.getInstance().getReference("services");
        serviceChargeDatabaseReference = FirebaseDatabase.getInstance().getReference("service_charge");

        serviceRequestList = new ArrayList<>();
        myServicesAdapter = new MyServicesAdapter(this, serviceRequestList, this::showEditChargeDialog, this::deleteServiceCharge);
        servicesRecyclerView.setAdapter(myServicesAdapter);

        fetchServiceRequests();
    }

    private void fetchServiceRequests() {
        String providerId = auth.getCurrentUser().getUid();

        serviceChargeDatabaseReference.orderByChild("providerId").equalTo(providerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        serviceRequestList.clear();

                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            ServiceRequest serviceRequest = requestSnapshot.getValue(ServiceRequest.class);
                            if (serviceRequest != null) {
                                serviceRequest.setBookingId(requestSnapshot.getKey());
                                fetchServiceDetails(serviceRequest);
                            }
                        }

                        myServicesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ServicesActivity.this, "Failed to load service requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchServiceDetails(ServiceRequest serviceRequest) {
        servicesDatabaseReference.child(serviceRequest.getServiceId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            serviceRequest.setServiceTitle(snapshot.child("serviceTitle").getValue(String.class));
                            serviceRequest.setServiceImage(snapshot.child("serviceImage").getValue(String.class));
                            serviceRequestList.add(serviceRequest);
                            myServicesAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ServicesActivity.this, "Failed to load service details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditChargeDialog(ServiceRequest serviceRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Service Charge");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_service_charge, null);
        EditText chargeEditText = dialogView.findViewById(R.id.chargeEditText);
        chargeEditText.setText(serviceRequest.getCharge());

        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedCharge = chargeEditText.getText().toString().trim();
            if (!updatedCharge.isEmpty()) {
                updateServiceCharge(serviceRequest, updatedCharge);
            } else {
                Toast.makeText(this, "Charge cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateServiceCharge(ServiceRequest serviceRequest, String updatedCharge) {
        DatabaseReference requestRef = serviceChargeDatabaseReference.child(serviceRequest.getBookingId());
        requestRef.child("charge").setValue(updatedCharge)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        serviceRequest.setCharge(updatedCharge);
                        myServicesAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Service charge updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update service charge", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteServiceCharge(ServiceRequest serviceRequest) {
        // Show a confirmation dialog before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Service Charge");
        builder.setMessage("Are you sure you want to delete this service charge?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Proceed with deletion if the user confirms
            serviceChargeDatabaseReference.child(serviceRequest.getBookingId()).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Service charge deleted successfully", Toast.LENGTH_SHORT).show();
                            serviceRequestList.remove(serviceRequest);
                            myServicesAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Failed to delete service charge", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
