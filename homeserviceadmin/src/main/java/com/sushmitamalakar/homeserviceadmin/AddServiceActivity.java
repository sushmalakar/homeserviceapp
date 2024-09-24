package com.sushmitamalakar.homeserviceadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import com.sushmitamalakar.homeserviceadmin.model.Service;

public class AddServiceActivity extends AppCompatActivity {
    private EditText serviceName;
//    private EditText servicePrice;
    private Button addServiceButton;
    private CircleImageView serviceImage;
    private Uri imageUri;
    private String imageURL;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);

        serviceImage = findViewById(R.id.serviceImageView);
//        servicePrice = findViewById(R.id.priceEditText);
        serviceName = findViewById(R.id.serviceEditText);
        addServiceButton = findViewById(R.id.addServiceButton);

        // Activity Result Launcher for picking images
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                imageUri = data.getData();
                                serviceImage.setImageURI(imageUri);
                            }
                        } else {
                            Toast.makeText(AddServiceActivity.this, "No Image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        serviceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImageAndData();
                } else {
                    Toast.makeText(AddServiceActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImageAndData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddServiceActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        storageReference = FirebaseStorage.getInstance().getReference()
                .child("service_pics")
                .child(imageUri.getLastPathSegment());

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageURL = uri.toString();
                                uploadData();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddServiceActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddServiceActivity.this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void uploadData() {
        String name = serviceName.getText().toString().trim();
//        String priceText = servicePrice.getText().toString().trim();

//        if (name.isEmpty() || priceText.isEmpty()) {
//            Toast.makeText(AddServiceActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (name.isEmpty()) {
            Toast.makeText(AddServiceActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

//        Double price;
//        try {
//            price = Double.parseDouble(priceText);
//        } catch (NumberFormatException e) {
//            Toast.makeText(AddServiceActivity.this, "Invalid price format", Toast.LENGTH_SHORT).show();
//            return;
//        }

//        Service service = new Service(imageURL, name, price);
//        Service service = new Service(imageURL, name);
          Service service = new Service(imageURL, name);

        FirebaseDatabase.getInstance().getReference("services").child(name)
                .setValue(service)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddServiceActivity.this, "Service Added Successfully", Toast.LENGTH_SHORT).show();
                            serviceName.setText("");
//                            servicePrice.setText("");
                            serviceImage.setImageURI(null);

                            Log.d("AddServiceActivity", "Redirecting to ShowServiceActivity");

                            Intent intent = new Intent(AddServiceActivity.this, ShowServiceActivity.class);
                            Log.d("AddServiceActivity1", "Redirecting to ShowServiceActivity");

                            startActivity(intent);

                        } else {
                            Toast.makeText(AddServiceActivity.this, "Failed to Add Service: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddServiceActivity.this, "Failed to Add Service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
