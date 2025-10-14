package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_menu)

        val btnCheckIn: Button = findViewById(R.id.btnCheckIn)
        val btnMakeReservation: Button = findViewById(R.id.btnMakeReservation)
        val btnViewStatus: Button = findViewById(R.id.btnViewStatus)
        val btnViewReservation: Button = findViewById(R.id.btnViewReservation)
        val btnNotifications: Button = findViewById(R.id.btnNotifications)
        val btnUserMenu: Button = findViewById(R.id.btnUserMenu)


        // ====== Example click listeners ======
        btnCheckIn.setOnClickListener {
            Toast.makeText(this, "Check In clicked", Toast.LENGTH_SHORT).show()
            // TODO: Open CheckInActivity
        }

        btnMakeReservation.setOnClickListener {
            Toast.makeText(this, "Make Reservation clicked", Toast.LENGTH_SHORT).show()
            // TODO: Open MakeReservationActivity
        }

        // Open UserQueueActivity when "View Status" is clicked
        btnViewStatus.setOnClickListener {
            val intent = Intent(this, UserQueueActivity::class.java)
            startActivity(intent)
        }


        btnViewReservation.setOnClickListener {
            Toast.makeText(this, "View Reservation clicked", Toast.LENGTH_SHORT).show()
            // TODO: Open ViewReservationActivity
        }

        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            // TODO: Open NotificationsActivity
        }

        btnUserMenu.setOnClickListener {
            Toast.makeText(this, "User Menu clicked", Toast.LENGTH_SHORT).show()
            // TODO: Open UserMenuActivity or show menu options
            // Example placeholder
            // val intent = Intent(this, ProfileActivity::class.java)
            // startActivity(intent)
        }

    }
}

