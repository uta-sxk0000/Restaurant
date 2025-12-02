package com.example.queue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReservationActivity extends AppCompatActivity {

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Views
    private EditText editTextName;
    private EditText editTextGuests;
    private EditText editTextDate;
    private EditText editTextTime;
    private Button buttonReserve;

    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        restaurantId = getIntent().getStringExtra("restaurantId");
        if (restaurantId == null || restaurantId.isEmpty()) {
            Toast.makeText(this, "Error: Restaurant ID is missing.", Toast.LENGTH_LONG).show();
            Log.e("ReservationActivity", "Restaurant ID was not passed in the intent.");
            finish();
            return;
        }

        // Initialize Views
        editTextName = findViewById(R.id.editTextName);
        editTextGuests = findViewById(R.id.editTextGuests);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        buttonReserve = findViewById(R.id.buttonReserve);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Make a Reservation");
        }

        buttonReserve.setOnClickListener(v -> createReservationRequest());
    }

    private void createReservationRequest() {
        String name = editTextName.getText().toString().trim();
        String guests = editTextGuests.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (name.isEmpty() || guests.isEmpty() || date.isEmpty() || time.isEmpty() || userId == null) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("restaurants").document(restaurantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String restaurantName = documentSnapshot.getString("name");
                        String ownerId = documentSnapshot.getString("ownerId");

                        Map<String, Object> reservation = new HashMap<>();
                        reservation.put("restaurantId", restaurantId);
                        reservation.put("restaurantName", restaurantName);
                        reservation.put("userId", userId);
                        reservation.put("customerName", name);
                        reservation.put("guestCount", guests);
                        reservation.put("date", date);
                        reservation.put("time", time);
                        reservation.put("status", "pending");
                        reservation.put("ownerId", ownerId); // THE CRITICAL FIX

                        db.collection("reservations")
                                .add(reservation)
                                .addOnSuccessListener(documentReference -> {
                                    // After creating the reservation, create a notification for the admin
                                    if (ownerId != null && !ownerId.isEmpty()) {
                                        Map<String, Object> notification = new HashMap<>();
                                        notification.put("userId", ownerId);
                                        notification.put("message", "You have a new reservation request from " + name + ".");
                                        notification.put("timestamp", com.google.firebase.Timestamp.now());

                                        db.collection("notifications").add(notification)
                                                .addOnSuccessListener(aVoid -> Log.d("ReservationActivity", "Notification sent to admin."))
                                                .addOnFailureListener(e -> Log.e("ReservationActivity", "Error sending notification.", e));
                                    }

                                    Toast.makeText(ReservationActivity.this, "Reservation request sent!", Toast.LENGTH_SHORT).show();
                                    Log.d("ReservationActivity", "Reservation request created with ID: " + documentReference.getId());
                                    finish(); // Go back to the previous screen
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ReservationActivity.this, "Failed to send request. Please try again.", Toast.LENGTH_SHORT).show();
                                    Log.e("ReservationActivity", "Error creating reservation", e);
                                });
                    } else {
                        Toast.makeText(this, "Error: Restaurant not found.", Toast.LENGTH_LONG).show();
                        Log.e("ReservationActivity", "Restaurant document with ID " + restaurantId + " not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get restaurant details. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("ReservationActivity", "Error getting restaurant document", e);
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
