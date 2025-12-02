package com.example.queue

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CheckInActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoReservations: TextView
    private lateinit var adapter: CheckInAdapter
    private val reservationList = mutableListOf<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        tvNoReservations = findViewById(R.id.tvNoReservations)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CheckInAdapter(reservationList) { reservation ->
            performCheckIn(reservation)
        }
        recyclerView.adapter = adapter

        fetchAcceptedReservations()
    }

    private fun fetchAcceptedReservations() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("reservations")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    tvNoReservations.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    reservationList.clear()
                    for (document in documents) {
                        val reservation = document.toObject(Reservation::class.java)
                        reservation.id = document.id // Use the 'id' field
                        reservationList.add(reservation)
                    }
                    adapter.notifyDataSetChanged()
                    tvNoReservations.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                Log.w("CheckInActivity", "Error getting documents: ", exception)
                tvNoReservations.text = "Error fetching reservations."
                tvNoReservations.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
    }

    private fun performCheckIn(reservation: Reservation) {
        if (reservation.id.isNullOrEmpty()) {
            Log.e("CheckInActivity", "Reservation document ID is null or empty!")
            return
        }

        val reservationRef = db.collection("reservations").document(reservation.id!!)

        reservationRef.update("status", "checked-in")
            .addOnSuccessListener {
                Log.d("CheckInActivity", "Reservation ${reservation.id} checked in.")

                val notification = hashMapOf(
                    "userId" to reservation.ownerId,
                    "title" to "Guest Checked In!",
                    "message" to "${reservation.customerName} has checked in for their reservation.",
                    "timestamp" to FieldValue.serverTimestamp()
                )
                db.collection("notifications").add(notification)

                val position = reservationList.indexOf(reservation)
                if (position != -1) {
                    reservationList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    if (reservationList.isEmpty()) {
                        tvNoReservations.visibility = View.VISIBLE
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("CheckInActivity", "Error updating reservation", e)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
