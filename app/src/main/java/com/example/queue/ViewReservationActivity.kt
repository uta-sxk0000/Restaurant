package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewReservationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reservation)

        // Enable toolbar back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val layoutInfo = findViewById<LinearLayout>(R.id.layoutReservationInfo)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)

        val tvRestaurant = findViewById<TextView>(R.id.tvRestaurant)
        val tvDate = findViewById<TextView>(R.id.tvDateR)
        val tvTime = findViewById<TextView>(R.id.tvTimeR)
        val tvGuests = findViewById<TextView>(R.id.tvGuestsR)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvPosition = findViewById<TextView>(R.id.tvPosition)
        val tvWaitTime = findViewById<TextView>(R.id.tvWaitTime)

        val userId = auth.currentUser?.uid ?: return

        // Load reservation from Firestore
        db.collection("reservations").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    layoutInfo.visibility = LinearLayout.VISIBLE
                    tvEmpty.visibility = TextView.GONE

                    tvRestaurant.text = "Restaurant: ${doc.getString("restaurant") ?: "-"}"
                    tvDate.text = "Date: ${doc.getString("date") ?: "-"}"
                    tvTime.text = "Time: ${doc.getString("time") ?: "-"}"
                    tvGuests.text = "Guests: ${doc.get("guests") ?: "-"}"
                    tvStatus.text = "Status: ${doc.getString("status") ?: "Confirmed"}"
                    tvPosition.text = "Position: ${doc.get("position") ?: "-"}"
                    tvWaitTime.text = "Estimated Time: ${doc.get("waitTime") ?: "-"} minutes"
                } else {
                    layoutInfo.visibility = LinearLayout.GONE
                    tvEmpty.visibility = TextView.VISIBLE
                }
            }
    }

    // Handle toolbar back arrow
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // Handle action bar item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
