package com.sushmitamalakar.homeserviceadmin;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
<<<<<<< HEAD:homeserviceadmin/src/main/java/com/sushmitamalakar/homeserviceadmin/ShowServiceActivity.java

=======
>>>>>>> parent of 5087604 (6th commit user dashboard design with data created):homeserviceadmin/src/main/java/edu/divyagyan/homeserviceadmin/ShowServiceActivity.java
import java.util.ArrayList;
import java.util.List;
import com.sushmitamalakar.homeserviceadmin.adapter.ServiceAdapter;
import com.sushmitamalakar.homeserviceadmin.model.Service;

public class ShowServiceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Service> serviceList;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_services);

        recyclerView = findViewById(R.id.servicesRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShowServiceActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowServiceActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        serviceList = new ArrayList<>();
        final ServiceAdapter serviceAdapter = new ServiceAdapter(ShowServiceActivity.this, serviceList);
        recyclerView.setAdapter(serviceAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("services");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ShowServiceActivity", "Data received from Firebase");

                serviceList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Service service = itemSnapshot.getValue(Service.class);
                    if (service != null) {
                        serviceList.add(service);
                    } else {
                        Log.d("ShowServiceActivity", "Service object is null");
                    }
                }
                serviceAdapter.notifyDataSetChanged();
                dialog.dismiss();
                Log.d("ShowServiceActivity", "Data updated, size: " + serviceList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Log.e("ShowServiceActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
