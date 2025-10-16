package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

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
            val intent = Intent(this, CheckInActivity::class.java)
            startActivity(intent)
        }

        btnMakeReservation.setOnClickListener {
            val intent = Intent(this, ReservationActivity::class.java)
            startActivity(intent)
        }

        // Open UserQueueActivity when "Join Queue" is clicked
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

        // This button is now for logging out
        btnUserMenu.setOnClickListener {
            // Log out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Go back to the login screen (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            // Clear the back stack so the user can't navigate back to the menu
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close the current activity
        }

    }
}
