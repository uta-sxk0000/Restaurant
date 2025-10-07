package com.example.queue

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnViewQueue: Button = findViewById(R.id.btnViewQueue)
        btnViewQueue.setOnClickListener {
            Toast.makeText(this, "View Queue clicked", Toast.LENGTH_SHORT).show()
        }

        val btnProfile: Button = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
