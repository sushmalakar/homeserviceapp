package com.sushmitamalakar.providerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    FirebaseAuth auth;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        auth = FirebaseAuth.getInstance();


        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int itemId = item.getItemId();

        if (itemId == R.id.myProfileItem) {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.logoutItem) {
            performLogout();
        } else if(itemId == R.id.requestsItem){
            startActivity(new Intent(this, RequestsActivity.class));
            overridePendingTransition(0, 0);
        }else if (itemId == R.id.myDocumentsItem) {
            startActivity(new Intent(this, ViewDocumentActivity.class));
            overridePendingTransition(0, 0);
        }else if (itemId == R.id.locationItem) {
            startActivity(new Intent(this, MapActivity.class));
            overridePendingTransition(0, 0);
        }else if(itemId == R.id.requestsItem){
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String providerId = currentUser.getUid();
                Log.d("ProviderDashboardActivity", "Opening RequestsActivity with providerId: " + providerId);

                Intent intent = new Intent(this, RequestsActivity.class);
                intent.putExtra("providerId", providerId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
            overridePendingTransition(0, 0);
        }else if(itemId == R.id.myservicesItem){
            overridePendingTransition(0, 0);
        }else if(itemId == R.id.dashboardItem){
            startActivity(new Intent(this, ProviderDashboardActivity.class));
            overridePendingTransition(0, 0);
        }
        else {
            return false;
        }

        return true;
    }

    private void performLogout() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    protected void allocateActivityTitle(String titleString) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleString);
        }
    }
}
