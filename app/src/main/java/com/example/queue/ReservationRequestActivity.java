package com.example.queue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReservationRequestActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ReservationRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_request);

        setUpRecyclerView();

        Button backButton = findViewById(R.id.backToAdminMenuButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReservationRequestActivity.this, AdminMenuActivity.class));
                finish();
            }
        });
    }

    private void setUpRecyclerView() {
        Query query = db.collection("reservations").whereEqualTo("status", "pending").orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Reservation> options = new FirestoreRecyclerOptions.Builder<Reservation>()
                .setQuery(query, Reservation.class)
                .build();

        adapter = new ReservationRequestAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.requestsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
