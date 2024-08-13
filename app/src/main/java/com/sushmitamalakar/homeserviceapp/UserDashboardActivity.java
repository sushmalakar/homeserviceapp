package com.sushmitamalakar.homeserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class UserDashboardActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        drawerLayout = findViewById(R.id.userDashboardDrawerLayout);
        navigationView = findViewById(R.id.userDashboardNavigationView);

        test = findViewById(R.id.testbtn);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here
                int id = item.getItemId();

                if (id == R.id.myProfileItem) {
                    openProfileActivity();
                    return true;
                } else if (id == R.id.logoutItem) {
                    handleLogout();
                    return true;
                }

                // Close the drawer and return false for any unhandled menu items
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void openProfileActivity() {
        Log.d("UserDashboardActivity", "Attempting to start ProfileActivity");
        Toast.makeText(this, "Opening Profile", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UserDashboardActivity.this, ProfileActivity.class));
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void handleLogout() {
        // Implement logout functionality, such as clearing user sessions
        startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
