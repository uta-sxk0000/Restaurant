package com.example.queue

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateRestaurantActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_restaurant)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val nameField = findViewById<EditText>(R.id.etRestaurantName)
        val locationField = findViewById<EditText>(R.id.etRestaurantLocation)
        val registerButton = findViewById<Button>(R.id.btnRegisterRestaurant)

        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val location = locationField.text.toString().trim()
            val uid = auth.currentUser?.uid ?: return@setOnClickListener

            if (name.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val restaurantData = hashMapOf(
                "name" to name,
                "location" to location,
                "ownerId" to uid
            )

            db.collection("restaurants")
                .add(restaurantData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Restaurant registered!", Toast.LENGTH_SHORT).show()
                    finish() // Go back to AdminMenuActivity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}