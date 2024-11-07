package com.sushmitamalakar.homeserviceapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private EditText dateEditText, timeEditText, messageEditText;
    private Button submitButton;
    private DatabaseReference bookingsReference;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        messageEditText = findViewById(R.id.messageEditText);
        submitButton = findViewById(R.id.submitButton);

        selectedDateTime = Calendar.getInstance(); // initialize with current date and time
        bookingsReference = FirebaseDatabase.getInstance().getReference("bookings");

        // Set up Date Picker
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Set up Time Picker
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        submitButton.setOnClickListener(v -> submitBooking());
    }

    private void showDatePickerDialog() {
        Calendar today = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(year, month, dayOfMonth); // set date in calendar
                    dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis()); // Restrict to today or later
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void submitBooking() {
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get provider details from intent
        String providerId = getIntent().getStringExtra("providerId");
        String charge = getIntent().getStringExtra("charge");

        // Prepare booking data
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("providerId", providerId);
        bookingData.put("charge", charge);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("message", message);

        // Save to Firebase
        bookingsReference.push().setValue(bookingData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BookingActivity.this, "Booking Successful", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(BookingActivity.this, "Booking Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
