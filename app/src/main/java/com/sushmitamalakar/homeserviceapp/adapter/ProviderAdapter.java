package com.sushmitamalakar.homeserviceapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.sushmitamalakar.homeserviceapp.BookingActivity;
import com.sushmitamalakar.homeserviceapp.R;
import com.sushmitamalakar.homeserviceapp.model.Provider;
import java.util.ArrayList;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    private Context context;
    private ArrayList<Provider> providerList;
    private String serviceId;  // Add serviceId
    private String userId;     // Add userId

    public ProviderAdapter(Context context, ArrayList<Provider> providerList, String serviceId, String userId) {
        this.context = context;
        this.providerList = providerList;
        this.serviceId = serviceId;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_provider, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Provider provider = providerList.get(position);
        holder.providerName.setText(provider.getFullName());
        holder.providerCharge.setText("Charge: Rs " + provider.getCharge());


        // Display location as "latitude, longitude"
        Provider.Location location = provider.getLocation();
        holder.providerLocation.setText("Location: " + (location != null ? location.toString() : "Unknown"));

        // Load provider image using Glide
        Glide.with(context)
                .load(provider.getImageUrl())
                .placeholder(R.drawable.user_icon)
                .into(holder.providerImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("serviceId", serviceId);                // Pass serviceId
            intent.putExtra("userId", userId);                      // Pass userId
            intent.putExtra("providerId", provider.getProviderId()); // Pass providerId
            intent.putExtra("charge", provider.getCharge());        // Pass charge
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class ProviderViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView providerImage;
        TextView providerName, providerCharge, providerLocation;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            providerImage = itemView.findViewById(R.id.providerImage);
            providerName = itemView.findViewById(R.id.providerName);
            providerCharge = itemView.findViewById(R.id.providerCharge);
            providerLocation = itemView.findViewById(R.id.providerLocation);
        }
    }
    public void updateProviderLocation(String providerId, String shortAddress) {
        for (int i = 0; i < providerList.size(); i++) {
            if (providerList.get(i).getProviderId().equals(providerId)) {
                providerList.get(i).getLocation().setShortAddress(shortAddress);
                notifyItemChanged(i);
                break;
            }
        }
    }

}
