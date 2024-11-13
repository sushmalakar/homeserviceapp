package com.sushmitamalakar.homeserviceapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sushmitamalakar.homeserviceapp.R;
import com.sushmitamalakar.homeserviceapp.model.Booking;
import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private ArrayList<Booking> bookingList;

    public BookingAdapter(Context context, ArrayList<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mybookings, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.serviceNameTextView.setText("Service: " + booking.getServiceName());
        holder.providerNameTextView.setText("Provider: " + booking.getProviderName());
        holder.dateTextView.setText("Date: " + booking.getDate());
        holder.timeTextView.setText("Time: " + booking.getTime());
        holder.statusTextView.setText("Status: " + booking.getStatus());
        holder.chargeTextView.setText("Charge: Rs " + booking.getCharge());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView, providerNameTextView, dateTextView, timeTextView, statusTextView, chargeTextView;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            providerNameTextView = itemView.findViewById(R.id.providerNameTextView);
            dateTextView = itemView.findViewById(R.id.bookingDateTextView);
            timeTextView = itemView.findViewById(R.id.bookingTimeTextView);
            statusTextView = itemView.findViewById(R.id.bookingStatusTextView);
            chargeTextView = itemView.findViewById(R.id.bookingChargeTextView);
        }
    }
}
