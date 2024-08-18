package edu.divyagyan.homeserviceadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.divyagyan.homeserviceadmin.R;
import edu.divyagyan.homeserviceadmin.ShowProviderActivity;
import edu.divyagyan.homeserviceadmin.holder.ServiceViewHolder;
import edu.divyagyan.homeserviceadmin.model.Service;

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

        // Load image with placeholders and error images
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.icon_error) // Add a placeholder drawable
                .error(R.drawable.icon_error) // Add an error drawable
                .into(holder.serviceImage);

        holder.serviceName.setText(serviceList.get(position).getServiceTitle());

        holder.serviceRecyclerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProviderActivity.class);
                intent.putExtra("Image", imageUrl);
                intent.putExtra("ServiceName", serviceList.get(holder.getAdapterPosition()).getServiceTitle());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }
}

