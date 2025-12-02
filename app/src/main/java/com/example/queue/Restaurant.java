package com.example.queue;

public class Restaurant {
    private String name;
    private String address;
    private String ownerId;

    // Firestore requires an empty constructor
    public Restaurant() {}

    public Restaurant(String name, String address, String ownerId) {
        this.name = name;
        this.address = address;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
