package com.example.queue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheckInAdapter(
    private val reservations: MutableList<Reservation>,
    private val onCheckIn: (Reservation) -> Unit
) : RecyclerView.Adapter<CheckInAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.check_in_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation)
    }

    override fun getItemCount(): Int = reservations.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val restaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        private val reservationDetails: TextView = itemView.findViewById(R.id.tvReservationDetails)
        private val checkInButton: Button = itemView.findViewById(R.id.btnCheckIn)

        fun bind(reservation: Reservation) {
            restaurantName.text = reservation.restaurantName
            reservationDetails.text = "${reservation.date}, ${reservation.time} - ${reservation.guestCount} Guests"

            checkInButton.setOnClickListener {
                onCheckIn(reservation)
            }
        }
    }
}
