package com.example.queue;

import java.util.ArrayList;

/**
 * This class represents a temporary, in-memory queue for customers.
 * This is a placeholder and has major limitations
 * This file has been fixed to resolve compilation errors, but it needs to be
 * replaced with a proper Firestore implementation to work correctly.
 */
public class admin_queue {
    // This list only exists in memory. It is not connected to a database.
    private static ArrayList<Customer> queue = new ArrayList<>();

    /*
     * This method was intended to add customers to the queue.
     * It is not connected to the user interface or the database.
     */
    public static void addCustomerAutomatically(String customerInfo) {
        queue.add(new Customer(customerInfo));
        System.out.println("Customer added: " + customerInfo);
    }

    /**
     * Prints the current state of the in-memory queue to the system console.
     * This is for debugging only and is not visible to the admin in the app UI.
     */
    public static void showQueue() {
        if (queue.isEmpty()) {
            System.out.println("The queue is currently empty.");
            return;
        }

        System.out.println("--- Current Queue Status ---");
        for (int i = 0; i < queue.size(); i++) {
            Customer customer = queue.get(i);
            // Example output: "1. Info: John Doe, Wait: 5 minutes"
            System.out.println((i + 1) + ". Info: " + customer.getInfo()
                    + ", Wait: " + customer.getMinutesInQueue() + " minutes");
        }
        System.out.println("--------------------------");
    }

    /**
     *  represent a customer in the temporary queue.
     */
    static class Customer {
        private long joinTime;
        private String info;

        public Customer(String info) {
            this.info = info;
            this.joinTime = System.currentTimeMillis();
        }

        public String getInfo() {
            return info;
        }

        public long getMinutesInQueue() {
            // Converts the wait time from milliseconds to minutes.
            return (System.currentTimeMillis() - joinTime) / 60000;
        }
    }
}
