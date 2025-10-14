package com.example.queue

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UserQueueActivity : AppCompatActivity() {

    private lateinit var spinnerRestaurants: Spinner
    private lateinit var etName: EditText
    private lateinit var etPartySize: EditText
    private lateinit var btnJoinQueue: Button

    // Add a Firestore instance
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_queue)

        spinnerRestaurants = findViewById(R.id.spinnerRestaurants)
        etName = findViewById(R.id.etName)
        etPartySize = findViewById(R.id.etPartySize)
        btnJoinQueue = findViewById(R.id.btnJoinQueue)

        // Fetch restaurants from Firestore
        fetchRestaurants()

        btnJoinQueue.setOnClickListener {
            val restaurant = spinnerRestaurants.selectedItem.toString()
            val userName = etName.text.toString().trim()
            val partySize = etPartySize.text.toString().toIntOrNull() ?: 1

            if (restaurant == "Select Restaurant" || userName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Add logic to save the queue entry to Firestore
            Toast.makeText(this, "Joined queue for $restaurant (Party of $partySize)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRestaurants() {
        val restaurantList = mutableListOf("Select Restaurant")
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Assuming the field in your document is named "name"
                    val restaurantName = document.getString("name")
                    if (restaurantName != null) {
                        restaurantList.add(restaurantName)
                    }
                }
                // Populate the spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, restaurantList)
                spinnerRestaurants.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting restaurants: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
