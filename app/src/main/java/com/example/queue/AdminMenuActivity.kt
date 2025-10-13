package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        // Find the button in the XML
        val createRestaurantButton = findViewById<Button>(R.id.btnCreateRestaurant)

        // Set a click listener to start the CreateRestaurantActivity
        createRestaurantButton.setOnClickListener {
            val intent = Intent(this, CreateRestaurantActivity::class.java)
            startActivity(intent)
        }
    }
}
