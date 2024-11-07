package com.sushmitamalakar.homeserviceadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sushmitamalakar.homeserviceadmin.databinding.ActivityAdminDashboardBinding;

public class AdminDashboardActivity extends DrawerBaseActivity {
    ActivityAdminDashboardBinding activityAdminDashboardBinding;
    private Button addServiceButton, showServiceButton,verifyDocumentButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminDashboardBinding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Dashboard");
        setContentView(activityAdminDashboardBinding.getRoot());

        addServiceButton = findViewById(R.id.addServiceButton);
        showServiceButton = findViewById(R.id.showServiceButton);
        verifyDocumentButton = findViewById(R.id.verifyDocumentButton);


        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AddServiceActivity.class));
            }
        });

        showServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ShowServiceActivity.class));

            }
        });
        verifyDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ShowPendingDocumentsActivity.class));

            }
        });
    }
}
