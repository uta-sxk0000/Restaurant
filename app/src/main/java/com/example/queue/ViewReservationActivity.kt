package com.example.queue

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewReservationActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AcceptedReservationAdapter
    private lateinit var textViewNoReservations: TextView
    private val reservationList = mutableListOf<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reservation)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Accepted Reservations"

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerViewReservations)
        textViewNoReservations = findViewById(R.id.textViewNoReservationsMessage)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AcceptedReservationAdapter(reservationList)
        recyclerView.adapter = adapter

        fetchAcceptedReservations()
    }

    private fun fetchAcceptedReservations() {
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Log.e("ViewReservation", "No user logged in")
            textViewNoReservations.text = "You must be logged in to view reservations."
            textViewNoReservations.visibility = View.VISIBLE
            return
        }
        val userId = currentUser.uid

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val isAdmin = userDocument.getBoolean("isAdmin") ?: false
                
                var query: Query = db.collection("reservations")
                    .whereEqualTo("status", "accepted")

                if (!isAdmin) {
                    query = query.whereEqualTo("userId", userId)
                }

                query.get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Log.d("ViewReservation", "No accepted reservations found.")
                            textViewNoReservations.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            reservationList.clear()
                            for (document in documents) {
                                val reservation = document.toObject(Reservation::class.java)
                                reservationList.add(reservation)
                            }
                            adapter.notifyDataSetChanged()
                            textViewNoReservations.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ViewReservation", "Error getting reservations: ", exception)
                        textViewNoReservations.text = "Error loading reservations."
                        textViewNoReservations.visibility = View.VISIBLE
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("ViewReservation", "Error checking admin status: ", exception)
                textViewNoReservations.text = "Error checking user role."
                textViewNoReservations.visibility = View.VISIBLE
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
