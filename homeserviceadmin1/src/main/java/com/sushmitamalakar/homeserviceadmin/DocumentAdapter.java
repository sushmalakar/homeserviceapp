//package edu.divyagyan.homeserviceadmin;
//
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.sushmitamalakar.providerapp.model.Document; // Adjust import path
//import java.util.List;
//
//public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
//
//    private List<Document> documents;
//    private OnDocumentClickListener listener;
//
//    public interface OnDocumentClickListener {
//        void onDocumentClick(Document document);
//    }
//
//    public DocumentAdapter(List<Document> documents, OnDocumentClickListener listener) {
//        this.documents = documents;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pending_doc, parent, false);
//        return new DocumentViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
//        Document document = documents.get(position);
//        holder.bind(document, listener);
//    }
//
//    @Override
//    public int getItemCount() {
//        return documents.size();
//    }
//
//    static class DocumentViewHolder extends RecyclerView.ViewHolder {
//        TextView nameTextView;
//        TextView statusTextView;
//
//        DocumentViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.providerRecyclerName);
//            statusTextView = itemView.findViewById(R.id.providerRecyclerStatus);
//        }
//
//        void bind(final Document document, final OnDocumentClickListener listener) {
//            nameTextView.setText(document.getName());
//            statusTextView.setText("Status: " + document.getStatus()); // Assuming Document has a getStatus() method
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onDocumentClick(document);
//                }
//            });
//        }
//    }
//}
//
