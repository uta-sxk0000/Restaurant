package com.example.queue;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SelectRestaurantActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_restaurant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select a Restaurant");
        }

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = db.collection("restaurants").orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Restaurant> options = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class)
                .build();

        adapter = new RestaurantAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.restaurantsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String documentId) {
                Intent intent = new Intent(SelectRestaurantActivity.this, ReservationActivity.class);
                intent.putExtra("restaurantId", documentId);
                startActivity(intent);
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
