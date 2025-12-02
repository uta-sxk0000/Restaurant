package com.example.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AcceptedReservationAdapter(
    private val reservations: List<Reservation>,
    private val isAdmin: Boolean // To control button visibility
) : RecyclerView.Adapter<AcceptedReservationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val customerName: TextView = view.findViewById(R.id.tvCustomerName)
        val partySize: TextView = view.findViewById(R.id.tvPartySize)
        val dateTime: TextView = view.findViewById(R.id.tvDateTime)
        val checkInButton: Button = view.findViewById(R.id.btnCheckIn) // The new button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_accepted_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]

        holder.restaurantName.text = reservation.restaurantName
        holder.customerName.text = "Customer: ${reservation.customerName}"
        holder.partySize.text = "Party Size: ${reservation.guestCount}"
        holder.dateTime.text = "${reservation.date} at ${reservation.time}"

        // --- This is the new logic ---
        if (!isAdmin) {
            // If the user is NOT an admin, show the check-in button
            holder.checkInButton.visibility = View.VISIBLE
            holder.checkInButton.setOnClickListener {
                // Update the reservation status to 'checked-in' in Firestore
                val db = FirebaseFirestore.getInstance()
                db.collection("reservations").document(reservation.id)
                    .update("status", "checked-in")
                    // You can add success/failure listeners here if needed
            }
        } else {
            // If the user IS an admin, the button remains hidden (its default state)
            holder.checkInButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = reservations.size
}
