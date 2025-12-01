package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckInActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val tvRestaurant = findViewById<TextView>(R.id.tvRestaurantName)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val tvGuests = findViewById<TextView>(R.id.tvGuests)
        val btnCheckIn = findViewById<Button>(R.id.btnCheckIn)

        btnCheckIn.text = "CHECK IN"

        val userId = auth.currentUser?.uid ?: return

        db.collection("reservations").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    tvRestaurant.text = "Restaurant: ${doc.getString("restaurant") ?: "-"}"
                    tvDate.text = "Date: ${doc.getString("date") ?: "-"}"
                    tvTime.text = "Time: ${doc.getString("time") ?: "-"}"
                    tvGuests.text = "Guests: ${doc.get("guests") ?: "-"}"
                }
            }

        btnCheckIn.setOnClickListener {
            val intent = Intent(this, ViewReservationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
