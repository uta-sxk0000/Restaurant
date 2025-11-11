package com.example.queue;

//imports for navigation, lifecycle, logs, inputs, and small popup messages
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//firebase imports to create the account and save a simple profile field
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity {

    //firebase tools
    //firebase authentication creates the user
    //firestore stores a tiny profile field for account type so we can route later
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //views from the xml layout
    private EditText emailEditText;
    private EditText passwordEditText;
    private RadioGroup accountTypeRadioGroup;
    private Button signUpButton;
    private Button goToReservationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        //get the firebase instances we will use on this screen
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //connect code to the xml views by id so we can read values and handle clicks
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        accountTypeRadioGroup = findViewById(R.id.radioGroupAccountType);
        signUpButton = findViewById(R.id.buttonSignUp);
        goToReservationButton = findViewById(R.id.buttonGoToReservation);

        //sign up button listens for a tap and then runs signUpUser
        signUpButton.setOnClickListener(v -> signUpUser());

        //optional shortcut used during testing to jump straight to reservation flow
        goToReservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(SingUpActivity.this, ReservationActivity.class);
            startActivity(intent);
        });
    }

    //reads the email and password, figures out the selected account type, then asks firebase to create the user
    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        int selectedId = accountTypeRadioGroup.getCheckedRadioButtonId();

        //if restaurant is selected we store restaurant otherwise we store user as the default
        String accountType = (selectedId == R.id.radioRestaurant) ? "Restaurant" : "User";

        //simple non empty check here and let firebase enforce stronger rules server side
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SingUpActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        //create the firebase authentication user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid(); //unique id that firebase gives this user

                        //save the account type under users/uid so the app can route to admin or user later
                        saveAccountTypeToFirestore(uid, accountType);

                        Toast.makeText(SingUpActivity.this, "Account created as " + accountType, Toast.LENGTH_SHORT).show();

                        //send the user back to login to sign in with the new account
                        Intent intent = new Intent(SingUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(SingUpActivity.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //writes a small document like { accountType: "User" } to firestore at users/uid
    private void saveAccountTypeToFirestore(String uid, String accountType) {
        Map<String, Object> user = new HashMap<>();
        user.put("accountType", accountType);

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("SignUp", "Account type saved"))
                .addOnFailureListener(e -> Log.e("SignUp", "Error saving account type", e));
    }
}
