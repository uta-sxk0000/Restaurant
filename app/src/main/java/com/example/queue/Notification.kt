package com.example.queue

import com.google.firebase.Timestamp

data class Notification(
    var id: String = "",
    val userId: String = "",
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
