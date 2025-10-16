package com.example.queue

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CheckInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)
        title = getString(R.string.check_in)

        // Enable the back arrow in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnArrived: Button = findViewById(R.id.btnArrived)
        btnArrived.setOnClickListener {
            Toast.makeText(this, "You're checked in. Please wait to be called.", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the back arrow click
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close the current activity and go back
        return true
    }
}
