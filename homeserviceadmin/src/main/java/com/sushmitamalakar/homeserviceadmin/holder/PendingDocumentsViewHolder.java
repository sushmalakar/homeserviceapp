package com.sushmitamalakar.homeserviceadmin.holder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sushmitamalakar.homeserviceadmin.R;

public class PendingDocumentsViewHolder extends RecyclerView.ViewHolder {
    public TextView providerNameTextView, statusTextView;

    public PendingDocumentsViewHolder(@NonNull View itemView) {
        super(itemView);
        providerNameTextView = itemView.findViewById(R.id.pendingNameRecyclerTextView);
        statusTextView = itemView.findViewById(R.id.statusRecyclerTextView);
    }
}
