package com.sushmitamalakar.homeserviceadmin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import com.sushmitamalakar.homeserviceadmin.adapter.UsersAdapter;
import com.sushmitamalakar.homeserviceadmin.databinding.ActivityShowAcceptedBookingsBinding;
import com.sushmitamalakar.homeserviceadmin.databinding.ActivityShowUsersBinding;
import com.sushmitamalakar.homeserviceadmin.model.User;
import java.util.ArrayList;

public class ShowUsersActivity extends DrawerBaseActivity {
    ActivityShowUsersBinding activityShowUsersBinding;

    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;
    private ArrayList<User> userList;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityShowUsersBinding = ActivityShowUsersBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Show Users");
        setContentView(activityShowUsersBinding.getRoot());
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, userList, this::showEditUserDialog, this::confirmDeleteUser);
        usersRecyclerView.setAdapter(usersAdapter);

        fetchUsers();
    }

    private void fetchUsers() {
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if (user != null) {
                        // Set the userId from the key of the snapshot
                        user.setUserId(userSnapshot.getKey());
                        userList.add(user);
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText editFullName = dialogView.findViewById(R.id.editFullName);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);
        EditText editMobile = dialogView.findViewById(R.id.editMobile);

        // Set the existing values
        editFullName.setText(user.getFullName());
        editEmail.setText(user.getEmail());
        editMobile.setText(user.getMobileNo()); // Use getMobileNo() instead of getMobile()

        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String fullName = editFullName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String mobileNo = editMobile.getText().toString().trim();

            updateUser(user, fullName, email, mobileNo);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateUser(User user, String fullName, String email, String mobileNo) {
        if (user.getUserId() == null) {
            Toast.makeText(this, "User ID is null. Cannot update user.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = usersDatabaseReference.child(user.getUserId());
        userRef.child("fullName").setValue(fullName);
        userRef.child("email").setValue(email);
        userRef.child("mobileNo").setValue(mobileNo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                        fetchUsers(); // Refresh the list
                    } else {
                        Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void confirmDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUser(user))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteUser(User user) {
        usersDatabaseReference.child(user.getUserId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
