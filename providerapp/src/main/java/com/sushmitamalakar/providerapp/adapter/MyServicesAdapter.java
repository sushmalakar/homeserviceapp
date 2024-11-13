package com.sushmitamalakar.providerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sushmitamalakar.providerapp.R;
import com.sushmitamalakar.providerapp.model.ServiceRequest;

import java.util.ArrayList;

public class MyServicesAdapter extends RecyclerView.Adapter<MyServicesAdapter.ServiceViewHolder> {

    private Context context;
    private ArrayList<ServiceRequest> serviceRequestList;
    private OnServiceClickListener editListener;
    private OnServiceClickListener deleteListener;

    public MyServicesAdapter(Context context, ArrayList<ServiceRequest> serviceRequestList, OnServiceClickListener editListener, OnServiceClickListener deleteListener) {
        this.context = context;
        this.serviceRequestList = serviceRequestList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_services, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceRequest serviceRequest = serviceRequestList.get(position);

        holder.serviceTitleTextView.setText(serviceRequest.getServiceTitle());
        holder.serviceChargeTextView.setText("Charge: Rs " + serviceRequest.getCharge());

        Glide.with(context)
                .load(serviceRequest.getServiceImage())
                .into(holder.serviceImageView);

        holder.itemView.setOnClickListener(v -> editListener.onServiceClick(serviceRequest));
        holder.deleteIcon.setOnClickListener(v -> deleteListener.onServiceClick(serviceRequest));
    }

    @Override
    public int getItemCount() {
        return serviceRequestList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTitleTextView, serviceChargeTextView;
        ImageView serviceImageView, deleteIcon;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitleTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceChargeTextView = itemView.findViewById(R.id.serviceChargeTextView);
            serviceImageView = itemView.findViewById(R.id.serviceImageView);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }

    public interface OnServiceClickListener {
        void onServiceClick(ServiceRequest serviceRequest);
    }
}
