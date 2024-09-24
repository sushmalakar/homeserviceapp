// Update the adapter to extend BaseAdapter for GridView
package com.sushmitamalakar.providerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.sushmitamalakar.providerapp.R;
import com.sushmitamalakar.providerapp.holder.ServiceViewHolder;
import com.sushmitamalakar.providerapp.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends BaseAdapter {
    private Context context;
    private List<Service> serviceList;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ServiceViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
            holder = new ServiceViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ServiceViewHolder) convertView.getTag();
        }

        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getServiceTitle());

        Glide.with(context)
                .load(service.getServiceImage())
                .placeholder(R.drawable.error_icon)
                .error(R.drawable.error_icon)
                .into(holder.serviceImage);

        return convertView;
    }

    public void searchServiceList(ArrayList<Service> filteredServices) {
        this.serviceList.clear();
        this.serviceList.addAll(filteredServices);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


}
