package com.example.queue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ReservationRequestAdapter extends FirestoreRecyclerAdapter<Reservation, ReservationRequestAdapter.ReservationRequestViewHolder> {

    public ReservationRequestAdapter(@NonNull FirestoreRecyclerOptions<Reservation> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReservationRequestViewHolder holder, int position, @NonNull Reservation model) {
        holder.tvRestaurantName.setText(model.getRestaurantName());
        holder.tvCustomerName.setText(model.getCustomerName());
        holder.tvPartySize.setText("Party Size: " + model.getGuestCount());
        holder.tvDate.setText("Date: " + model.getDate());
        holder.tvTime.setText("Time: " + model.getTime());

        holder.acceptButton.setOnClickListener(v -> {
            getSnapshots().getSnapshot(position).getReference().update("status", "accepted");
        });

        holder.rejectButton.setOnClickListener(v -> {
            getSnapshots().getSnapshot(position).getReference().update("status", "rejected");
        });
    }

    @NonNull
    @Override
    public ReservationRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_reservation_request, parent, false);
        return new ReservationRequestViewHolder(view);
    }

    class ReservationRequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvRestaurantName, tvCustomerName, tvPartySize, tvDate, tvTime;
        Button acceptButton, rejectButton;

        public ReservationRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPartySize = itemView.findViewById(R.id.tvPartySize);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            acceptButton = itemView.findViewById(R.id.btnAccept);
            rejectButton = itemView.findViewById(R.id.btnReject);
        }
    }
}
