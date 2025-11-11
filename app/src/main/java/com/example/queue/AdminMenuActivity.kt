package com.example.queue

//android imports give us intent navigation, lifecycle hooks, basic widgets, and toast popups
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth //for sign out

class AdminMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        //grab each button from the layout so we can attach actions
        val btnReservations     = findViewById<Button>(R.id.btnReservations)
        val btnQueue            = findViewById<Button>(R.id.btnQueue)
        val btnWaitTime         = findViewById<Button>(R.id.btnWaitTime)
        val btnSettings         = findViewById<Button>(R.id.btnSettings)
        val btnCreateRestaurant = findViewById<Button>(R.id.btnCreateRestaurant)

        //reservations tries a few possible screen names because our project has mixed names in places
        //this keeps the app from crashing if one class is not there yet
        btnReservations.setOnClickListener {
            val ok = startByName(
                "com.example.queue.admin_queue",
                "com.example.queue.AdminQueueActivity",
                "com.example.queue.ManageReservationActivity"
            )
            if (!ok) toast("Reservations screen not ready.")
        }

        //queue opens the existing admin queue screen
        btnQueue.setOnClickListener {
            startActivity(Intent(this, com.example.queue.admin_queue::class.java))
        }

        //wait time is a placeholder for now so the ui feels complete without errors
        btnWaitTime.setOnClickListener { toast("Wait time coming soon.") }

        //settings acts as logout here to keep the demo simple and predictable for the walkthrough
        btnSettings.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        //create restaurant opens the screen if present otherwise a friendly message
        btnCreateRestaurant.setOnClickListener {
            val ok = startByName("com.example.queue.CreateRestaurantActivity")
            if (!ok) toast("Create Restaurant screen not found.")
        }
    }

    //helper function tries to open the first class name that exists
    //this pattern is a simple idea you see online a lot and it keeps the ui from crashing while we iterate
    private fun startByName(vararg names: String): Boolean {
        for (n in names) {
            try {
                val clazz = Class.forName(n) //load the class by its full name if it exists
                startActivity(Intent(this, clazz))
                return true
            } catch (_: ClassNotFoundException) { /*try next name*/ }
        }
        return false
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
