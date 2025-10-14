package com.example.queue

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class UserQueueActivity : AppCompatActivity() {

    private lateinit var spinnerRestaurants: Spinner
    private lateinit var etName: EditText
    private lateinit var etPartySize: EditText
    private lateinit var btnJoinQueue: Button

    // Layouts for swapping visibility
    private lateinit var layoutJoinQueue: LinearLayout
    private lateinit var layoutQueueStatus: LinearLayout

    // Views for displaying queue status
    private lateinit var tvQueueName: TextView
    private lateinit var tvQueuePosition: TextView
    private lateinit var tvEstimatedTime: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_queue)

        // Initialize all views
        spinnerRestaurants = findViewById(R.id.spinnerRestaurants)
        etName = findViewById(R.id.etName)
        etPartySize = findViewById(R.id.etPartySize)
        btnJoinQueue = findViewById(R.id.btnJoinQueue)

        layoutJoinQueue = findViewById(R.id.layoutJoinQueue)
        layoutQueueStatus = findViewById(R.id.layoutQueueStatus)
        tvQueueName = findViewById(R.id.tvQueueName)
        tvQueuePosition = findViewById(R.id.tvQueuePosition)
        tvEstimatedTime = findViewById(R.id.tvEstimatedTime)


        fetchRestaurants()

        btnJoinQueue.setOnClickListener {
            joinQueue()
        }
    }

    private fun fetchRestaurants() {
        val restaurantList = mutableListOf("Select Restaurant")
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val restaurantName = document.getString("name")
                    if (restaurantName != null) {
                        restaurantList.add(restaurantName)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, restaurantList)
                spinnerRestaurants.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting restaurants: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun joinQueue() {
        val restaurant = spinnerRestaurants.selectedItem.toString()
        val userName = etName.text.toString().trim()
        val partySize = etPartySize.text.toString().toIntOrNull() ?: 1

        if (restaurant == "Select Restaurant" || userName.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new queue entry object
        val queueEntry = hashMapOf(
            "restaurantName" to restaurant,
            "userName" to userName,
            "partySize" to partySize,
            "timestamp" to FieldValue.serverTimestamp()
        )

        // Add a new document with a generated ID
        db.collection("queue_entries")
            .add(queueEntry)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Successfully joined queue!", Toast.LENGTH_SHORT).show()
                showQueueStatus(userName, restaurant, documentReference.id)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error joining queue: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showQueueStatus(userName: String, restaurantName: String, userId: String) {
        // Swap UI from form to status
        layoutJoinQueue.visibility = View.GONE
        layoutQueueStatus.visibility = View.VISIBLE

        tvQueueName.text = "Hi, $userName!"

        // Listen for real-time updates to the queue
        listenForQueuePosition(restaurantName, userId)
    }

    private fun listenForQueuePosition(restaurantName: String, userId: String) {
        db.collection("queue_entries")
            .whereEqualTo("restaurantName", restaurantName)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    tvQueuePosition.text = "Position: Error"
                    return@addSnapshotListener
                }
                
                if (snapshots == null) return@addSnapshotListener

                // Manually sort the documents by timestamp on the client side.
                // This avoids needing a composite index in Firestore.
                val sortedDocuments = snapshots.documents.sortedBy { doc ->
                    doc.getTimestamp("timestamp")
                }

                val userPosition = sortedDocuments.indexOfFirst { it.id == userId }

                if (userPosition != -1) {
                    tvQueuePosition.text = "Position: ${userPosition + 1}"
                    tvEstimatedTime.text = "Estimated Time: 0 minutes" // Placeholder
                } else {
                    // This can happen briefly before the user's document is available in the snapshot.
                    tvQueuePosition.text = "Position: Calculating..."
                    tvEstimatedTime.text = ""
                }
            }
    }
}
