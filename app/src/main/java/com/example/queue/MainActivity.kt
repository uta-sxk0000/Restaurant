package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
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

        // UI references
        val emailEditText: EditText = findViewById(R.id.etEmail)
        val passwordEditText: EditText = findViewById(R.id.etPassword)
        val loginButton: MaterialButton = findViewById(R.id.btnLogin)


        // Login logic
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // get the user id from the database
                        val uid = auth.currentUser?.uid

                        // if the user exists (is not null)
                        if (uid != null) {

                            // get their account type from the database (user/admin)
                            db.collection("users").document(uid).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val accountType = document.getString("accountType")

                                        // Login successful
                                        Toast.makeText(
                                            this,
                                            "Login successful!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // redirect to the right menu depending on the account type
                                        if (accountType == "Restaurant") {
                                            startActivity(
                                                Intent(
                                                    this,
                                                    AdminMenuActivity::class.java
                                                )
                                            )
                                        } else {
                                            startActivity(
                                                Intent(
                                                    this,
                                                    MenuActivity::class.java
                                                )
                                            )
                                        }
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "User data not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Login", "Error: ", task.exception)
                    }
                }
        }
    }

    //Had to change the way the button was implemneted in order to fix a bug
    fun goToSignUp(view: View)
    {
        Toast.makeText(this, "Sign Up clicked", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, SingUpActivity::class.java))
    }
}

