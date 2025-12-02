package com.example.queue;

public class Reservation {

    public String id; // Public field to store the document ID

    // Fields must be public or have public getters to be used by Firestore
    private String restaurantName;
    private String customerName;
    private String guestCount;
    private String date;
    private String time;
    private String status;
    private String userId;
    private String ownerId; // <-- ADDED THIS FIELD

    public Reservation() {
        // Public no-arg constructor required by Firebase
    }

    // --- Getters ---
    // The FirestoreRecyclerAdapter uses these to get the data for each reservation

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getGuestCount() {
        return guestCount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public String getOwnerId() { // <-- ADDED THIS GETTER
        return ownerId;
    }
}
