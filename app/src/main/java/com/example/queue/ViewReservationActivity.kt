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
    private var adapter: AcceptedReservationAdapter? = null
    private lateinit var textViewNoReservations: TextView
    private val reservationList = mutableListOf<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reservation)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerViewReservations)
        textViewNoReservations = findViewById(R.id.textViewNoReservationsMessage)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // The adapter is initialized here but the list is populated later
        adapter = AcceptedReservationAdapter(reservationList, true)
        recyclerView.adapter = adapter
        
        fetchUserAndDefineQuery()
    }

    private fun fetchUserAndDefineQuery() {
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Log.e("ViewReservation", "CRITICAL: No user is currently logged in.")
            showError("You must be logged in to view reservations.")
            return
        }
        val userId = currentUser.uid
        Log.d("ViewReservation", "Current User ID: $userId")

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                if (!userDocument.exists()) {
                    Log.e("ViewReservation", "CRITICAL: User document does not exist for UID: $userId")
                    showError("Could not find user profile.")
                    return@addOnSuccessListener
                }

                val accountType = userDocument.getString("accountType")
                // THIS IS THE FIX: Check for "Restaurant" to identify an admin user.
                val isAdmin = accountType == "Restaurant"
                Log.d("ViewReservation", "User accountType is '$accountType'. isAdmin check is: $isAdmin")
                supportActionBar?.title = if (isAdmin) "Accepted Reservations" else "My Reservations"

                val query = if (isAdmin) {
                    Log.d("ViewReservation", "Querying as ADMIN. Searching for reservations with ownerId == $userId")
                    db.collection("reservations")
                        .whereEqualTo("ownerId", userId)
                        .whereEqualTo("status", "accepted")
                } else {
                    Log.d("ViewReservation", "Querying as CUSTOMER. Searching for reservations with userId == $userId")
                    db.collection("reservations")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("status", "accepted")
                }
                
                attachQueryListener(query)
            }
            .addOnFailureListener { exception ->
                Log.e("ViewReservation", "CRITICAL: Failed to get user document.", exception)
                showError("Error checking user role.")
            }
    }
    
    private fun attachQueryListener(query: Query) {
        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.e("ViewReservation", "Firestore listen failed.", e)
                showError("Error loading reservations.")
                return@addSnapshotListener
            }

            if (snapshots != null && !snapshots.isEmpty) {
                Log.d("ViewReservation", "Query successful. Found ${snapshots.size()} documents.")
                reservationList.clear()
                for (document in snapshots.documents) {
                    val reservation = document.toObject(Reservation::class.java)
                    if (reservation != null) {
                        reservation.id = document.id
                        reservationList.add(reservation)
                        Log.d("ViewReservation", "  - Added reservation: ${document.id} | Status: ${reservation.status}")
                    } else {
                        Log.w("ViewReservation", "  - Failed to parse document ${document.id} into Reservation object.")
                    }
                }
                adapter?.notifyDataSetChanged()
                textViewNoReservations.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                Log.w("ViewReservation", "Query returned no documents. Displaying 'no reservations' message.")
                reservationList.clear() // Clear the list for the "no reservations" case
                adapter?.notifyDataSetChanged() // Notify the adapter
                showError("No accepted reservations found.")
            }
        }
    }

    private fun showError(message: String) {
        reservationList.clear()
        adapter?.notifyDataSetChanged()
        textViewNoReservations.text = message
        textViewNoReservations.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
