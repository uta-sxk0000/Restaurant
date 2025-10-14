package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnViewQueue: Button = findViewById(R.id.btnViewQueue)
        val btnProfile: Button = findViewById(R.id.btnProfile)

        // Open UserQueueActivity when "View Queue" is clicked
        btnViewQueue.setOnClickListener {
            val intent = Intent(this, UserQueueActivity::class.java)
            startActivity(intent)
        }

        // You can later replace this with a ProfileActivity
        btnProfile.setOnClickListener {
            // Example placeholder
            // val intent = Intent(this, ProfileActivity::class.java)
            // startActivity(intent)
        }
    }
}

