package com.sushmitamalakar.homeserviceadmin.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

<<<<<<< HEAD:homeserviceadmin/src/main/java/com/sushmitamalakar/homeserviceadmin/holder/ServiceViewHolder.java
import com.sushmitamalakar.homeserviceadmin.R;
=======
import de.hdodenhof.circleimageview.CircleImageView;
import edu.divyagyan.homeserviceadmin.R;
>>>>>>> parent of 5087604 (6th commit user dashboard design with data created):homeserviceadmin/src/main/java/edu/divyagyan/homeserviceadmin/holder/ServiceViewHolder.java

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
