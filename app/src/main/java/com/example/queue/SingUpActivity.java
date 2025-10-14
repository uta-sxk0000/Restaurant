package com.example.queue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText emailEditText;
    private EditText passwordEditText;
    private RadioGroup accountTypeRadioGroup;
    private Button signUpButton;
    private Button goToReservationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Link XML views
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        accountTypeRadioGroup = findViewById(R.id.radioGroupAccountType);
        signUpButton = findViewById(R.id.buttonSignUp);
        goToReservationButton = findViewById(R.id.buttonGoToReservation);

        // Sign Up button logic
        signUpButton.setOnClickListener(v -> signUpUser());

        // Go to Reservation button logic
        goToReservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(SingUpActivity.this, ReservationActivity.class);
            startActivity(intent);
        });
    }

    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        int selectedId = accountTypeRadioGroup.getCheckedRadioButtonId();

        String accountType = (selectedId == R.id.radioRestaurant) ? "Restaurant" : "User";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SingUpActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        //Create Firebase user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        // Save account type to Firestore
                        saveAccountTypeToFirestore(uid, accountType);

                        Toast.makeText(SingUpActivity.this, "Account created as " + accountType, Toast.LENGTH_SHORT).show();

                        //After successful signup, go back to Login (MainActivity)
                        Intent intent = new Intent(SingUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(SingUpActivity.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveAccountTypeToFirestore(String uid, String accountType) {
        Map<String, Object> user = new HashMap<>();
        user.put("accountType", accountType);

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("SignUp", "Account type saved"))
                .addOnFailureListener(e -> Log.e("SignUp", "Error saving account type", e));
    }
}

