package com.example.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AcceptedReservationAdapter(private val reservations: List<Reservation>) :
    RecyclerView.Adapter<AcceptedReservationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val customerName: TextView = view.findViewById(R.id.tvCustomerName)
        val partySize: TextView = view.findViewById(R.id.tvPartySize)
        val dateTime: TextView = view.findViewById(R.id.tvDateTime)
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
    }

    override fun getItemCount() = reservations.size
}
