package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        val btnReservations     = findViewById<Button>(R.id.btnReservations)
        val btnQueue            = findViewById<Button>(R.id.btnQueue)
        val btnWaitTime         = findViewById<Button>(R.id.btnWaitTime)
        val btnSettings         = findViewById<Button>(R.id.btnSettings)
        val btnCreateRestaurant = findViewById<Button>(R.id.btnCreateRestaurant)

        // Try several possible activity class names for Reservations (Java/Kotlin)
        btnReservations.setOnClickListener {
            val ok = startByName(
                "com.example.queue.admin_queue",
                "com.example.queue.AdminQueueActivity",
                "com.example.queue.ManageReservationActivity"
            )
            if (!ok) toast("Reservations screen not ready.")
        }

        // Placeholders (UI feels complete; no crashes)
        btnQueue.setOnClickListener    { toast("Queue coming soon.") }
        btnWaitTime.setOnClickListener { toast("Wait time coming soon.") }
        btnSettings.setOnClickListener { toast("Settings coming soon.") }

        // Create Restaurant (existing screen)
        btnCreateRestaurant.setOnClickListener {
            val ok = startByName("com.example.queue.CreateRestaurantActivity")
            if (!ok) toast("Create Restaurant screen not found.")
        }
    }

    private fun startByName(vararg names: String): Boolean {
        for (n in names) {
            try {
                val clazz = Class.forName(n)
                startActivity(Intent(this, clazz))
                return true
            } catch (_: ClassNotFoundException) { /* try next */ }
        }
        return false
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
