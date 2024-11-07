package com.sushmitamalakar.homeserviceadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sushmitamalakar.homeserviceadmin.R;
import com.sushmitamalakar.homeserviceadmin.VerifyPendingDocumentActivity;
import com.sushmitamalakar.homeserviceadmin.holder.PendingDocumentsViewHolder;
import com.sushmitamalakar.homeserviceadmin.model.Document;

import java.util.List;

public class PendingDocumentsAdapter extends RecyclerView.Adapter<PendingDocumentsViewHolder> {
    private Context context;
    private List<Document> documentList;

    public PendingDocumentsAdapter(Context context, List<Document> documentList) {
        this.context = context;
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public PendingDocumentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recylcer_items_pending_documents, parent, false);
        return new PendingDocumentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingDocumentsViewHolder holder, int position) {
        Document document = documentList.get(position);

        holder.providerNameTextView.setText(document.getProviderName() != null ? document.getProviderName() : "Unknown Provider");
        holder.statusTextView.setText(document.getStatus());

        holder.statusTextView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VerifyPendingDocumentActivity.class);
            intent.putExtra("providerId", document.getProviderId());
            intent.putExtra("providerName", document.getProviderName());
            intent.putExtra("documentId", document.getDocumentId());
            intent.putExtra("status", document.getStatus());
            intent.putExtra("frontImageUrl", document.getFrontImageUrl());
            intent.putExtra("backImageUrl", document.getBackImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }
}
