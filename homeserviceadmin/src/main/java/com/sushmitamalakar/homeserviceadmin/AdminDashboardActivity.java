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
    private Button addServiceButton, showServiceButton,verifyDocumentButton, pendingBookingsButton, acceptedBookingsButton,rejectedBookingsButton, completedBookingsButton,manageUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminDashboardBinding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Dashboard");
        setContentView(activityAdminDashboardBinding.getRoot());

        addServiceButton = findViewById(R.id.addServiceButton);
        showServiceButton = findViewById(R.id.showServiceButton);
        verifyDocumentButton = findViewById(R.id.verifyDocumentButton);
        pendingBookingsButton = findViewById(R.id.pendingBookingsButton);
        acceptedBookingsButton = findViewById(R.id.acceptedBookingsButton);
        rejectedBookingsButton = findViewById(R.id.rejectedBookingsButton);
        completedBookingsButton = findViewById(R.id.completedBookingsButton);
        manageUsers = findViewById(R.id.manageUsers);

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

        acceptedBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ShowAcceptedBookingsActivity.class));
            }
        });

        pendingBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ShowPendingBookingsActivity.class));
            }
        });
        rejectedBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ShowRejectedBookingsActivity.class));
            }
        });

        completedBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ShowCompletedBookingsActivity.class));
            }
        });
        manageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ShowUsersActivity.class));
            }
        });
    }
}
