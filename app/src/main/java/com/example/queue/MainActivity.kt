package com.example.queue
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 🔹 Test Firestore write
        val testData = hashMapOf("name" to "Juan", "createdAt" to System.currentTimeMillis())
        db.collection("testCollection").add(testData)
            .addOnSuccessListener { docRef ->
                Log.d("FirebaseTest", "Document written with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTest", "Error adding document", e)
            }
    }
}