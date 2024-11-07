package com.sushmitamalakar.homeserviceapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sushmitamalakar.homeserviceapp.databinding.ActivityLogoutBinding;

public class LogoutActivity extends DrawerBaseActivity {

    ActivityLogoutBinding activityLogoutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLogoutBinding = ActivityLogoutBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Dashboard");
        setContentView(activityLogoutBinding.getRoot());
    }

}