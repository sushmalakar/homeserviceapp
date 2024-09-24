package edu.divyagyan.shared.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import edu.divyagyan.homeserviceadmin.R;

public class ServiceViewHolder extends RecyclerView.ViewHolder {
     public ShapeableImageView serviceImage;
    public TextView serviceName;
    public CardView serviceRecyclerCard;
    public ServiceViewHolder(@NonNull View itemView) {
        super(itemView);

        serviceImage = itemView.findViewById(R.id.serviceRecyclerImage);
        serviceName = itemView.findViewById(R.id.serviceRecyclerTitle);
        serviceRecyclerCard = itemView.findViewById(R.id.servicesRecyclerCard);
    }
}
