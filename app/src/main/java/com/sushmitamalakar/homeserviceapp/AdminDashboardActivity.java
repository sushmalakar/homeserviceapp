package com.sushmitamalakar.homeserviceapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sushmitamalakar.homeserviceapp.databinding.ActivityAdminDashboardBinding;

public class AdminDashboardActivity extends DrawerBaseActivity {

    ActivityAdminDashboardBinding activityAdminDashboardBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminDashboardBinding =ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Dashboard");
        setContentView(activityAdminDashboardBinding.getRoot());
    }
}
