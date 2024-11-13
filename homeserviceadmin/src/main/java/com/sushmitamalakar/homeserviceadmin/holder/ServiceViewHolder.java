package com.sushmitamalakar.homeserviceadmin.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import com.sushmitamalakar.homeserviceadmin.R;
import de.hdodenhof.circleimageview.CircleImageView;
import com.sushmitamalakar.homeserviceadmin.R;

public class ServiceViewHolder extends RecyclerView.ViewHolder {
     public ShapeableImageView serviceImage;
    public TextView serviceName;
    public CardView serviceRecyclerCard;
    public ImageView editIcon, deleteIcon;
    public ServiceViewHolder(@NonNull View itemView) {
        super(itemView);

        serviceImage = itemView.findViewById(R.id.serviceRecyclerImage);
        serviceName = itemView.findViewById(R.id.serviceRecyclerTitle);
        serviceRecyclerCard = itemView.findViewById(R.id.servicesRecyclerCard);
        editIcon = itemView.findViewById(R.id.editIcon);
        deleteIcon = itemView.findViewById(R.id.deleteIcon);
    }
}
