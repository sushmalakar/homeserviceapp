package com.sushmitamalakar.providerapp.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sushmitamalakar.providerapp.R;

public class ServiceViewHolder extends RecyclerView.ViewHolder {
    public ImageView serviceImage;
    public TextView serviceName;


    public ServiceViewHolder(View itemView) {
        super(itemView);
        serviceImage = itemView.findViewById(R.id.serviceImageView);
        serviceName = itemView.findViewById(R.id.serviceTextView);
    }
}
