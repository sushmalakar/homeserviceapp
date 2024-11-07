package com.sushmitamalakar.providerapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sushmitamalakar.providerapp.databinding.ActivityMapBinding;
import com.sushmitamalakar.providerapp.databinding.ActivityProfileBinding;

import java.util.Arrays;

public class MapActivity extends DrawerBaseActivity implements OnMapReadyCallback {
    ActivityMapBinding activityMapBinding;

    private static final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker selectedMarker;
    private boolean isFirstLocationUpdate = true;
    private String providerId;

    private static final String PREFS_NAME = "ProviderAppPrefs";
    private static final String KEY_PROVIDER_ID = "providerId";
    private DatabaseReference databaseReference;
    private ActivityResultLauncher<Intent> locationSettingsLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMapBinding = ActivityMapBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Location");
        setContentView(activityMapBinding.getRoot());

        locationSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (isLocationEnabled()) {
                            getLastLocation();
                        } else {
                            Toast.makeText(MapActivity.this, "Location services are still disabled. Please enable them to continue.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        providerId = sharedPreferences.getString(KEY_PROVIDER_ID, null);

        databaseReference = FirebaseDatabase.getInstance().getReference("providers");

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "YOUR_API_KEY_HERE");
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        mMap.clear();
                        selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        showConfirmationDialog(latLng);
                    }
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Toast.makeText(MapActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showConfirmationDialog(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("Confirm Location");
        builder.setMessage("Do you want to set this location: " + latLng.latitude + ", " + latLng.longitude + "?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateProviderLocation(providerId, latLng.latitude, latLng.longitude);
                Toast.makeText(MapActivity.this, "Location Confirmed: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapActivity.this, ProviderDashboardActivity.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        if (!isLocationEnabled()) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            locationSettingsLauncher.launch(intent);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                if (isFirstLocationUpdate) {
                    updateMapWithCurrentLocation();
                    isFirstLocationUpdate = false;
                }
            }
        });
    }

    private void updateMapWithCurrentLocation() {
        if (mMap != null && currentLocation != null) {
            LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.clear();
            selectedMarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadSavedLocation();  // Load the saved location from Firebase on map ready

        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove(); // Remove the old marker if present
            }
            selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            showConfirmationDialog(latLng);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied. Please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            loadSavedLocation();  // Reload saved location data from Firebase each time activity resumes
        }
    }

    private void updateProviderLocation(String providerId, double latitude, double longitude) {
        if (providerId != null) {
            databaseReference.child(providerId).child("location").setValue(new LocationData(latitude, longitude))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MapActivity.this, "Location updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MapActivity.this, "Failed to update location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Provider ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSavedLocation() {
        if (providerId != null) {
            databaseReference.child(providerId).child("location").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    LocationData locationData = task.getResult().getValue(LocationData.class);
                    if (locationData != null) {
                        LatLng savedLocation = new LatLng(locationData.latitude, locationData.longitude);
                        updateMapWithSavedLocation(savedLocation);
                    }
                } else {
                    Toast.makeText(MapActivity.this, "No saved location found for this provider", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateMapWithSavedLocation(LatLng savedLocation) {
        if (mMap != null) {
            mMap.clear();
            selectedMarker = mMap.addMarker(new MarkerOptions().position(savedLocation).title("Saved Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLocation, 15));
            Toast.makeText(MapActivity.this, "Loaded saved location", Toast.LENGTH_SHORT).show();
        }
    }

    public static class LocationData {
        public double latitude;
        public double longitude;

        public LocationData() {
        }

        public LocationData(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
