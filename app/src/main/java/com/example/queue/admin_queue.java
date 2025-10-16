package com.example.queue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class admin_queue extends AppCompatActivity {

    private Spinner spinnerAdminRestaurants;
    private RecyclerView rvQueue;
    private QueueAdapter adapter;

    private FirebaseFirestore db;
    private List<QueueEntry> queueList = new ArrayList<>();
    private List<String> restaurantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_queue);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerAdminRestaurants = findViewById(R.id.spinnerAdminRestaurants);
        rvQueue = findViewById(R.id.rvQueue);

        db = FirebaseFirestore.getInstance();

        setupRecyclerView();
        fetchRestaurants();

        spinnerAdminRestaurants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRestaurant = parent.getItemAtPosition(position).toString();
                if (!selectedRestaurant.equals("Select Restaurant")) {
                    listenForQueueUpdates(selectedRestaurant);
                } else {
                    queueList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new QueueAdapter(queueList);
        rvQueue.setLayoutManager(new LinearLayoutManager(this));
        rvQueue.setAdapter(adapter);
    }

    private void fetchRestaurants() {
        restaurantList.add("Select Restaurant");
        db.collection("restaurants")
                .get()
                .addOnSuccessListener(documents -> {
                    for (QueryDocumentSnapshot document : documents) {
                        String restaurantName = document.getString("name");
                        if (restaurantName != null) {
                            restaurantList.add(restaurantName);
                        }
                    }
                    ArrayAdapter<String> restaurantAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, restaurantList);
                    spinnerAdminRestaurants.setAdapter(restaurantAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(admin_queue.this, "Failed to fetch restaurants.", Toast.LENGTH_SHORT).show());
    }

    private void listenForQueueUpdates(String restaurantName) {
        db.collection("queue_entries")
                .whereEqualTo("restaurantName", restaurantName)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(admin_queue.this, "Error listening for queue updates.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        queueList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            QueueEntry entry = doc.toObject(QueueEntry.class);
                            entry.setId(doc.getId()); // Store document ID for deletion
                            queueList.add(entry);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    // RecyclerView Adapter
    class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder> {

        private List<QueueEntry> items;

        QueueAdapter(List<QueueEntry> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue_entry, parent, false);
            return new QueueViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QueueViewHolder holder, int position) {
            QueueEntry entry = items.get(position);
            holder.bind(entry);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class QueueViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvWaitTime;
            Button btnRemove;

            QueueViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvQueueItemName);
                tvWaitTime = itemView.findViewById(R.id.tvQueueItemWaitTime);
                btnRemove = itemView.findViewById(R.id.btnRemoveFromQueue);
            }

            void bind(QueueEntry entry) {
                tvName.setText(String.format("%s (Party of %d)", entry.getUserName(), entry.getPartySize()));

                long waitTimeMinutes = 0;
                if (entry.getTimestamp() != null) {
                    long diff = new Date().getTime() - entry.getTimestamp().toDate().getTime();
                    waitTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                }
                tvWaitTime.setText(String.format("Waiting for %d minutes", waitTimeMinutes));

                btnRemove.setOnClickListener(v -> {
                    db.collection("queue_entries").document(entry.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(admin_queue.this, "Removed " + entry.getUserName(), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(admin_queue.this, "Failed to remove.", Toast.LENGTH_SHORT).show());
                });
            }
        }
    }

    // Data model class for a queue entry
    public static class QueueEntry {
        private String id;
        private String userName;
        private String restaurantName;
        private int partySize;
        private Timestamp timestamp;

        public QueueEntry() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUserName() { return userName; }
        public String getRestaurantName() { return restaurantName; }
        public int getPartySize() { return partySize; }
        public Timestamp getTimestamp() { return timestamp; }
    }
}